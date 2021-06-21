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
import twitter4j.auth.RequestToken
import twitter4j.auth.AccessToken
import twitter4j.TwitterException
import redis.clients.jedis.Jedis

class TwitterController {

	static int MINS_15 = 900

	def springSecurityService
	def twitterService
	def redisService

	@Secured(['ROLE_USER'])
    def connect = {

		def callback = createLink(controller: 'twitter', action: 'callback', absolute:true).toString()
		def twitter = twitterService.getTwitter()
		def user = springSecurityService.currentUser

		RequestToken requestToken = twitter.getOAuthRequestToken(callback)
		session.twitter_requestToken = requestToken

		withRedis { Jedis redis ->
			def key = "twitter_token_" + user.id.toString()
			redis.setex(key, MINS_15, requestToken)
		}

		redirect url:requestToken.getAuthorizationURL()

	}

	@Secured(['ROLE_USER'])
    def callback = {

		def user = springSecurityService.currentUser
		def requestToken = null

		withRedis { Jedis redis ->
			def key = "twitter_token_" + user.id.toString()
			requestToken = redis.get(key)
		}

		def twitter = twitterService.getTwitter()
		def accessToken = twitter.getOAuthAccessToken(requestToken, params.oauth_verifier)

		twitterService.saveAuth(accessToken)

		redirect controller: "home", action: "bio"

	}

	@Secured(['ROLE_USER'])
	def tweet = {

		def post = Post.get(params.id)
		def user = springSecurityService.currentUser
		def ok = false

		if(post.user == user) {

			def link = createLink(controller: "discussion", action: "listfrom", id: post.id, absolute: true).toString()

			ok = twitterService.tweet(post.text, link)

		}

		if(ok) {
			render "OK"
		}
		else {
			render "ERROR"
		}

	}

}
