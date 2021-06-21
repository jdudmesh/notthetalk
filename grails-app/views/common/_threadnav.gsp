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

		<div class="bordered margin12 threadnav">

			<div style="float:left;">
				<g:if test="${nav.start > 0}">
					<g:link action="prev" controller="discussion" id="${discussion.id}" params="[last:nav.start]">Previous</g:link>
				</g:if>
				<g:else>
					Previous
				</g:else>
				|
				<g:if test="${!nav.isLastPage}">
					<g:link action="next" controller="discussion" id="${discussion.id}" params="[last:nav.start]">Next</g:link>
				</g:if>
				<g:else>
					Next
				</g:else>

				|
				<g:if test="${discussion.id}">
					<g:link action="top" controller="discussion" id="${discussion.id}">Top</g:link>
				</g:if>
				<g:else>
					Top
				</g:else>
				|
				<g:if test="${discussion.id}">
					<g:link action="bottom" controller="discussion" id="${discussion.id}" fragment="last">Bottom</g:link>
				</g:if>
				<g:else>
					Bottom
				</g:else>

				<sec:ifLoggedIn>
					<g:if test="${discussion.id}">
						| <g:link action="unread" controller="discussion" id="${discussion.id}">Unread</g:link>
					</g:if>
				<g:else>
					| Unread
				</g:else>
				</sec:ifLoggedIn>

			</div>

			<div class="pagelist">
				<g:displayPages max="${nav.maxPage}" page="${nav.pageSize}" start="${nav.start}" cur="${nav.curPage}" discussionid="${discussion.id}" />
			</div>

		</div>
