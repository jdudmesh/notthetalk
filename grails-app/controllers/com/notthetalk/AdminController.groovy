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

import grails.plugins.springsecurity.Secured

class AdminController {

	def springSecurityService
	def postService

	@Secured(['ROLE_ADMIN'])
    def index = { }

	@Secured(['ROLE_ADMIN', 'ROLE_MODERATOR'])
	def moderate = {

		cache shared: true, validFor: 0

		def posts = postService.getPostsToModerate()
		def nav = [:]

		nav.start = 0
		render view: "moderate", model:[posts:posts, user:springSecurityService.currentUser, nav: nav]
	}

	@Secured(['ROLE_ADMIN'])
	def modhistory = {

		cache shared: true, validFor: 0

		def posts = postService.getRecentlyModeratedPosts()
		def nav = [:]

		nav.start = 0
		render view: "moderate", model:[posts:posts, user:springSecurityService.currentUser, nav: nav]

	}

	@Secured(['ROLE_ADMIN'])
	def users = {
		cache false
		ArchiveJob.triggerNow()
		render view: "users", model: [users:User.getAll()]
	}

	@Secured(['ROLE_ADMIN'])
	def resetpassword = {

		def user = User.get(params.userid)
		user.password = springSecurityService.encodePassword(params.password.encodeAsSanitizedMarkup())
		user.validate()
		if(!user.hasErrors()) {
			user.save(flush:true, failOnError: true)
		}
		else {
			user.errors.each {
				println it
			}
		}

		redirect controller: "home", action: "user", id: user.username
	}

	@Secured(['ROLE_ADMIN'])
	def archivenow = {
		ArchiveJob.triggerNow()
		redirect controller: "home", action: "index"
	}

	@Secured(['ROLE_ADMIN'])
	def moderateuser = {

		def user = User.get(params.id)
		assert user

		user.options.premoderate = true;
		user.options.save(failOnError: true)

		UserHistory.CreateUserHistory(user, "PREMOD", springSecurityService.currentUser)

		redirect controller: "home", action: "user", id: user.username

	}

	@Secured(['ROLE_ADMIN'])
	def unmoderateuser = {

		def user = User.get(params.id)
		assert user

		user.options.premoderate = false;
		user.options.save(failOnError: true)
		UserHistory.CreateUserHistory(user, "UNPREMOD", springSecurityService.currentUser)

		redirect controller: "home", action: "user", id: user.username

	}

	@Secured(['ROLE_ADMIN'])
	def lockuser = {

		def user = User.get(params.id)
		assert user

		user.accountLocked = true
		user.save(failOnError: true)

		UserHistory.CreateUserHistory(user, "LOCK", springSecurityService.currentUser)

		redirect controller: "home", action: "user", id: user.username

	}

	@Secured(['ROLE_ADMIN'])
	def unlockuser = {

		def user = User.get(params.id)
		assert user

		user.accountLocked = false
		user.save(failOnError: true)

		UserHistory.CreateUserHistory(user, "UNLOCK", springSecurityService.currentUser)

		redirect controller: "home", action: "user", id: user.username

	}

	@Secured(['ROLE_ADMIN'])
	def deleteuser = {

		def user = User.get(params.id)
		assert user

		user.enabled = false
		user.save(failOnError: true)

		UserHistory.CreateUserHistory(user, "DELETE", springSecurityService.currentUser)

		redirect controller: "home", action: "user", id: user.username

	}

	@Secured(['ROLE_ADMIN'])
	def undeleteuser = {

		def user = User.get(params.id)
		assert user

		user.enabled = true
		user.save(failOnError: true)

		UserHistory.CreateUserHistory(user, "DELETE", springSecurityService.currentUser)

		redirect controller: "home", action: "user", id: user.username

	}

	@Secured(['ROLE_ADMIN'])
	def watchuser = {

		def user = User.get(params.id)
		assert user

		user.options.watch = true;
		user.options.save(failOnError: true)
		user.save(failOnError: true)

		UserHistory.CreateUserHistory(user, "WATCH", springSecurityService.currentUser)

		redirect controller: "home", action: "user", id: user.username

	}

	@Secured(['ROLE_ADMIN'])
	def unwatchuser = {

		def user = User.get(params.id)
		assert user

		user.options.watch = false;
		user.options.save(failOnError: true)
		user.save(failOnError: true)

		UserHistory.CreateUserHistory(user, "UNWATCH", springSecurityService.currentUser)

		redirect controller: "home", action: "user", id: user.username

	}

	@Secured(['ROLE_ADMIN'])
	def fixpostnumbers = {
		//postService.fixPostNumbers()



	}

	@Secured(['ROLE_ADMIN'])
	def premods = {

		def crit = User.createCriteria()
		def preusers = crit.list {
			and {
				options{
					eq("premoderate", true)
				}
				eq("enabled", true)
			}
			order "username"
		}

		def critWatch = User.createCriteria()
		def watchusers = critWatch.list {
			and {
				options{
					eq("watch", true)
				}
				eq("enabled", true)
			}
			order "username"
		}

		def lockusers = User.findAllByAccountLockedAndEnabled(true, true, [sort:"username"])

		render view:"premods", model: [preusers:preusers, lockusers:lockusers, watchusers: watchusers]

	}

	@Secured(['ROLE_ADMIN'])
	def recentusers = {

		def now = new Date()

		def crit = User.createCriteria()
		def users = crit.list {
			between("createdDate", now - 30, now)
			order("createdDate", "desc")
		}

		render view:"recentusers", model: [users:users]

	}


}
