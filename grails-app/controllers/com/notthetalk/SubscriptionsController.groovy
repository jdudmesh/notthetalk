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
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils;

class SubscriptionsController {

	def springSecurityService
	def folderService
	def postService
	def subscriptionsService
	def discussionTagLib

	@Secured(['ROLE_USER'])
    def list = {

		cache false

		def headerText = "Subscriptions"
		def nav = [:]

		def user = springSecurityService.currentUser
		def discussionSubs = subscriptionsService.getDiscussionSubs()
		def folderSubs = subscriptionsService.getFolderSubs()

		def folders = folderService.getFolders()
		//def postcounts = postService.getPostCounts()

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Subscriptions"] ]
		render view: "list", model: [headerText:headerText, discussionSubs:discussionSubs, folderSubs:folderSubs, folders:folders, nav: nav]
	}

	@Secured(['ROLE_USER'])
	def sort = {
		def order = params.int("order")
		subscriptionsService.setSortOrder(order)
		forward action: "list"
	}

	@Secured(['ROLE_USER'])
	def check = {

		def updatedDiscussions = subscriptionsService.getUpdatedDiscussions()
		if(updatedDiscussions.size()) {
			redirect controller: "discussion",action: "check", id: updatedDiscussions[0].discussionId
		}
		else {
			flash.actionmessage = "No new messages"
			forward action: "list"
		}

	}

	@Secured(['ROLE_USER'])
	def subscribe = {

		def discussion = Discussion.findById(params.id)
		def user = springSecurityService.currentUser

		if(discussion.folder.type == Folder.TYPE_ADMIN && !SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN,ROLE_MODERATOR")) {
			redirect controller: "home", action: "index"
			return
		}


		// check for duplicate subscriptions
		def subscription = Subscription.findByUserAndDiscussion(user, discussion)
		if(!subscription) {
			subscription = new Subscription(user: user, discussion: discussion)
			if(subscription.save())
			{
				flash.actionmessage = "You have successfully subscribed to the thread"
			}
		}
		else
		{
			flash.actionmessage = "You are already subscribed to the thread"
		}

		if(params.return) {
			redirect url: URLDecoder.decode(params.return)
		}
		else {
			redirect action: "list"
		}

	}

	@Secured(['ROLE_USER'])
	def unsubscribe = {

		def discussion = Discussion.findById(params.id)
		def user = springSecurityService.currentUser

		def subscription = Subscription.findByUserAndDiscussion(user, discussion)
		if(subscription) {
			subscription.delete()
			flash.actionmessage = "You have been unsubscribed from the thread"
		}
		else
		{
			def folderSubscription = FolderSubscription.findByFolderAndUser(discussion.folder, user)
			if(folderSubscription) {
				def exception = new FolderSubscriptionException(subscription: folderSubscription, discussion:discussion)
				exception.save(failOnError: true)
				flash.actionmessage = "You have been unsubscribed from the thread"
			}
			else {
				flash.actionmessage = "You weren't subscribed to the thread"
			}
		}

		if(params.return) {
			redirect url: URLDecoder.decode(params.return)
		}
		else {
			redirect action: "list"
		}

	}

	@Secured(['ROLE_USER'])
	def checkAjax = {

		def updatedDiscussions = subscriptionsService.getUpdatedDiscussions()

		StringWriter w = new StringWriter()

		updatedDiscussions.each {
			w << discussionTagLib.renderDiscussionTitle(it, 50, true)
		}

		render w.toString()

	}

	@Secured(['ROLE_USER'])
	def unsubselected = {

		def user = springSecurityService.currentUser

		def unsubs = params.list("unsub")
		unsubs.each() {

			def discussion = Discussion.findById(it)
			def subscription = Subscription.findByUserAndDiscussion(user, discussion)
			if(subscription) {
				subscription.delete()
				flash.actionmessage = "You have been unsubscribed from the thread"
			}

		}

		redirect action: "list"

	}


}
