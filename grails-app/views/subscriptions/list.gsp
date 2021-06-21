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
<title>Subscriptions</title>
</head>
<body>

	<g:render template="/common/folderbar" bean="${folders}" />

	<div class="centrecolumn">

		<div style="margin-top: 12px; margin-bottom:24px;">
			<g:link action="check">Check my subscriptions</g:link>
			<div>${flash.actionmessage}</div>
		</div>

		<g:render template="/common/subslist" bean="${subscriptions}" />

	</div>

</body>
</html>