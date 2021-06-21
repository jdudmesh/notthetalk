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

import com.megatome.grails.RecaptchaService
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil

class SignupController {

	def springSecurityService
	def recaptchaService
	def userService

    def index = {
		redirect url: "https://justthetalk.com/signup/index"

		// def nav = [:]
		// nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Sign Up"] ]
		// render view:"index", model: [nav: nav]
	}

	def confirmationrequired = {
		render view:"confirmationrequired"
	}

	def save = {

		def user = new User()
		def recaptchaOK = recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)
		def ip = request.getHeader("X-Forwarded-For") ?: request.getRemoteAddr()

		if(userService.saveUser(user, params, recaptchaOK, ip)) {
			redirect action: "confirmationrequired"
		}
		else {
			render view:"index", model:[user:user]
		}

		recaptchaService.cleanUp(session)

	}

	def confirm = {
		def key = params.id
		def conf = SignupConfirmation.findByConfirmationKey(key)
		if(conf) {

			conf.ipAddress = request.getHeader("X-Forwarded-For") ?: request.getRemoteAddr()
			conf.user.enabled = true
			conf.user.save()
			conf.save()

			UserHistory.CreateUserHistoryLocation(conf.user, "SIGNUP CONFIRMED", conf.ipAddress);

			flash.message = "Confirmation successful. You can log in now!"
			redirect controller: "login", action: "auth"

		}
		else {
			render view:"confirmationnotfound"
		}

	}

}
