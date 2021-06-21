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

<%@ page contentType="text/html;charset=ISO-8859-1" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="layout" content="main"/>
<title>Search Results</title>
</head>
<body>

	<div class="centrecolumn-nofolders">

		<g:if test="${posts.size()}">

			<h2 class="margin12">${posts.size()} Results</h2>

			<g:each var="post" in="${posts}">
				<div class="margin12 bordered">

					<g:set var="postid" value="${post.id}" />



	   				<g:link action="listfrom" controller="discussion" id="${post.id}">
	   					${post.discussion.folder.description} - ${post.discussion.title} - <g:formatDate date="${post.createdDate}" type="datetime" style="MEDIUM"/>
	   				</g:link>

	   				<br/>

	   				<g:if test="${post.text.length() < 256}"><g:formatPost text="${post.text}" /></g:if>
	   				<g:else><g:formatPost text="${post.text.substring(0, 256)}" />...</g:else>

				</div>
			</g:each>

		</g:if>
		<g:else>
			<h2 class="margin12">No results found</h2>
		</g:else>

	</div>

</body>
</html>