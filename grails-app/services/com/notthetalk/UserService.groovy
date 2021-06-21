/*
 * This file is part of the NOTtheTalk distribution (https://github.com/jdudmesh/notthetalk).
 * Copyright (c) 2011-2021 John Dudmesh.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.notthetalk

import grails.util.Environment
import org.hibernate.persister.collection.AbstractCollectionPersister
import org.hibernate.persister.entity.EntityPersister
import redis.clients.jedis.Jedis

class UserService {

	def ONE_HOUR = 3600
    static transactional = true

	def springSecurityService
	def redisService

	def sessionFactory


    def saveUser(user, params, recaptchaOK, ip) {

		user.createdDate = new Date()
		user.lastUpdated = user.createdDate
		user.username = params.username
		user.password = springSecurityService.encodePassword(params.password.encodeAsSanitizedMarkup())
		user.email = params.email
		user.bio = ""
		user.enabled = false
		user.accountExpired = false
		user.accountLocked = false
		user.passwordExpired = false


		if(!user.options) {
			user.options = new UserOptions(user: user, autoSubs: false)
			user.options.watch = true
		}

		if(!(params.password ==~ /.{6,12}/))
		{
			user.errors.rejectValue "password", "user.password.wronglength"
		}

		if(params.password != params.passwordConfirm)
		{
			user.errors.rejectValue "password", "user.password.doesnotmatch"
		}

		if(!params.checkBoxAccept)
		{
			user.errors.rejectValue "username", "user.username.acceptterms"
		}

		if(!recaptchaOK)
		{
			user.errors.rejectValue "username", "user.username.captcha"
		}

		user.validate()

		def userOk = !user.hasErrors()

		if(userOk && recaptchaOK && user.save()) {

			UserRole.create(user, Role.findByAuthority('ROLE_USER'), true)

			SignupConfirmation conf = new SignupConfirmation(user)
			conf.save()

			sendMail {
				to user.email
				from "help@justthetalk.com"
				bcc "admins@justthetalk.com"
				subject "Signup Confirmation"
				html (view: "/common/signup-confirmation-mail", model: [user:user, conf:conf])
			}

			UserHistory.CreateUserHistoryLocation(user, "SIGNUP", ip)

			return true

		}
		else {
			return false
		}

    }

	def getSockpuppets(user) {

		def session = sessionFactory.getCurrentSession()

		def sockpuppets = session.createSQLQuery("{call GetUserSockpuppets(?)}")
			.addScalar("ip_address", org.hibernate.Hibernate.STRING)
			.addScalar("geo_location", org.hibernate.Hibernate.STRING)
			.addScalar("username", org.hibernate.Hibernate.STRING)
			.addScalar("sockpuppet", org.hibernate.Hibernate.STRING)
			.addScalar("last_login", org.hibernate.Hibernate.DATE)
			.setLong(0, user.id)
			.setResultTransformer(new SockPuppetTransformer())
			.list()

		//.setResultTransformer(new SockPuppetTransformer());
		//sockpuppets.setLong(0, user.id)

		return (List<SockPuppet>)sockpuppets

		/*
			select l1.ip_address, l1.geo_location, u1.username, u2.username, l2.last_login
			from user_login_location l1
			inner join user_login_location l2
			on l1.ip_address = l2.ip_address
			inner join user u1
			on l1.user_id = u1.id
			inner join user u2
			on l2.user_id = u2.id
			where l1.user_id != l2.user_id
			and l1.user_id = :user_id
			order by u1.username, u2.username
		*/

	}

	def ignoreUser(user, ignoredUser, ignore) {

		if(ignore) {
			def alreadyIgnored = IgnoreUser.findByUserAndIgnoredUser(user, ignoredUser)
			if(!alreadyIgnored) {
				def obj = new IgnoreUser(user: user, ignoredUser: ignoredUser)
				obj.validate()

				if(!obj.hasErrors()) {
					obj.save(flush: true, failOnError: true)
				}
			}
		}
		else {
			def ignored = IgnoreUser.findByUserAndIgnoredUser(user, ignoredUser)
			if(ignored) {
				ignored.delete()
			}
		}

		redisService.withRedis { Jedis redis ->
			def key = "user:$user.id:ignoredUser:$ignoredUser.id"
			redis.setex(key, ONE_HOUR, ignore as String)
		}

	}

	def userIgnoresUserId(user, ignoredUserId) {

		def ignore = redisService.memoize("user:$user.id:ignoredUser:$ignoredUserId", [expire: ONE_HOUR]) {
			def ignoredUser = User.get(ignoredUserId)
			def obj = IgnoreUser.findByUserAndIgnoredUser(user, ignoredUser)
			if(obj) {
				return "true"
			}
			else {
				return "false"
			}
		}

		return ignore == "true" ? true : false
	}

	def createUserHistory(userId, ip, location) {

		try {

			def user = User.get(userId)
			def loginDate = new Date()

			def locationHistory = new UserLoginLocation(user: user, ipAddress: ip, geoLocation:location, lastLogin:loginDate)
			locationHistory.save(flush:true)

		}
		catch(Exception ex) {
			log?.error "That annoying createUserHistory optimistic locking failure"
		}

	}

	def getUsername(userId) {

		def username = "Unknown User"

		username = redisService.memoize("user:name:$userId", [expire: ONE_HOUR]) {
			def user = User.get(userId)
			if(user) {
				username = user.username
			}
		}

		return username

	}

}
