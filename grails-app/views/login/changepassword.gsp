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
<title>Change Password</title>
</head>
<body>

  	<div class="centrecolumn-nofolders">
  		<h2 class="margin12">Change Password</h2>

		<g:form name="savepassword" action="savechangedpassword" autocomplete="false">

			<table id="userDetails">

				<tr><td class="signupcol1">New Password</td><td><g:passwordField name="newPassword" maxlength="12" style="width: 100px;" /><br/><span class="instruction">(Between 6 and 12 characters)</span></td></tr>
				<tr><td>Confirm New Password</td><td><g:passwordField name="newPasswordConfirm" maxlength="12" style="width: 100px;" /></td></tr>

				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>

				<tr><td><g:submitButton name="submit" value="Change password..." /></td><td>&nbsp;</td></tr>

				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>

			</table>

		</g:form>

  		<br/>
  		<g:each var="err" in="${flash.errors }">
			<div class="rederror"><g:message code="${err }" /></div>
  		</g:each>

	</div>


</body>
</html>