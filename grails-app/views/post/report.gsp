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
<title>Report Post</title>
</head>
<body>

	<div class="centrecolumn-nofolders" style="margin-bottom:128px;">


		<p>We will generally only remove posts that are defamatory, racist, homophobic, incite hatred
		or are of a commercial nature. The nature of free speech is such that you are free to disagree
		with other points of view, points of view which you may find offensive but you are not free to
		curtail them.</p>
		<br/>
		<p>Occasionally some posters resort to personal abuse. This can be deeply unpleasant and you
		should use the "Ignore" (see the <g:link controller="home" action="help">Help</g:link> page) feature to block posters who you feel are being abusive towards you. In
		the event that abuse becomes persistent bullying, please report the posts to us.</p>
		<br/>
		<p>In all cases the decision of the moderators is final.</p>
		<br/>
		<div style="text-decoration: underline;font-weight: bold;">Post</div>
		<br/>
		<div>Folder: ${post.discussion.folder.description }</div>
		<div>Discussion: ${post.discussion.header }</div>
		<br/>
		<div class="margin12" style="text-decoration: underline;">Post Content:</div>
		<div class="margin12" id="post_${post.id}">
			<g:formatPost text="${post.text}" />
		</div>

		<div class="bordered">
			<g:form name="reportform" action="savereport" id="${post.id }">

				<table>
					<tr><td>Your Name:</td><td><g:textField name="username" maxlength="24" value="${report.name }"/></td></tr>
					<tr><td>Your E-Mail Address:</td><td><g:textField name="email" maxlength="64" value="${report.email }"/></td></div>
					<tr><td colspan="2">&nbsp;</td></tr>
					<tr><td colspan="2">Please explain why you are reporting this post:</td></tr>
					<tr><td colspan="2"><g:textArea name="comment" class="posttextarea" maxlength="512">${report.comment }</g:textArea></td></tr>
					<tr><td colspan="2">&nbsp;</td></tr>
					<tr>
						<td colspan="3">
							<recaptcha:ifEnabled>
								<recaptcha:recaptcha theme="blackglass" />
								<recaptcha:ifFailed />
							</recaptcha:ifEnabled>
						</td>
					</tr>

					<tr><td><g:submitButton name="submit" value="Report Post" /></td><td>&nbsp;</td></tr>
					<tr><td colspan="2"><g:renderErrors bean="${report}" as="list" /></td></tr>
					<tr><td colspan="2"><h2 style="color:red;">${flash.message}</h2></td></tr>
				</table>

				<g:hiddenField name="postid" value="${post.id }" />
			</g:form>
		</div>

	</div>


</body>
</html>