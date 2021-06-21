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
import grails.converters.*
import grails.util.Environment


class PostController {

	def markupSanitizerService
	def springSecurityService
	def discussionService
	def postService
	def facebookGraphService
	def userService

	def recaptchaService

	@Secured(['ROLE_USER'])
	def save = {

		withForm {

			def discussionId = params.long("discussionid")
			def postId = params.postid
			def postText = params.reply.encodeAsHTML()
			def folderId = params.int("folderid")
			def sourceId = params.int("sourceid")
			def sourceType = params.int("sourcetype")
			def postAsAdmin = false

			if(params.postasadmin) {
				postAsAdmin = params.postasadmin == "on" ? true : false
			}

			try {

				def user = springSecurityService.currentUser
				HellBan.stickItToEm(user)

				def ip = request.getHeader("x-forwarded-for") ?: request.getRemoteAddr()
				def post = postService.savePost(discussionId, postId, postText, folderId, sourceId, sourceType, postAsAdmin, ip)

				if(params.postid == "NEW") {
					flash.pagehint = "ADD"
				}
				else {
					flash.pagehint = "EDIT"

				}

				if(!post.hasErrors()) {
					flash.fragment = "post_" + post.id.toString()
					redirect controller: "discussion", action: "check", id: post.discussion.id
				}
				else {
					flash.messagetext = params.reply
					if(sourceId) {
						redirect controller: "discussion", view: "fromheadline", id: sourceId, fragment: "post"
					}
					else {
						redirect controller: "discussion", action: "list", id: params.discussionid, params:[start: params.start], fragment: "post"
					}
				}

			}
			catch(InvalidUserException badUser) {
				flash.errorMessage = badUser.reason
				redirect controller: "home", view: "index"
			}
			catch(InvalidDiscussionFromHeadlineException badHeadline) {
				log?.error "Source Headline: $sourceId"
				log?.error badHeadline.toString()
				flash.errorMessage = "Error creating discussion"
				redirect controller: "discussion", action: "fromheadline", id: sourceId
			}
		}
		.invalidToken {
			flash.messagetext = params.reply
			flash.posterror = "Hmm, this looks like a duplicate post. If it's not then click 'Post Reply' again."
			redirect controller: "discussion", action: "list", id: params.discussionid, params:[start: params.start], fragment: "post"
		}
	}

	@Secured(['ROLE_USER'])
    def edit = {

		def user = springSecurityService.currentUser
		def post = Post.get(params.id)

		if(post && post.user.id == user.id)
		{
			flash.messagetext = post.text.decodeHTML()
			flash.postid = post.id
		}

		flash.forceUpdate = true
		redirect controller: "discussion", action: "list", id: post.discussion.id, params:[start: params.start], fragment: "post"

	}

	@Secured(['ROLE_USER'])
	def delete = {

		def user = springSecurityService.currentUser
		def post = Post.get(params.id)
		if(post.user.id != user.id) {
			render status:403, text: "You cannot delete other people's posts"
		}
		else {

			postService.deletePost(post)
			def start = params.start.toInteger()

			flash.pagehint = "DELETE"
			redirect controller: "discussion", action: "list", id: post.discussion.id, params:[start: start]

		}

	}

	@Secured(['ROLE_USER'])
	def ignore = {

		def user = springSecurityService.currentUser
		def post = Post.get(params.id)

		userService.ignoreUser(user, post.user, true)

		flash.forceUpdate = true
		redirect url: params.return

	}

	@Secured(['ROLE_USER'])
	def ignoreuser = {

		def user = springSecurityService.currentUser
		def ignoredUser = User.get(params.id)

		userService.ignoreUser(user, ignoredUser, true)

		redirect controller: "home", action: "user", id: ignoredUser.username
	}

	@Secured(['ROLE_USER'])
	def unignoreuser = {

		def user = springSecurityService.currentUser
		def ignoredUser = User.get(params.id)

		userService.ignoreUser(user, ignoredUser, false)

		redirect controller: "home", action: "bio", id: user.id
	}

	def report = {

		redirect url: "https://justthetalk.com/post/report/${params.id}"

		// def post = Post.get(params.id)
		// if(post?.status == Post.STATUS_OK && post?.discussion.status == Discussion.STATUS_OK && post?.discussion.folder.type != Folder.TYPE_ADMIN) {

		// 	def report = new PostReport()
		// 	def user = springSecurityService.currentUser
		// 	def nav = [:]

		// 	if(user) {
		// 		report.user = user
		// 		report.name = user.username
		// 		report.email = user.email
		// 	}

		// 	nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Report Post"] ]
		// 	render view: "report", model: [post:post, report:report, nav: nav]

		// } else {
		// 	redirect2 controller: "home", action: "index"
		// }

	}

	def savereport = {

		def recaptchaOK = recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)

		def report = new PostReport()
		def nav = [:]
		def post = Post.get(params.postid)

		if(recaptchaOK) {

			report.post = post
			report.createdDate = new Date()
			report.score = 0
			report.user = springSecurityService.currentUser
			report.email = params.email.encodeAsHTML()
			report.name = params.username.encodeAsHTML()
			report.comment = params.comment.encodeAsHTML()

			def x = request.getHeader("x-forwarded-for")
			def y = request.remoteAddr
			report.ipaddress = request.getHeader("x-forwarded-for") ?: request.remoteAddr

			report.validate()

			if(!report.hasErrors() && postService.saveReport(post, report)) {

				if(Environment.getCurrent() != Environment.DEVELOPMENT) {
					sendMail {
						to report.email
						from "help@justthetalk.com"
						bcc "admins@justthetalk.com"
						subject "Report Confirmation"
						html (view: "/common/report-submission-confirmation", model: [report:report])
					}
				}

				nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Report Confirmation"] ]
				render view: "reportconfirm", model: [nav: nav]
			}
			else {

				report.email = params.email
				report.name = params.username
				report.comment = params.comment

				nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Report Post"] ]
				render view: "report", model: [post:post, report:report, nav: nav]
			}

		}
		else {

			flash.message = "Please enter the CAPTCHA code"

			report.email = params.email
			report.name = params.username
			report.comment = params.comment

			nav.breadcrumbs = [ [link:false, controller:"", action:"", text:"Report Post"] ]
			render view: "report", model: [post:post, report:report, nav: nav]

		}

		recaptchaService.cleanUp(session)
	}

	@Secured(['ROLE_ADMIN', 'ROLE_MODERATOR'])
	def vote_keep = {
		def post = Post.get(params.id)
		postService.moderatePost post, 1
		redirect controller: "discussion", action: "list", id: post.discussion.id, params: [start: params.start]
	}

	@Secured(['ROLE_ADMIN', 'ROLE_MODERATOR'])
	def vote_delete = {
		def post = Post.get(params.id)
		postService.moderatePost post, -1
		redirect controller: "discussion", action: "list", id: post.discussion.id, params: [start: params.start]
	}

	@Secured(['ROLE_ADMIN'])
	def adminUndelete = {
		def post = Post.get(params.id)
		postService.adminUndeletePost post
		redirect url: params.return
		//redirect controller: "discussion", action: "list", id: post.discussion.id, params: [start: params.start]
	}

	@Secured(['ROLE_ADMIN'])
	def adminDelete = {
		def post = Post.get(params.id)
		postService.adminDeletePost post
		redirect url: params.return
		//redirect controller: "discussion", action: "list", id: post.discussion.id, params: [start: params.start]
	}

	@Secured(['ROLE_USER'])
	def facebookShare = {

		def post = Post.get(params.id)
		def user = springSecurityService.currentUser

		if(post.user == user) {
			def message = new ByteArrayOutputStream()
			def link = createLink(controller: "discussion", action: "listfrom", id: post.id, absolute: true).toString()

			TextFormatter.formatTextRaw post.text, message

			facebookGraphService.publishWall(message:message.encodeAsHTML(),
				link:link,
				name:"From NOTtheTalk...",
				caption:post.discussion.title,
				picture: "http://talk.notthetalk.com/images/gut.png")

			render "OK"
		}
		else {
			render "ERROR"
		}
	}

	@Secured(['ROLE_ADMIN', 'ROLE_MODERATOR'])
	def submitmoderation = {

		def post = Post.get(params.id)
		def user = springSecurityService.currentUser

		def result = 0
		def vote = 1 //SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN") ? 1000 : 1
		def action = params.mod_action

		switch(action) {
			case "MOD_KEEP":
				result = vote
				break
			case "MOD_DELETE":
				result = -vote
				break
		}

		postService.moderatePost post, result, params.comment

		render postService.cachePost(post)

	}

	@Secured(['ROLE_USER'])
	def getpostAjax = {

		def post = Post.get(params.id)
		assert post

		def nav = [start: 0]

		render template: "/common/postbody", model: [post: post, discussion: post.discussion, posts: [post], nav: nav, counter: 1]

	}


}
