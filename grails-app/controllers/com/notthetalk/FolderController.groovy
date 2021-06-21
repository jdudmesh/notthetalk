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
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils;

class FolderController {

	def springSecurityService
	def folderService
	def discussionService
	def twitterService
	def facebookService
	def postService

    def list = {

		def folderid = folderService.getIdForKey(params.id)
		def folder = Folder.get(folderid)
		def user = springSecurityService.currentUser

		cache shared: true, validFor: 0

		if(folder.type == Folder.TYPE_ADMIN && !SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN,ROLE_MODERATOR")) {
			redirect controller: "home", action: "index"
			return
		}

		def offset = params.int("offset") ?: 0
		def max = params.int("max") ?: 30

		def discussions = null;
		if(SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN,ROLE_MODERATOR")) {
			discussions = FrontPageEntry.findAllByFolderId(folder.id, [sort: "id", order:"desc", max:max, offset:offset])
		}
		else {
			discussions = FrontPageEntry.findAllByFolderIdAndAdminOnly(folder.id, false, [sort: "id", order:"desc", max:max, offset:offset])
		}

		def numdiscussions = FrontPageEntry.countByFolderId(folder.id)
		def nav = [:]

		if(springSecurityService.isLoggedIn()) {
			nav.userIsSubscribed = folderService.userIsSubscribed(folder, user)
			HellBan.stickItToEm(user)
		}
		else {
			nav.userIsSubscribed = false
		}

		nav.showfolders = false
		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:folder.description] ]
		render(	view: "list",
				model: [	discussions: discussions,
							folder:folder,
							numdiscussions:numdiscussions,
							user: user,
							nav: nav,
							headerText: folder.description])

	}

	def headlines = {
		cache false

		def folder = Folder.findByType(Folder.TYPE_HEADLINES)

		def headlines = folderService.getHeadlines()
		def nav = [:]

		nav.showfolders = false
		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:folder.description] ]
		render view: "headlines", model: [headlines: headlines, folder:folder, nav: nav]

	}

	def twitter = {
		cache false

		def folder = Folder.findByType(Folder.TYPE_TWITTER)
		def tweets = [:]

		try {
			tweets = twitterService.getTweets()
		}
		catch(InvalidTwitterRequestException w) {
			tweets = [:]
			flash.actionMessage = "Twitter request failed!"
		}
		catch(Exception e) {

		}

		def nav = [:]

		nav.showfolders = false
		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:folder.description] ]
		render view: "twitter", model: [tweets: tweets, folder:folder, nav: nav]

	}

	@Secured(['ROLE_USER'])
	def subscribe = {

		def folderId = params.long('id')
		def folder = Folder.get(folderId)
		def user = springSecurityService.currentUser

		if(folder.type == Folder.TYPE_ADMIN && !SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN,ROLE_MODERATOR")) {
			redirect controller: "home", action: "index"
			return
		}


		folderService.subscribeToFolder(folder, user)

		redirect controller: "folder", action: "list", id: folder.folderKey

	}

	@Secured(['ROLE_USER'])
	def unsubscribe = {

		def folderId = params.long('id')
		def folder = Folder.get(folderId)
		def user = springSecurityService.currentUser

		if(folder.type == Folder.TYPE_ADMIN && !SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN,ROLE_MODERATOR")) {
			redirect controller: "home", action: "index"
			return
		}

		folderService.unsubscribeFromFolder(folder, user)

		redirect controller: "folder", action: "list", id: folder.folderKey

	}

	@Secured(['ROLE_USER'])
	def archive = {

		def folderId = params.long('id')
		def folder = Folder.get(folderId)
		def start = params.int('start') ?: 0
		def num = params.int('num') ?: 100
		def postCounts = [:]

		if(folder.type == Folder.TYPE_ADMIN && !SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN,ROLE_MODERATOR")) {
			redirect controller: "home", action: "index"
			return
		}

		def discussions = discussionService.getArchivedDiscussionsByFolder(folder, start, num)
		def nav = [:]

		nav.breadcrumbs = [ [link:true, controller:"folder", action:"list", id:folder.folderKey, text:folder.description], [link:false, controller:"folder", action:"archive", id: folder.id, text:"Archive"] ]
		render view: "archive", model: [discussions: discussions, postcounts: postCounts, nav: nav]
	}

	@Secured(['ROLE_ADMIN'])
	def listdeleted = {

		cache false

		def user = springSecurityService.currentUser

		def discussions = discussionService.getDeletedDiscussions()

		def nav = [:]

		nav.userIsSubscribed = false
		nav.showfolders = true
		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Deleted Discussions"] ]
		render view: "listdeleted", model: [discussions: discussions, postcounts:[:], user: user, nav: nav]
	}


}

