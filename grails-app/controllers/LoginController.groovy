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

import grails.converters.JSON

import javax.servlet.http.HttpServletResponse

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import grails.plugins.springsecurity.Secured

import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil

import com.notthetalk.User;
import com.notthetalk.PasswordReset;
import com.notthetalk.LoginHistory;

class LoginController {

	/**
	 * Dependency injection for the authenticationTrustResolver.
	 */
	def authenticationTrustResolver

	/**
	 * Dependency injection for the springSecurityService.
	 */
	def springSecurityService

	def recaptchaService

	/**
	 * Default action; redirects to 'defaultTargetUrl' if logged in, /login/auth otherwise.
	 */
	def index = {

		if (springSecurityService.isLoggedIn()) {
			redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
		}
		else {
			redirect action: auth, params: params
		}
	}

	/**
	 * Show the login page.
	 */
	def auth = {

		def config = SpringSecurityUtils.securityConfig

		if (springSecurityService.isLoggedIn()) {
			redirect uri: config.successHandler.defaultTargetUrl
			return
		}

		def nav = [:]
		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Sign In"] ]

		String view = 'auth'
		String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
		render view: view, model: [postUrl: postUrl,
		                           rememberMeParameter: config.rememberMe.parameter,
								   nav: nav]
	}

	/**
	 * The redirect action for Ajax requests.
	 */
	def authAjax = {
		response.setHeader 'Location', SpringSecurityUtils.securityConfig.auth.ajaxLoginFormUrl
		response.sendError HttpServletResponse.SC_UNAUTHORIZED
	}

	/**
	 * Show denied page.
	 */
	def denied = {
		if (springSecurityService.isLoggedIn() &&
				authenticationTrustResolver.isRememberMe(SCH.context?.authentication)) {
			// have cookie but the page is guarded with IS_AUTHENTICATED_FULLY
			redirect action: full, params: params
		}
	}

	/**
	 * Login page for users with a remember-me cookie but accessing a IS_AUTHENTICATED_FULLY page.
	 */
	def full = {
		def config = SpringSecurityUtils.securityConfig
		render view: 'auth', params: params,
			model: [hasCookie: authenticationTrustResolver.isRememberMe(SCH.context?.authentication),
			        postUrl: "${request.contextPath}${config.apf.filterProcessesUrl}"]
	}

	/**
	 * Callback after a failed login. Redirects to the auth page with a warning message.
	 */
	def authfail = {

		String msg = ''

        def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
        if (exception) {
                if (exception instanceof AccountExpiredException) {
                        msg = g.message(code: "springSecurity.errors.login.expired")
                }
                else if (exception instanceof CredentialsExpiredException) {
                        msg = g.message(code: "springSecurity.errors.login.passwordExpired")
                }
                else if (exception instanceof DisabledException) {
                        msg = g.message(code: "springSecurity.errors.login.disabled")
                }
                else if (exception instanceof LockedException) {
                        msg = g.message(code: "springSecurity.errors.login.locked")
                }
                else {
                        msg = g.message(code: "springSecurity.errors.login.fail")
                }
        }

        if (springSecurityService.isAjax(request)) {
                render([error: msg] as JSON)
        }
        else {
                flash.message = msg
                redirect action: 'auth', params: params
        }

	}

	/**
	 * The Ajax success redirect url.
	 */
	def ajaxSuccess = {
		def response = [success: true, username: springSecurityService.authentication.name]
		render response as JSON
	}

	/**
	 * The Ajax denied redirect url.
	 */
	def ajaxDenied = {
		def response = [error: 'access denied']
		render response as JSON
	}

	def forgotpassword = {
		render view: "forgotpassword"
	}

	def recoverpassword = {

		def user = User.findByUsernameAndEmail(params.username, params.email)
		if(user) {

			def key = (new Date()).getTime().toString()
			def req = new PasswordReset(user:user, resetKey:key)
			req.save(flush:true)

			def url = "https://justthetalk.com/login/resetpassword/" + key

			sendMail {
				to user.email
				from "help@justthetalk.com"
				subject "Password Reset Request"
				html (view: "/common/password-reset-mail", model: [url:url, user:user])
			}

			flash.actionmessage = "Your request has been sent"

		}
		else {
			flash.actionmessage = "Sorry, that user cannot be found"
		}

		render view: "forgotpassword"

	}

	def resetpassword = {
		flash.resettoken = params.id
		render view: "resetpassword"
	}

	def savepassword = {

		def errors = new ArrayList()
		def user = User.findByUsernameAndEmail(params.username, params.email)

		if(user) {

			def req = PasswordReset.findByUserAndResetKey(user, params.resettoken.toLong())
			if(req) {

				if(!(params.newPassword ==~ /.{6,12}/))
				{
					errors.add "user.password.wronglength"
				}

				if(params.newPassword != params.newPasswordConfirm)
				{
					errors.add "user.password.doesnotmatch"
				}

				def recaptchaOK = recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)
				if(!recaptchaOK)
				{
					errors.add "user.username.captcha"
				}

				if(!errors.size()) {
					user.password = springSecurityService.encodePassword(params.newPassword.encodeAsSanitizedMarkup())
					user.save(flush:true)
					req.delete()
					recaptchaService.cleanUp(session)
				}

			}
			else {
				errors.add "password.reset.requestnotfound"
			}
		}
		else
		{
			errors.add "password.reset.usernotfound"
		}

		if(errors.size()) {
			flash.errors = errors
			redirect action: "resetpassword", id: params.resettoken
		}
		else {
			redirect controller: "login", action: "index"
		}

	}

	@Secured(['ROLE_USER'])
	def changepassword = {
		render view: "changepassword"
	}

	@Secured(['ROLE_USER'])
	def savechangedpassword = {

		def errors = new ArrayList()
		def user = springSecurityService.currentUser

		if(!(params.newPassword ==~ /.{6,12}/))
		{
			errors.add "user.password.wronglength"
		}

		if(params.newPassword != params.newPasswordConfirm)
		{
			errors.add "user.password.doesnotmatch"
		}

		if(!errors.size()) {
			user.password = springSecurityService.encodePassword(params.newPassword.encodeAsSanitizedMarkup())
			user.save(flush:true)
			redirect controller: "home", action: "bio"
		}
		else {
			flash.errors = errors
			redirect action: "changepassword"
		}

	}

	/*
	@Secured(['ROLE_USER'])
	def facebookLogin = {
		def user = springSecurityService.currentUser
		user.facebookUID = session.facebook.uid
		user.save()
		redirect controller:"home", action:"bio"
	}
	*/
}
