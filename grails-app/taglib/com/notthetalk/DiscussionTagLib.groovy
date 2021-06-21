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

import groovy.xml.MarkupBuilder
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class DiscussionTagLib {

	def ONE_HOUR = 3600

	def springSecurityService
	def redisService
	def discussionService

	def discussionTitle = { attrs, body ->
		out << renderDiscussionTitle(attrs.header, attrs.max, attrs.showfolders)
	}

	def renderDiscussionHeader = { attrs, body ->
		out << TextFormatter.renderLineBreaks(attrs.text, attrs.int('max'))
	}

	def renderDiscussionTitle(dis, max, showFolders) {

		def w = new StringWriter()
		def b = new MarkupBuilder(new IndentPrinter(w, "", false))
		def g = new ApplicationTagLib()

		def user = springSecurityService.currentUser
		def newPostCount = dis.postCount

		def fmt = new SimpleDateFormat("dd MMM yyyy HH:mm:ss")
		def dicussionDate = fmt.format(dis.lastPost)


		if(user) {
			newPostCount = dis.postCount - Integer.parseInt(discussionService.getCurrentPostCount(dis.discussionId, user.id))
		}

		b.div(class:"discussionlist") {

			a(href: g.createLink(controller:"discussion", action:"check", id:dis.discussionId)) {
				mkp.yieldUnescaped(TextFormatter.renderLineBreaks(dis.discussionName, Integer.parseInt((String)max)))
			}

			if((boolean)showFolders) {
				mkp.yield(" - ")
				a(href: g.createLink(controller:"folder", action:"list", id:dis.folderKey)) {
					mkp.yieldUnescaped(dis.folderName)
				}
			}

			b.div {
				mkp.yield("$dicussionDate ($dis.postCount posts, $newPostCount new)")
			}

		}


		return w.toString()

	}


}
