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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="layout" content="main"/>
<title>Your Profile</title>
</head>
<body>

	<div class="centrecolumn-nofolders">

  		<g:form name="savebio" action="savebio">

	  		<div class="margin12">
		  		<table>
		  			<tr>
		  				<td>E-Mail Address</td><td><g:textField name="email" maxlength="64" value="${user.email }" /></td>
		  			</tr>
		  			<tr>
		  				<td>&nbsp;</td><td><g:checkBox name="displayemail" value="${user.displayEmail}" /> Show e-mail to the world?*</td>
		  			</tr>
		  			<tr>
		  				<td colspan="2">

			  				<g:if test="${!user.hasFacebook()}">
								<g:link controller="facebook" action="connect">
			  						<img id="facebook-login" src="${resource(dir:'images',file:'facebook-login.png')}"/>
			  					</g:link>
			  				</g:if>

			  				<g:if test="${!user.hasTwitter()}">
	  							<g:link controller="twitter" action="connect">
	  								<img id="twitter-login" src="${resource(dir:'images',file:'sign-in-with-twitter-l.png')}"/>
	  							</g:link>
			  				</g:if>

						</td>
		  			</tr>



		  			<tr><td colspan="2">&nbsp;</td></tr>
		  			<tr>
		  				<td colspan="2">
		  					<div class="margin12">Say something about yourself...</div>
		  					<g:textArea name="bio" class="biotext">${user.bio}</g:textArea>
		  				</td>
		  			</tr>
		  			<tr><td colspan="2">&nbsp;</td></tr>
		  			<tr><td colspan="2">* We don't recommend that you reveal your main e-mail address here - create a free GMail or Hotmail account based on your user name and use that instead.</td></tr>
		  		</table>
			</div>

			<g:hiddenField name="userid" value="${user.id}"></g:hiddenField>

			<div class="margin12">
				<h4 class="margin12">Options</h4>
				<div>
					<ul class="nobullets">
						<li><g:checkBox name="autosubs" value="${user.options.autoSubs}" /> Automatically subscribe to threads I post on</li>
						<!--  <li><g:checkBox name="markdown" value="${user.options.markdown}" /> Use markdown</li>  -->
						<li><g:checkBox name="sortfolders" value="${user.options.sortFoldersByActivity}" /> Sort folder list by activity</li>
					</ul>
				</div>
			</div>
			<br/>
	  		<div><g:submitButton name="submit" value="Update Details" /></div>


	  		<br/>

	  		<div class="margin12">${flash.actionmessage }</div>
	  		<div class="margin12"><g:renderErrors bean="${user}" as="list" /></div>


  		</g:form>

  		<div class="margin12">
  			<g:link controller="login" action="changepassword">Change password...</g:link>
  		</div>

  		<br/>

  		<h3 class="margin12">Ignored Users</h3>
  		<g:if test="${user.ignores.size() }">
  			<g:each var="ignored" in="${user.ignores }">
  				<div class="margin12">
  					${ignored.ignoredUser.username } <g:link controller="post" action="unignoreuser" id="${ignored.ignoredUser.id}">(click here to stop ignoring)</g:link>
  				</div>
  			</g:each>
  		</g:if>
  		<g:else>
  			You haven't ignored any users!
  		</g:else>
  		<br/><br/>

  		<g:render template="/common/subslist" bean="${subscriptions}" />

	</div>

</body>
</html>