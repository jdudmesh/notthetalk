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
<meta name="layout" content="main"/>
<title>Moderate Posts</title>
 <script type="text/javascript">
	dojo.require("dijit.form.Button");
	dojo.require("dijit.Menu");
	dojo.require("dijit.TooltipDialog");
</script>

</head>
<body>

	<div class="centrecolumn-nofolders">

		<g:each var="user" in="${users}">
			<div class="margin12 bordered">

				<table>
					<tr><td>${user.username}</td><td>${user.email}</td></tr>
					<tr>
						<td>
							<g:form name="user_${user.id}" action="resetpassword">
								<g:textField name="password" />
								<g:hiddenField name="userid" value="${user.id}" />
								<g:submitButton name="submit" value="Change Password" />
							</g:form>
						</td>
					</tr>
				</table>
			</div>
		</g:each>

	</div>

</body>
</html>