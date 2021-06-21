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
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import twitter4j.auth.RequestToken
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.TwitterException
import twitter4j.conf.ConfigurationBuilder
import twitter4j.StatusUpdate

import groovyx.net.http.*

class TwitterService {

    static transactional = true

	def oauthService
	def springSecurityService

	private getTwitterConfiguration(account) {
		def configuration = ConfigurationHolder.config.twitter."$account"
		if(!configuration) {
			throw new IllegalArgumentException("Missing 'twitter.$account' configuration in your Config.groovy file")
		}
		return configuration
	}

	def getTwitter() {

		def twitterConfiguration = getTwitterConfiguration('default')

		ConfigurationBuilder cb = new ConfigurationBuilder();
				twitterConfiguration.each { key, value ->
					cb."$key" = value
				}

		def twitterFactory = new TwitterFactory(cb.build())
		return twitterFactory.getInstance()

	}

    def saveAuth(accessToken) {

		def user = springSecurityService.currentUser

		assert user

		def connection = ExternalUserConnection.findByUserAndConnectionType(user, ExternalUserConnection.TYPE_TWITTER)
		if(!connection) {
			connection = new ExternalUserConnection(
				user: user,
				connectionType: ExternalUserConnection.TYPE_TWITTER,
				remoteUserId: "",
				requestToken: "")
		}

		//'{"class":"twitter4j.auth.AccessToken","screenName":"johnnythesailor","token":"28520029-xp5bX8C4BqaN4KLlsT6S0yuUL8b0TI3WNueVXJcV6","tokenSecret":"8PbILqKNK8iIRqMrtk2p2XwfIfcZ03Tr0qRHcEmdlI","userId":28520029}'
		def token = [
			screenName: accessToken.screenName,
			token: accessToken.token,
			tokenSecret: accessToken.tokenSecret,
			userId: accessToken.userId
		]

		connection.accessToken = (token as JSON).toString()
		connection.save()

    }

	def tweet(text, link) {

		def user = springSecurityService.currentUser
		def connection = ExternalUserConnection.findByUserAndConnectionType(user, ExternalUserConnection.TYPE_TWITTER)
		def accessToken = JSON.parse(connection.accessToken)

		def bitly = shortenLink(link)

		def len = text.length() + bitly.length() + 1
		if(len > 140) {
			len = text.length() + (140 - len)
		}

		def truncate = Math.min(len, text.length())
		def tweet = text.substring(0, truncate) + " " + bitly

		def twitter = getTwitter()
		twitter.setOAuthAccessToken(new AccessToken(accessToken.token, accessToken.tokenSecret))

		def update = new StatusUpdate(tweet)
		twitter.updateStatus(update)

	}

	def shortenLink(link) {

		def http = new HTTPBuilder('http://api.bitly.com')

		def login = grailsApplication.config.bitly.login
		def appKey = grailsApplication.config.bitly.appKey

		http.get( path : "/v3/shorten",
			query : [
				login: login,
				apiKey: appKey,
				longUrl: link.encodeAsURL()]
				) { resp, json ->

			if(json.status_code == 200) {
				return json.data.url
			}
			else {
				return ""
			}

		}

	}

	def getTweets() {

		def user = springSecurityService.currentUser
		def connection = ExternalUserConnection.findByUserAndConnectionType(user, ExternalUserConnection.TYPE_TWITTER)
		def accessToken = JSON.parse(connection.accessToken)

		def twitter = getTwitter()
		twitter.setOAuthAccessToken(new AccessToken(accessToken.token, accessToken.tokenSecret))

		def timeline = twitter.getHomeTimeline()

		return timeline

	}


}