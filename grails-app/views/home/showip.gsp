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
<title>NOTtheTalk</title>
</head>
<body>

	<div class="centrecolumn-nofolders">

		<div class="margin12">
			<h2>Reverse DNS</h2>
			<div>${rdns}</div>
			<br/>

			<h2>Logins</h2>
			<table>
				<tr><th style="width: 120px">User</th><th style="width: 200px">Location</th><th  style="width: 100px">Last Login</th></tr>
				<g:each in="${logins}" var="login">
					<tr><th><g:link action="user" id="${login.user.username}">${login.user.username}</g:link></th><th>${login.geoLocation}</th><th><g:formatDate format="yyyy-MM-dd" date="${login.lastLogin}"/></th></tr>
				</g:each>
			</table>
		</div>


</body>
</html>