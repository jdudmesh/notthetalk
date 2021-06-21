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
import com.maxmind.geoip.*;

import org.apache.log4j.Logger;
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils;

class HomeController {

	private static Logger log = Logger.getLogger(HomeController.class);

	def springSecurityService
	def postService
	def discussionService
	def folderService
	def subscriptionsService
	def userService
	def tagsService

    def index = {

		//cache shared: true, validFor: 0
		cache false

		def user = springSecurityService.currentUser
		def folders = folderService.getFolders()
		def tags = tagsService.getTop10()
		def iphone = false

		def agent = request.getHeader("User-Agent")
		if(agent =~ /iPhone/) {
			iphone = true
		}

		//def discussions = FrontPageEntry.listOrderById(order: "desc", max: 50)//discussionService.getCurrentDiscussions()
		def discussions = null;
		if(SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN")) {
			discussions = FrontPageEntry.listOrderById(order: "desc", max: 50)
		}
		else {
			discussions = FrontPageEntry.findAllByAdminOnly(false, [sort: "id", order:"desc", max:50])
		}

		def updatedDiscussions = []

		if(springSecurityService.isLoggedIn()) {
			updatedDiscussions = subscriptionsService.getUpdatedDiscussions()
			HellBan.stickItToEm(user)
		}

		def nav = [:]

		nav.showfolders = true
		nav.breadcrumbs = [ ]

		/*
		withMobileDevice { device ->

			render(	view: "mobileIndex",
					model: [
						folders:folders,
						discussions: discussions,
						postcounts:postcounts,
						updates: updatedDiscussions,
						user:user,
						nav: nav])
		}

		withoutMobileDevice { device ->
		*/
			render	view: "index",
				model: [
					folders:folders,
					tags: tags,
					discussions: discussions,
					updates: updatedDiscussions,
					user:user,
					nav: nav,
					iphone: iphone]
		// }


	}

	def search = {

		def nav = [:]

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Search"] ]
		render view: "search", nav: nav
	}

	/*
	def results = {

		cache shared: true, validFor: 0

		// TODO Full Text Search

		def cleanText = params.searchterm.encodeAsHTML()
		def posts = postService.searchPosts(cleanText)
		def nav = [:]

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Search Results"] ]
		render(view: "results", model:[posts:posts, nav: nav])
	}
	*/

	def charter = {

		cache shared: true, validFor: 86400

		def nav = [:]

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Charter"] ]
		render view: "charter", model: [nav: nav]
	}

	def help = {

		cache shared: true, validFor: 86400

		def nav = [:]

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Help"] ]
		render view: "help", model: [nav: nav]
	}

	def policy = {

		cache shared: true, validFor: 86400

		def nav = [:]

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Policy"] ]
		render view: "policy", model: [nav: nav]
	}

	def cookiepolicy = {

		cache shared: true, validFor: 86400

		def nav = [:]

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Cokkie Policy"] ]
		render view: "cookiepolicy", model: [nav: nav, folders: Folder.listOrderByDescription()]
	}

	def privacy = {

		cache shared: true, validFor: 86400

		def nav = [:]

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Privacy Policy"] ]
		render view: "privacy", model: [nav: nav, folders: Folder.listOrderByDescription()]
	}

	def terms = {

		cache shared: true, validFor: 86400

		def nav = [:]

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Terms and Conditions"] ]
		render view: "terms", model: [nav: nav]
	}

	def donations = {

		cache shared: true, validFor: 86400

		def nav = [:]

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Donations"] ]
		render view: "donations", model: [nav: nav]
	}

	@Secured(['ROLE_USER'])
	def bio = {

		cache shared: true, validFor: 0

		def user = springSecurityService.currentUser
		def discussionSubs = subscriptionsService.getDiscussionSubs()
		def folderSubs = subscriptionsService.getFolderSubs()
		//def postcounts = postService.getPostCounts()
		def nav = [:]

		if(!user.options) {
			user.options = new UserOptions(user: user, autoSubs: false, sortFoldersByActivity: true, markdown: false)
		}

		nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Profile"] ]
		render view: "bio", model:[user:user, discussionSubs:discussionSubs, folderSubs:folderSubs, nav: nav]
	}

	@Secured(['ROLE_USER'])
	def savebio = {

		cache false

		def user = springSecurityService.currentUser
		if(!user.options) {
			user.options = new UserOptions(user: user, autoSubs: false, sortFoldersByActivity: true, markdown: false)
		}

		if(params.userid.toInteger() != user.id) {
			redirect controller: "home", action: "index"
		}
		else {

			user.email = params.email
			user.bio = params.bio
			user.options.autoSubs = params.autosubs ? true : false
			user.options.sortFoldersByActivity = params.sortfolders ? true : false
			user.options.markdown = params.markdown	? true : false
			user.displayEmail = params.displayemail ? true : false

			user.validate()
			if(!user.hasErrors()) {
				user.save()
				flash.actionmessage = "Details saved!"
			}

			def discussionSubs = subscriptionsService.getDiscussionSubs()
			def folderSubs = subscriptionsService.getFolderSubs()
			def postcounts = postService.getPostCounts()
			def nav = [:]


			render view: "bio", model:[user:user, discussionSubs:discussionSubs, folderSubs:folderSubs, postcounts:postcounts, nav: nav]
		}

	}

	@Secured(['ROLE_USER'])
	def user = {

		cache false

		def user = User.findByUsername(params.id)
		def nav = [:]
		def logins = null
		def history = null
		def sockpuppets = null
		def modNotes = null

		if(!user) {
			redirect controller: "home", action: "index"
		}
		else {

			def callingUser = springSecurityService.currentUser
			if(callingUser.hasRole('ROLE_ADMIN')) {
				logins = UserLoginLocation.findAllByUser(user, [sort: "lastLogin", order: "desc"])
				history = UserHistory.findAllByUser(user, [sort: "createdDate", order: "desc"])
				sockpuppets = userService.getSockpuppets(user)
				modNotes = UserModNote.findAllByUser(user, [sort: "createdDate", order: "desc"])
			}

			nav.isSelf = (user.id == springSecurityService.currentUser.id)
			nav.isIgnored = user.isIgnoredBy(springSecurityService.currentUser)
			nav.moderationScore = postService.getUserModerationScore(user)
			render view: "user", model:[user:user, nav: nav, modNotes: modNotes, logins: logins, history: history, sockpuppets: sockpuppets]
		}

	}

	@Secured(['ROLE_ADMIN'])
	def addModNote = {

		def user = User.get(params.userid)
		def callingUser = springSecurityService.currentUser

		def modNote = new UserModNote()
		modNote.createdDate = modNote.lastUpdated = new Date()
		modNote.user = user;
		modNote.mod = callingUser;
		modNote.note = params.note;
		modNote.save()

		redirect action:"user", id:user.username

	}

	@Secured(['ROLE_ADMIN'])
	def showip = {

		def addr = InetAddress.getByName(params.id)


		def logins = UserLoginLocation.findAllByIpAddress(params.id, [sort: "lastLogin", order: "desc"])
		render view: "showip", model:[logins: logins, rdns: addr.getHostName()]
	}


	@Secured(['ROLE_ADMIN'])
	def geolocation = {

		def dataFile = grailsApplication.config.geocitylite.datafile
		LookupService cl = new LookupService(dataFile, LookupService.GEOIP_MEMORY_CACHE );

		UserLoginLocation.findAllWhere(geoLocation: null).each {
			Location l1 = cl.getLocation(it.ipAddress)
			if(l1 != null) {
				it.geoLocation = l1.city + "," + l1.countryName
				it.save()
			}
		}

		render "Geolocation update done"

	}

}
