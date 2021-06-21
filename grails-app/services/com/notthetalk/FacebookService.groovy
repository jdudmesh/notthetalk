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

import grails.converters.*

import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.util.EntityUtils
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.protocol.HTTP;

class FacebookService {

    static transactional = true

	def grailsApplication
	def springSecurityService

    def getAccessToken(accessCode, redirectURI) {

		def user = springSecurityService.currentUser

		assert user

		def code = accessCode.encodeAsURL()
		def uri = redirectURI.encodeAsURL()
		def appId = grailsApplication.config.facebook.appid.encodeAsURL()
		def secret = grailsApplication.config.oauth.facebook.consumer.secret.encodeAsURL()

		def httpClient = new DefaultHttpClient()

		def url = "https://graph.facebook.com/oauth/access_token?client_id=$appId&redirect_uri=$redirectURI&client_secret=$secret&code=$code"
		def requestMethod = new HttpGet(url)
		def resp = httpClient.execute(requestMethod)

		def body = EntityUtils.toString(resp.getEntity())

		def map = [:]
		body.tokenize('&').each {
			def vars=it.tokenize('=')
			map[vars[0]] = vars[1]
		}

		def connection = ExternalUserConnection.findByUserAndConnectionType(user, ExternalUserConnection.TYPE_FACEBOOK)
		if(!connection) {
			connection = new ExternalUserConnection(
				user: user,
				connectionType: ExternalUserConnection.TYPE_FACEBOOK)
		}

		connection.remoteUserId = getUserId(map["access_token"])
		connection.requestToken = accessCode
		connection.accessToken =  map["access_token"]

		connection.save()

    }

	def getUserId(accessToken) {

		def url = "https://graph.facebook.com/me?access_token=" + accessToken.encodeAsURL()
		def requestMethod = new HttpGet(url)

		def httpClient = new DefaultHttpClient()
		def resp = httpClient.execute(requestMethod)

		def body = EntityUtils.toString(resp.getEntity())
		def jsonArray = JSON.parse(body)

		println jsonArray.toString()

		return jsonArray["id"]

	}

	def share(map) {

		def user = springSecurityService.currentUser
		def connection = ExternalUserConnection.findByUserAndConnectionType(user, ExternalUserConnection.TYPE_FACEBOOK)

		assert user
		assert connection

		def userId = connection.remoteUserId
		def accessToken = connection.accessToken.encodeAsURL()
		def url = "https://graph.facebook.com/$userId/feed?access_token=$accessToken"

		def httpClient = new DefaultHttpClient()
		def requestMethod = new HttpPost(url)

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		map.each { key, value ->
			params.add(new BasicNameValuePair(key, value));
		}

		requestMethod.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		def resp = httpClient.execute(requestMethod)
		def body = EntityUtils.toString(resp.getEntity())

		if(resp.statusline.statusCode == 200) {
			return true
		}
		else {
			return false
		}
	}

}
