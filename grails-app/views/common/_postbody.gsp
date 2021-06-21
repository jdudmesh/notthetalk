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

				<div class="postouter  margin12" id="post_${post.id}">

					<!--  bookmark to mark the last post -->
					<g:if test="${counter == (posts.size())}">
						<div id="last" class="postouter"></div>
					</g:if>

					<!-- display the status text if the post deleted/pending moderation etc  -->
					<g:if test="${!post.isOk(user)}">
						<div class="margin12 bordered" id="post_${post.id}">

							<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_MODERATOR">
								<div class="posttitle">
									<span class="bold"><g:link controller="home" action="user" id="${post.user.username.encodeAsURL() }">${post.user.username}</g:link></span> -
									<g:formatDate date="${post.createdDate}" type="datetime" style="MEDIUM" />
									(#<g:link controller="discussion" action="listfrom" id="${post.id }">${counter + nav.start}</g:link> of ${post.discussion.postCount})
								</div>

								<div class="postmenu" id="menu_${post.id}">
								</div>

							</sec:ifAnyGranted>

							<div class="post-deleted" style="font-weight: bold; clear: both;">${post.statusText()}</div>

						</div>
					</g:if>
					<g:else>
						<!--  if the post is ok then display it -->

						<g:if test="${user && post.user.isIgnoredBy(user)}">
							<div class="margin12 post-ignored bordered" id="post_${post.id}">Post by ignored user</div>
						</g:if>
						<g:else>
							<div class="postheader">

								<div class="posttitle">
									<span class="bold"><g:link controller="home" action="user" id="${post.user.username.encodeAsURL() }">${post.user.username}</g:link></span> -
									<g:formatDate date="${post.createdDate}" type="datetime" style="MEDIUM" />
									(#<g:link controller="discussion" action="listfrom" id="${post.id }">${counter + nav.start}</g:link> of ${post.discussion.postCount})
								</div>

								<div class="postmenu" id="menu_${post.id}">
								</div>

								<noscript>
									<div style="clear:both; margin-bottom: 4px;">
										<g:link controller="post" action="report" id="${post.id}">Report</g:link>

										<g:if test="${user && post.user.id == user.id}">
											<g:if test="${post.canEdit(user.id)}">
												<g:link controller="post" action="edit" id="${post.id}" params="${[start:nav.start]}">Edit</g:link>
											</g:if>
											<g:link controller="post" action="delete" id="${post.id}" params="${[start:nav.start]}">Delete</g:link>
										</g:if>
										<sec:ifAnyGranted roles="ROLE_ADMIN">
											<g:link controller="post" action="admin_delete" id="${post.id}" params="${[start:nav.start]}">Admin Delete</g:link>
										</sec:ifAnyGranted>
									</div>
								</noscript>

							</div>

							<div class="margin12 postbody">
									<g:if test="${post.markdown}">
										<markdown:renderHtml text="${post.text}"/>
									</g:if>
									<g:else>
										<g:formatPost text="${post.text}" />
									</g:else>
							</div>

						</g:else>

					</g:else>

					<g:render template="/common/moderationreport" model="[post:post]" />

				</div>
