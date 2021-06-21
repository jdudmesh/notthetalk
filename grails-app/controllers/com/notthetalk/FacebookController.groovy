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

import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.util.EntityUtils
import org.apache.http.client.utils.URLEncodedUtils;


class FacebookController {

	def facebookService
	def springSecurityService

	@Secured(['ROLE_USER'])
    def connect = {

		def appId = grailsApplication.config.facebook.appid
		def url = createLink(controller: "facebook", action: "callback", absolute: true).encodeAsURL()

		redirect url:"https://www.facebook.com/dialog/oauth?client_id=$appId&redirect_uri=$url&scope=publish_stream"
	}

	@Secured(['ROLE_USER'])
	def callback = {

		if(params.code) {

			def redirectURI = createLink(controller: "facebook", action: "callback", absolute: true)
			facebookService.getAccessToken(params.code, redirectURI)

			redirect controller: "home", action: "bio"

		}
	}

	@Secured(['ROLE_USER'])
	def share = {

		def post = Post.get(params.id)
		def user = springSecurityService.currentUser
		def ok = false

		if(post.user == user) {

			def message = new ByteArrayOutputStream()
			def link = createLink(controller: "discussion", action: "listfrom", id: post.id, absolute: true).toString()

			TextFormatter.formatTextRaw post.text, message

			ok = facebookService.share([
				message:message.encodeAsHTML(),
				link:link,
				name:"From NOTtheTalk...",
				caption:post.discussion.title,
				picture: "http://talk.notthetalk.com/images/gut.png"])


		}

		if(ok) {
			render "OK"
		}
		else {
			render "ERROR"
		}

	}


}
