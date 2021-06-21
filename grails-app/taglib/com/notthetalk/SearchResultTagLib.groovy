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

class SearchResultTagLib {

	def displaySearchResult = { attrs, body ->

		def post = Post.get(attrs.result.id)


		out << "<div><a href='" << createLink(controller: "discussion", action: "listfrom", id: post.id) << "' >"
		out << post.discussion.folder.description << " - " << post.discussion.title
		out << "</a></div>"

		out << "<div>" << post.user.username << " - " << formatDate(format: "dd-MMM-yyyy HH:mm") << "</div>"
		out << TextFormatter.formatText(post.text, out)

	}
}
