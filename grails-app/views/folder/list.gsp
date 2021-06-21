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
<title>NOTtheTalk - ${headerText}</title>
</head>
<body>

   	<div id="folders">
		<sec:ifLoggedIn>
	   		<div class="margin12">

	   			<g:if test="${folder.type == com.notthetalk.Folder.TYPE_NORMAL || folder.type == com.notthetalk.Folder.TYPE_ADMIN}">
   					<g:link action="add" controller="discussion" id="${folder.id}">Start a discussion</g:link>
   				</g:if>
   				<g:if test="${nav.userIsSubscribed}">
   					<g:link action="unsubscribe" controller="folder" id="${folder.id}">Unsubscribe</g:link>
   				</g:if>
   				<g:else>
   					<g:link action="subscribe" controller="folder" id="${folder.id}">Subscribe to folder</g:link>
   				</g:else>

   			</div>
   		</sec:ifLoggedIn>
   		<div class="margin12">&nbsp;</div>
	</div>

   	<div class="centrecolumn">

		<h2 class="margin12">Discussions</h2>


		<g:each var="d" in="${discussions}">
			<div class="discussionlist">
				<g:discussionTitle header="${d}" max="50" showfolders="${nav.showfolders}" />
			</div>
		</g:each>

		<div class="margin12 bordered">
			<g:paginate controller="folder" action="list" total="${numdiscussions}" id="${folder.folderKey}" max="30" offset="0"/>
		</div>

   	</div>

</body>
</html>