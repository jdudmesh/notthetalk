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

<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_MODERATOR">

	<g:if test="${ post.moderationScore > 0 || post.status > 0 }">

		<%
			out << "<div class=\"margin12 "
			switch(post.status) {
				case 1:
					out << "post-moderated-suspended"
					break;
				case 2:
					out << "post-moderated-deleted"
					break;
				default:
					out << "post-moderated"
					break;
			}
			out << "\" id=\"moderate_" << post.id << "\""
			out << ">"

		%>

		<div class="moderation-actions">
			<div id="moderate_action1_${post.id}" style="align: right;">
			</div>
			<div id="moderate_action2_${post.id}" style="align: right;">
			</div>
		</div>

		<div class="moderation-report">

			<g:if test="${post.status > 0 }">
				<div style="margin-bottom: 24px; width: 66%;" id="post_${post.id}">
					<g:formatPost text="${post.text}" />
				</div>
			</g:if>

			<div class="margin12">
				<p style="text-decoration: underline; font-weight: bold;">Summary</p>
				<div>Number of reports: ${post.reports.size() }</div>
				<div>Moderation Score: <g:formatNumber number="${post.moderationScore }" format="#0.00" /></div>
				<div>Moderation Result:
					<% switch(post.moderationResult) {
						case -2:
							out << "Delete"
							break
						case 0:
							out << "Pending"
							break
						case 2:
							out << "Keep"
							break
					} %>
				</div>
			</div>

			<div class="margin12">
				<p style="text-decoration: underline; font-weight: bold;">Reports</p>
				<g:each var="report" in="${post.reports}">
					<div>
						<div style="text-decoration: underline;">
							<g:formatDate date="${report.createdDate}" type="datetime" format="dd/MMM/yyyy HH:mm" /> - <g:if test="${report.user}">${report.user.username }</g:if><g:else>Anonymous</g:else>
						</div>
						<div>(${report.email })</div>
						<div>${report.comment }</div>
					</div>
				</g:each>
			</div>

			<div class="margin12">
				<p style="text-decoration: underline; font-weight: bold;">Moderator Comments</p>
				<g:each var="comment" in="${post.moderatorComments}">
					<div>
						<div style="text-decoration: underline;">
							<g:formatDate date="${comment.createdDate}" type="datetime" format="dd/MMM/yyyy HH:mm" /> - ${comment.user.username}
							(<% switch(comment.result) {
								case -1:
									out << "Delete"
									break
								case 0:
									out << "No Action"
									break
								case 1:
									out << "Keep"
									break
							} %>)
						</div>
						<div>${comment.comment }</div>
					</div>
				</g:each>
			</div>


		</div>

		<% out << "</div>" %>

	</g:if>

</sec:ifAnyGranted>

