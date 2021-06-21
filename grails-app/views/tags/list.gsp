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
<title>NOTtheTalk - ${tag}</title>
</head>
<body>

     <div id="folders">
   		<div class="margin12">&nbsp;</div>
	</div>

   	<div class="centrecolumn">

		<h2 class="margin12">Discussions for ${tag}</h2>

		<g:each var="d" in="${discussions}">
			<div class="discussionlist">
				<g:discussionTitle header="${d}" max="50" showfolders="${nav.showfolders}" />
			</div>
		</g:each>

   	</div>

</body>
</html>