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
<!DOCTYPE html>
<html>
<head>
	<meta name="layout" content="main"></meta>
	<title>Posters on Pre-Mod</title>
</head>
<body>

	<div class="centrecolumn-nofolders">

		<div class="margin12">
			<h2>On Pre-Mod</h2>
			<g:each in="${preusers}" var="user">
				<div>
					<g:link controller='home' action='user' id='${user.username}'>${user.username}</g:link>
				</div>
			</g:each>

			<br/>
			<h2>On Watch</h2>
			<g:each in="${watchusers}" var="user">
				<div>
					<g:link controller='home' action='user' id='${user.username}'>${user.username}</g:link>
				</div>
			</g:each>

			<br/>
			<h2>Locked</h2>
			<g:each in="${lockusers}" var="user">
				<div>
					<g:link controller='home' action='user' id='${user.username}'>${user.username}</g:link>
				</div>
			</g:each>

		</div>

	</div>

</body>
