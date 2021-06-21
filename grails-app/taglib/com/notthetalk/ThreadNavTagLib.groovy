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

class ThreadNavTagLib {


	def displayPagesOld = { attrs, body ->

		/*
		out << "<select dojoType=\"dijit.form.Select\" maxHeight=\"120\">"
		for( i in 0..attrs.max) {
			out << "<option value=\"${i}\" onClick=\"gotoPage(${i * 20});\">${i + 1}</option>"
		}
		out << "</select>"
		*/


		out << "<div dojoType=\"dijit.form.DropDownButton\" label=\"Page...\" maxHeight=\"120\">"
		out << "<div dojoType=\"dijit.Menu\">"

		for( i in 0..attrs.max) {
			out << "<div dojoType=\"dijit.MenuItem\">"
			out << link([action: "list", controller: "discussion", id: attrs.discussionid, params: ["start":i * 20]], { i + 1 })
			out << "</div>"
		}

		out << "</div>"
		out << "</div>"


	}

	def displayPages = { attrs, body ->

		StringBuilder sb = new StringBuilder()


		def map = [:]
		def max = attrs.int("max")
		def start = attrs.int("start")
		def page = attrs.int("page")

		sb << link([action: "all", controller: "discussion", id: attrs.discussionid], "All")
		sb << " "

		for( i in 0..(max > 5 ? 5 : max) ) {
			map[i] = true
		}

		for( i in (max - 5)..max ) {
			map[i] = true
		}

		def mid = (max / 2).toInteger()
		for( i in (mid - 2)..(mid + 2) ) {
			map[i] = true
		}

		def cur = (start / page).toInteger()
		map[cur - 1] = true
		map[cur] = true
		map[cur + 1] = true

		def lasti = -1
		for( i in 0..max) {

			if(map[i]) {

				def outpage = i * page

				if(i != lasti + 1) {
					sb << "&hellip; "
				}

				if(cur >= i && cur < (i + 1) ) {
					sb << i + 1 << " "
				}
				else {
					sb << link([action: "list", controller: "discussion", id: attrs.discussionid, params: ["start": outpage, pageSize: page]],{ i + 1 })
					sb << " "
				}

				lasti = i

			}

		}

		out << "<script type='text/javascript'>\r\n"
		out << "dojo.addOnLoad(function(){"
		out << "dojo.query('.pagelistdropdown').forEach(function(node, index, arr){"
		out << "node.style.display = '';"
		out << "});"
		out << "});"
		out << "</script>"
		out << "<div style='display: none;' class='pagelistdropdown' dojoType=\"dijit.form.DropDownButton\" label=\"Page...\">"
		out << "<div dojoType=\"dijit.TooltipDialog\">"
		out << sb.toString()
		out << "</div>"
		out << "</div>"

		out << "<noscript>"
		out << "<div>"
		out << sb.toString()
		out << "</div>"
		out << "</noscript>"


	}

	def displayNav = { attrs, body ->

		out << "Page "

		def map = [:]
		def x = attrs.max

		for( i in 0..(attrs.max > 2 ? 2 : attrs.max) ) {
			map[i] = true
		}

		for( i in (attrs.max - 2)..attrs.max ) {
			map[i] = true
		}

		def cur = (attrs.curidx / attrs.page).toInteger()
		map[cur - 1] = true
		map[cur] = true
		map[cur + 1] = true

		def lasti = -1
		for( i in 0..attrs.max) {

			if(map[i]) {

				def page = i * attrs.page

				if(i != lasti + 1) {
					out << "&hellip; "
				}

				if(cur >= i && cur < (i + 1) ) {
					out << i + 1 << " "
				}
				else {
					out << link([action: "list", controller: "discussion", id: attrs.discussionid, params: ["start":page]],{ i + 1 })
					out << " "
				}

				lasti = i

			}

		}
		//<g:link action="list" controller="discussion" id="${discussion.id}" params="[start:page]">${i }</g:link>

	}
}
