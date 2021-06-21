<%--
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
--%>


<g:each var="d" in="${discussions}">

	<div class="discussionlist">

		<div>
			<g:link action="check" controller="discussion" id="${d.discussion_id}"><g:renderDiscussionHeader text="${d.discussion_name}" max="50"/></g:link>
			<g:if test="${nav.showfolders}">
				- <g:link action="list" controller="folder" id="${d.folder_key}">${d.folder_name}</g:link>
			</g:if>
		</div>

		<div>
			<g:formatDate date="${d.last_post}" type="datetime" style="MEDIUM"/> (${d.post_count} posts, ${d.new_post_count} new)
		</div>

	</div>

</g:each>
