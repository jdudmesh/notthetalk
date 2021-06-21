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

class User {

	static searchable = [only: ["username"]]
	//static searchable = {
	//	username boost: 1.0
	//}

	Date createdDate
	Date lastUpdated
	Date lastLoginDate

	String username
	String password
	String email
	String bio
	boolean displayEmail

	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	//boolean accountHellban
	//int invisibilityThreshold

	static hasMany = [ subscriptions:Subscription, bookmarks:UserDiscussion, ignores:IgnoreUser, connections:ExternalUserConnection ]
	static hasOne = [options:UserOptions]

	static constraints = {
		username blank: false, unique: true, size: 6..24, matches: "\\w{6,24}"
		password blank: false, markup:true
		email blank: false, size: 3..64, unique: true
		bio blank: true, maxsize: 1024
		displayEmail default:false
		lastLoginDate nullable: true
	}

	static mapping = {
		password column: '`password`'
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

	def isIgnoredBy(user) {
		def obj = IgnoreUser.findByUserAndIgnoredUser(user, this)
		if(obj) {
			return true
		}
		else {
			return false
		}
	}

	def check() {

		if(accountLocked) {
			throw new InvalidUserException(reason:"Your account is locked!")
		}

		if(passwordExpired) {
			throw new InvalidUserException(reason:"Your password has expired!")
		}

		if(accountExpired) {
			throw new InvalidUserException(reason:"Your account has expired!")
		}

		if(!enabled) {
			throw new InvalidUserException(reason:"Your account is deleted!")
		}

	}

	def hasTwitter() {
		if(ExternalUserConnection.findWhere(user: this, connectionType: ExternalUserConnection.TYPE_TWITTER)) {
			return true
		}
		else {
			return false
		}
	}

	def hasFacebook() {
		if(ExternalUserConnection.findWhere(user: this, connectionType: ExternalUserConnection.TYPE_FACEBOOK)) {
			return true
		}
		else {
			return false
		}
	}

	boolean hasRole(String userRole) {
		boolean result = false;
		for (Role role : getAuthorities()) {
			if (role.authority.equals(userRole)) {
				result = true;
				break;
			}
		}
		return result;
	}
}
