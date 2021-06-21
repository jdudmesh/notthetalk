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
<title>Forgotten Password</title>
</head>
<body>

  	<div class="centrecolumn-nofolders">
  		<h2 class="margin12">Request a new password</h2>

  		<div class="margin12">
	  		<p>If you have forgotten your password then please enter your user name and e-mail address in the box below and click 'Send'.</p>
	  		<p>You will receive an e-mail which contains a link which will allow you to reset your password</p>
  		</div>

  		<g:form action="recoverpassword" name="recoverpassword">
  			<label for="username">Enter your user name:</label><br/>
  			<g:textField name="username" maxlength="24"></g:textField>
  			<br/><br/>
  			<label for="email">Enter the e-mail address you registered with:</label>
  			<g:textField name="email" maxlength="64"></g:textField>
  			<br/><br/>
  			<div><g:submitButton name="submit" value="Send" /></div>
  		</g:form>

  		<br/><div class="margin12 rederror">${flash.actionmessage }</div>


	</div>

</body>
</html>