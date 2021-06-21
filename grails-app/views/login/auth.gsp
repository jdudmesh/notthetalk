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

<head>
<meta name='layout' content='main' />
<title>Login</title>
</head>

<body>

	<div class="centrecolumn-nofolders">

		<div id='login'>
			<div class='inner'>

				<g:if test='${flash.message}'>
					<div class='login_message'>${flash.message}</div>
					<br/><br/>
				</g:if>

				<div class='margin12'>Please Login...</div>

				<form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>

					<table>
						<tr>
							<td>User Name</td>
							<td><input type='text' class='text_' name='j_username' id='username' /></td>
						</tr>

						<tr>
							<td>Password</td>
							<td><input type='password' class='text_' name='j_password' id='password' /><td>
						</tr>

						<tr>
							<td></td>
							<td><input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me'
								<g:if test='${hasCookie}'>checked='checked'</g:if> /> Remember me
							</td>
						</tr>

						<tr><td colspan="2">&nbsp;</td></tr>

						<tr>
							<td colspan="2">
								<input type='submit' value='Login' />
							</td>
						</tr>
					</table>
				</form>

				<br/>

				<g:link controller="login" action="forgotpassword">I forgot my password...</g:link>

			</div>
		</div>

	</div>

<script type='text/javascript'>
<!--
(function(){
	document.forms['loginForm'].elements['j_username'].focus();
})();
// -->
</script>
</body>
