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

<!DOCTYPE html>
<html>
<head>
	<meta name="layout" content="main"></meta>
	<title>Sign Up Here</title>
</head>
<body>

	<div class="centrecolumn-nofolders">

		<div class="margin12">
		Welcome to NOTtheTalk. We are a community of individuals who enjoy discussing anything and everything. There are
		no limits to what can be talked about here but we recommend that you read our charter and terms and conditions
		before you sign up.
		</div>

		<g:form name="signup" action="save">

			<table id="userDetails">
				<tr><td class="signupcol1">Your name on the site</td><td><g:textField name="username" maxlength="24" /><br/><span class="instruction">(Between 6 and 24 characters, letters and numbers only)</span></td></tr>
				<tr><td class="signupcol1">Password</td><td><g:passwordField name="password" maxlength="12" style="width: 100px;" /><br/><span class="instruction">(Between 6 and 12 characters)</span></td></tr>
				<tr><td>Confirm Password</td><td><g:passwordField name="passwordConfirm" maxlength="12" style="width: 100px;" /></td></tr>
				<tr><td>E-Mail Address</td><td><g:textField name="email" maxlength="64" /></td></tr>
				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
				<tr><td colspan="2"><g:checkBox name="checkBoxAccept" value="${false}" /> I accept the <g:link controller="home" action="terms">Terms &amp; Conditions</g:link></td></tr>

				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>

				<tr>
					<td colspan="3">
						<recaptcha:ifEnabled>
							<recaptcha:recaptcha theme="blackglass" />
							<recaptcha:ifFailed />
						</recaptcha:ifEnabled>
					</td>
				</tr>

				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>

				<tr><td><g:submitButton name="submit" value="Sign up now..." /></td><td>&nbsp;</td></tr>

				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>

				<tr><td colspan="3"><g:renderErrors bean="${user}" as="list" /></td></tr>

			</table>

			<g:hiddenField name="bio" value="" />

		</g:form>

	</div>

</body>
</html>