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

import grails.web.JSONBuilder
import groovy.xml.MarkupBuilder
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class PostTagLib {

	def ONE_HOUR = 3600

	def markdownService
	def redisService
	def userService
	def springSecurityService

	def renderPostFromId = { attrs, body ->

		def post = Post.get(attrs.postId)
		def user = springSecurityService.currentUser

		def postItem = new PostListItem(id: post.id,
			userId: post.userId,
			showModerationReport: (post.status == 1 || post.status == 4 || post.moderationScore > 0),
			isLastPost : true,
			postNum: post.postNum,
			createdDate: post.createdDate,
			status: post.status)

		out << buildPost(postItem, user, post)

	}

	def renderPost = { attrs, body ->

		def post = attrs.post
		def user = springSecurityService.currentUser

		def postItem = new PostListItem(id: post.id,
			userId: post.userId,
			showModerationReport: (post.status == 1 || post.status == 4 || post.moderationScore > 0),
			isLastPost : true,
			postNum: post.postNum,
			createdDate: post.createdDate,
			status: post.status)

		out << buildPost(postItem, user, post)

	}

	def renderPostCached = { attrs, body ->

		def postItem = attrs.postItem
		def postCount = attrs.postCount

		out << buildCachedPost(postItem,
			springSecurityService.currentUser,
			postCount,
			null)

	}

	def formatPost = { attrs, body ->
		out << TextFormatter.formatText(attrs.text)
	}

	def renderLineBreaks = { attrs, body ->
		TextFormatter.renderLineBreaks attrs.text, attrs.int('max'), out
	}

	def renderSearchResult = { attrs, body ->

		def post = Post.get(attrs.postId)
		def user = springSecurityService.currentUser

		def g = new ApplicationTagLib()
		def w = new StringWriter()
		def b = new MarkupBuilder(new IndentPrinter(w, "", false))
		b.setDoubleQuotes(true)

		if(post) {
			b.div("class": "postouter margin12", id: "post_$post.id") {

				div(class: "postheader margin12 bold") {
					a(href: g.createLink(controller:"discussion", action:"listfrom", id:post.id, fragment: "post_$post.id"), "$post.discussion.title - $post.discussion.folder.description")
				}

				if(user && userService.userIgnoresUserId(user, post.userId) && post.status != Post.STATUS_POSTED_BY_NOTTHETALK) {
					b.div("class":"margin12 post-ignored", style:"clear: both;", "Post by ignored user")
				}
				else {

					b.div {
						buildTopLine(post, b)
					}

					if(!post.isOk()) {
						b.div("class": "post-status-message margin12", style: "color: red;", post.statusText())
					}
					else {
						buildPostBody(post, b)
					}
				}
			}
		}
		else {
			b.div("class": "postouter margin12") {
				b.div("class": "post-status-message margin12", style: "color: gray;", "Post not found")
				log?.error("Post not found: $attrs.postId")
			}
		}

		out << w.toString()

	}

	def buildCachedPost(postItem, user, postCount, post) {

		def markup = buildPost(postItem, user, post)
		if(postCount) {
			markup = (markup =~ /<span class='POSTCOUNT'>\d+<\/span>/).replaceFirst("<span>$postCount</span>")
		}

		return markup
	}

	def buildPost(postItem, user, post) {

		def w = new StringWriter()
		def b = new MarkupBuilder(new IndentPrinter(w, "", false))
		b.setDoubleQuotes(true)

 		b.div("class": "postouter margin12", id: "post_$postItem.id") {

			if(postItem.isLastPost) {
				b.div(id:"last", class:"postouter"){
					mkp.comment("this is the last post")
				}
			}

			if(SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN,ROLE_MODERATOR")) {

				post = post ?: Post.get(postItem.id)

				b.div("class":"postheader") {
					buildTopLine(post, b)
				}

				buildMenu(postItem, user, b)

				if(user && userService.userIgnoresUserId(user, postItem.userId) && postItem.status != Post.STATUS_POSTED_BY_NOTTHETALK) {
					b.div("class":"margin12 post-ignored", style:"clear: both;", "Post by ignored user")
				}

				if(post.status == Post.STATUS_POSTED_BY_NOTTHETALK) {
					b.div(class:"post-status-message margin12", style:"color: red", "Posted by: $post.user.username")
				}

				if(!post.isOk()) {
					b.div("class": "post-status-message margin12", style: "color: red;", post.statusText())
				}

				buildPostBody(post, b)

				if(postItem.showModerationReport) {
					buildModerationReport(post, user, b)
				}

			}
			else {

				if(user && userService.userIgnoresUserId(user, postItem.userId) && postItem.status != Post.STATUS_POSTED_BY_NOTTHETALK) {
					b.div("class":"margin12 post-ignored postheader", "Post by ignored user")
				}
				else {

					if(postItem.status == Post.STATUS_OK || postItem.status == Post.STATUS_WATCH) {
						buildMenu(postItem, user, b)
					}

					def markup = redisService.memoize("post:$postItem.id", [expire: ONE_HOUR]) {
						post = post ?: Post.get(postItem.id)
						def childWriter = new StringWriter()
						def childMarkup = new MarkupBuilder(new IndentPrinter(childWriter, "", false))

						buildPostMain(post, user, childMarkup)

						return childWriter.toString()

					}

					mkp.yieldUnescaped(markup)

				}

			}

		}

		return w.toString()
	}

	def buildPostMain(post, user, b) {

		b.div("class": "postheader") {

			if(!post.isOk()) {
				b.div("class": "post-status-message", post.statusText())
			}
			else {
				buildTopLine(post, b)
			}
		}

		if(post.isOk()) {
			buildPostBody(post, b)
		}


	}

	def buildPostBody(post, b) {

		def g = new ApplicationTagLib()

		b.div("class":"margin12 postbody") {
			if(post.markdown) {
				mkp.yieldUnescaped(markdownService.markdown(post.text))
			}
			else {
				mkp.yieldUnescaped(TextFormatter.formatText(post.text))
			}
		}

	}

	def buildTopLine(post, b) {

		def g = new ApplicationTagLib()
		def username = post.status == Post.STATUS_POSTED_BY_NOTTHETALK ? "NOTtheTalk" : userService.getUsername(post.userId)
		def userlink = g.createLink(controller: "home", action: "user", id: username);

		def fmt = new SimpleDateFormat("dd MMM yyyy HH:mm:ss")
		def postDate = fmt.format(post.createdDate)

		b.div ("class": "posttitle") {
			span("class": "bold") {
				a(href: userlink, username)
			}
			mkp.yield(" - $postDate (#")
			a(href: g.createLink(controller:"discussion", action:"listfrom", id:post.id), post.postNum)
			mkp.yield(" of ")
			b.span(class:"POSTCOUNT", post.discussion.postCount)
			mkp.yield(")")
		}

	}

	def buildMenu(postItem, user, b) {

		def g = new ApplicationTagLib()

		b.div("class":"postmenu", "id":"menu_$postItem.id") {
			b.div(dojoType:"dijit.form.DropDownButton", showLabel:"false") {
				b.div(style:"display: none;", "Tools")
				b.div(dojoType:"dijit.TooltipDialog", style:"display: none; width: 180px;") {

					if(user && user.id != postItem.userId) {
						b.div(style:"margin-bottom: 6px;") {
							b.a(href:"javascript: ignorePoster($postItem.id);", class:"dropdownitem", "Ignore this poster")
						}
					}

					b.div(style:"margin-bottom: 6px;") {
						b.a(href:"javascript: reportPost($postItem.id);", class:"dropdownitem", "Report this post")
					}

					if(user && postItem.userId == user?.id) {
						b.div(class:"dropdownbreak", "")
						def age = new Date().getTime() - postItem.createdDate.getTime()
						if(age < 3600000) {
							b.div(style:"margin-bottom: 6px;") {
								b.a(href:"javascript:editPost($postItem.id);", class:"dropdownitem", "Edit")
							}
						}
						b.div(style:"margin-bottom: 6px;") {
							b.a(href:"javascript:deletePost($postItem.id);", class:"dropdownitem", "Delete")
						}

						div(class:"dropdownbreak", "")

						div(style:"margin-bottom: 6px;") {
							a(href:"javascript:shareFacebook($postItem.id);", class:"dropdownitem", "Share")
						}
						div(style:"margin-bottom: 6px;") {
							a(href:"javascript:shareTwitter($postItem.id);", class:"dropdownitem", "Tweet")
						}
					}

					if(SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN")) {
						b.div(class:"dropdownbreak", "")
						b.div(style:"margin-bottom: 6px;") {
							if(postItem.status == Post.STATUS_DELETED_BY_ADMIN) {
								b.a(href:"javascript:adminUndelete($postItem.id);", class:"dropdownitem", "Admin Un-delete")
							}
							else {
								b.a(href:"javascript:adminDelete($postItem.id);", class:"dropdownitem", "Admin Delete")
							}
						}
					}
				}
			}

		}

		b.noscript {

			div(style:"clear:both; margin-bottom: 4px;") {

				a(href: g.createLink(controller: "post", action: "report", "id": postItem.id), "Report")

				if(user && postItem.userId == user?.id) {
					def age = new Date().getTime() - postItem.createdDate.getTime()
					if(age < 3600000) {
						a(href: g.createLink(controller: "post", action: "edit", "id": postItem.id, params:[start: postItem.postNum]), "Edit")
					}
					a(href: g.createLink(controller: "post", action: "delete", "id": postItem.id, params:[start: postItem.postNum - 1]), "Delete")
				}

				if(SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN")) {
					if(postItem.status == Post.STATUS_DELETED_BY_ADMIN) {
						a(href: g.createLink(controller: "post", action: "admin_undelete", "id": postItem.id, params:[start: postItem.postNum - 1]),"Admin Undelete")
					}
					else {
						a(href: g.createLink(controller: "post", action: "admin_delete", "id": postItem.id, params:[start: postItem.postNum - 1]), "Admin Delete")
					}
				}


			}

		}

	}

	def buildModActions(post, b) {

		b.div("class":"moderation-actions") {

			div("id":"moderate_action1_$post.id", style:"align: right;") {
				div(dojoType:"dijit.form.DropDownButton", label:"Vote Keep") {

					div(dojoType:"dijit.TooltipDialog", style:"display: none;", postId:"$post.id", label: "Vote Keep") {
						div(dojoType:"dijit.form.Form", id:"FormKeep$post.id", jsId:"formKeep$post.id", encType:"multipart/form-data", action:"", method:"") {
							div {
								label(for:"comment", "Comment")
							}
							div {
			 					input(type: "text", required: true, name:"comment", dojoType:"dijit.form.ValidationTextBox", style:"width:320px;", "")
							}
							div {
								input(type:"hidden", name:"id", value:"$post.id")
								input(type:"hidden", name:"mod_action", value:"MOD_KEEP")
								button(dojotype:"dijit.form.Button", type:"button", onClick:"submitModerationResult(formKeep$post.id, $post.id);", "Save")
							}
						}
					}
				}
			}

			div("id":"moderate_action2_$post.id", style:"align: right;") {
				b.div(dojoType:"dijit.form.DropDownButton", label:"Vote Delete") {

					div(dojoType:"dijit.TooltipDialog", style:"display: none;", postId:"$post.id", label: "Vote Delete") {
						div(dojoType:"dijit.form.Form", id:"FormDelete$post.id", jsId:"formDelete$post.id", encType:"multipart/form-data", action:"", method:"") {
							div {
								label(for:"comment", "Comment")
							}
							div {
			 					input(type: "text", required: true, name:"comment", dojoType:"dijit.form.ValidationTextBox", style:"width:320px;", "")
							}
							div {
			 					input(type:"hidden", name:"id", value:"$post.id")
								input(type:"hidden", name:"mod_action", value:"MOD_DELETE")
			 					button(dojotype:"dijit.form.Button", type:"button", onClick:"submitModerationResult(formDelete$post.id, $post.id);", "Save")
							}
						}
					}

				}
			}
		}
	}

	def buildModSummary(post, b) {

		NumberFormat formatter = new DecimalFormat("#0.00");

		b.p(style:"text-decoration: underline; font-weight: bold;", "Summary")
		b.div("Number of reports: " + post.reports?.size().toString())
		b.div("Moderation Score: " + formatter.format(post.moderationScore))
	}

	def buildModResult(post, b) {
		b.div {
			mkp.yield("Moderation Result: ")
			if(post.moderationResult < 0) {
				mkp.yield("Delete")
			}
			else if(post.moderationResult > 0) {
				mkp.yield("Keep")
			}
			else {
				mkp.yield("Pending")
			}
		}
	}

	def buildModReports(post, b) {

		def g = new ApplicationTagLib()

		b.div(class:"margin12") {
			p(style:"text-decoration: underline; font-weight: bold;", "Reports")
			post.reports.each {
				def report = it
				div(class:"margin12") {
					div(style:"text-decoration: underline;") {
						mkp.yield(g.formatDate(date:report.createdDate, type:"datetime", format:"dd/MMM/yyyy HH:mm"))
						mkp.yield(" - ")
						if(report.user) {
							mkp.yield(report.user.username)
						}
						else {
							mkp.yield("Anonymous")
						}
					}

					div("($report.email)")

					div(report.comment)

				}
			}
		}
	}

	def buildModComments(post, b) {

		def g = new ApplicationTagLib()

		b.div(class:"margin12") {
			b.p(style:"text-decoration: underline; font-weight: bold;", "Moderator Comments")

			post.moderatorComments.each {
				def comment = it
				b.div(class:"margin12") {
					b.div(style:"text-decoration: underline;") {
						mkp.yield(g.formatDate(date:comment.createdDate, type:"datetime", format:"dd/MMM/yyyy HH:mm"))
						mkp.yield(" - $comment.user.username (")
						switch(comment.result) {
							case -1:
								mkp.yield("Delete")
								break
							case 0:
								mkp.yield("No Action")
								break
							case 1:
								mkp.yield("Keep")
								break
						}
						mkp.yield(")")
					}

					b.div(comment.comment)

				}
			}
		}

	}

	def buildModerationReport(post, user, b) {

		def g = new ApplicationTagLib()
		String cssClass = "post-moderated"

		switch(post.status) {
			case Post.STATUS_SUSPENDED_BY_ADMIN:
				cssClass = "post-moderated-suspended"
				break
			case Post.STATUS_DELETED_BY_ADMIN:
				cssClass = "post-moderated-deleted"
				break
			case Post.STATUS_POSTED_BY_NOTTHETALK:
				cssClass = "post-moderated-deleted"
				break
			case Post.STATUS_WATCH:
				cssClass = "post-moderated-watch"
				break
			case Post.STATUS_DELETED_BY_USER:
				cssClass = "post-moderated-deletedbyuser"
				break
		}

		b.div("class":cssClass, "id":"moderate_$post.id") {

			def comment = ModeratorComment.findByUserAndPost(user, post)
			if(!comment) {
				buildModActions(post, b)
			}

			b.div(class:"moderation-report") {

				if(post.status > 0 && post.status != Post.STATUS_WATCH) {
					b.div(style:"margin-bottom: 24px; width: 66%;", id:"post_${post.id}") {
						buildPostBody(post, b)
					}
				}

				b.div(class:"margin12") {

					buildModSummary(post, b)

					buildModResult(post, b)

					buildModReports(post, b)

					buildModComments(post, b)


				}
			}


		}

	}

}
