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
	   			<g:if test="${folder.type == 0}">
   					<g:link action="add" controller="discussion" id="${folder.id}">Start a discussion</g:link>
   				</g:if>
   			</div>
   		</sec:ifLoggedIn>
   		<div class="margin12">&nbsp;</div>
	</div>

   	<div class="centrecolumn">

		<h2 class="margin12">Your Timeline</h2>
		<div class="margin12" style="font-style: italic;">Create a discusison about one of your tweets by clicking
		on it below. You'll be prompted to move it to a more relevant folder.</div>

		<g:if test="${tweets?.size()==0}">
			<div class="margin12" style="font-style: italic;">
				You don't have any tweets. This may be because you have not connected your NOTtheTalk account to your
				Twitter account. Visit your profile page to connect both accounts.
			</div>
		</g:if>

   	   	<g:each var="tweet" in="${tweets}">

   			<g:set var="tweetText" value="From Twitter: ${tweet.user.screenName} - ${tweet.text}" />
   			<g:set var="tweetParam" value="${tweetText.encodeAsURL()}" />

   			<div class="discussionlist">

   				<div>
   					<g:link controller="discussion" action="add" id="${folder.id}" params="[title:tweetParam]"><span style="font-weight:bold;">${tweet.user.screenName}</span> - ${tweet.text}</g:link>
   				</div>

   			</div>

   		</g:each>

   	</div>

</body>
</html>