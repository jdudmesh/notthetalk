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
<html>
<head>
<meta name="layout" content="main"/>

<title>NOTtheTalk - ${discussion.title} (${discussion.folder.description})</title>

 <script type="text/javascript">
	dojo.require("dijit.form.DropDownButton");
	dojo.require("dijit.form.Textarea");
	dojo.require("dijit.TooltipDialog");
	dojo.require("dijit.Menu");
	dojo.require("dijit.Dialog");
	dojo.require("dijit.form.Button");
	dojo.require("dojo.fx");
 	dojo.require("dijit.form.Form");
 	dojo.require("dijit.form.Button");
 	dojo.require("dijit.form.ValidationTextBox");
	dojo.require("dijit.TooltipDialog");
</script>

</head>
<body>

	<g:if test="${user?.hasFacebook()}">
		<div id="fb-root"></div>
		<script src="http://connect.facebook.net/en_US/all.js"></script>
		<script>
		  FB.init({
		    appId  : '135030003232702',
		    status : true, // check login status
		    cookie : true, // enable cookies to allow the server to access the session
		    xfbml  : true  // parse XFBML
		  });
		</script>
	</g:if>

	<g:set var="d_id" value="${discussion.id}" />
	<g:set var="returnURLTop" value="${URLEncoder.encode(createLink(controller: controllerName, action: actionName, params: params).toString())}" />
	<g:set var="returnURLBottom" value="${URLEncoder.encode(createLink(controller: controllerName, action: actionName, params: params, fragment: 'last').toString())}" />

<sec:ifLoggedIn>
	<g:if test="${discussion && nav.isLastPage}">
		<script type="text/javascript">

			var discussionId = "${discussion.id}";
			var lastPostId = ${discussion.lastPostId ?: 0};
			var lastPostCount = ${discussion.postCount};
			var newPostsIndicatorVisible = false;
			var newPostsVisible = false;

			<g:if test="${!iphone}">
			dojo.addOnLoad(function() {

				var socket = io.connect("${grailsApplication.config.notthetalk.eventsUrl}");

				  socket.on('connect', function (data) {
				    socket.emit('subscribe', {topic: "discussion_" + discussionId} );
				  });

				  socket.on('update', function (msg) {
					if(msg.postCount > lastPostCount) {

						if(newPostsVisible) {
							dojo.place("<span>There are " + (msg.postCount - lastPostCount).toString() + " new post(s).</span>" , "newpostsindicator", "only");
						}
						else {
							dojo.place("<span>There are " + (msg.postCount - lastPostCount).toString() + " new post(s). Click to display...</span>" , "newpostsindicator", "only");
						}

						if(!newPostsIndicatorVisible) {

							dojo.fx.chain([
								dojo.fx.wipeIn({node: "newpostsborder" }),
								dojo.fx.wipeIn({node: "newpostsindicator" })
							]).play();

							newPostsIndicatorVisible = true;
						}

						if(newPostsVisible) {
							getLatestPosts();
						}

					}
				  });

			});
			</g:if>

			function getLatestPosts() {

				dojo.xhrGet({
					url: "${createLink(controller:'discussion', action:'getpostsafter', id:discussion.id)}?lastPostId=" + lastPostId,
					handleAs: "text",
					load: function(data) {
						dojo.place(data, "newpostshere", "only");
						if(!newPostsVisible) {
							dojo.fx.wipeIn({node: "newpostshere", onEnd: function() { dojo.parser.parse(dojo.byId("newpostshere")); } }).play();
							newPostsVisible = true;
						}
						else {
							dojo.parser.parse(dojo.byId("newpostshere"));
						}
					},
					error: function(error) {
						console.log(error);
					}
				});
			}

		</script>
	</g:if>
</sec:ifLoggedIn>

   	<div id="folders">
		<sec:ifLoggedIn>
	   		<div >
   				<g:link action="bottom" controller="discussion" id="${discussion.id}" fragment="post">Post a message</g:link>
   			</div>
   			<div>
   				<g:if test="${discussion.status == 0}">
					<g:if test="${nav.subscribed}">
						<g:link action="unsubscribe" controller="subscriptions" id="${discussion.id}" params="[return: returnURLTop]">Unsubscribe</g:link>
					</g:if>
					<g:else>
						<g:link action="subscribe" controller="subscriptions" id="${discussion.id}" params="[return: returnURLTop]">Subscribe</g:link>
					</g:else>
				</g:if>
			</div>
   		</sec:ifLoggedIn>
   		<div class="margin12">&nbsp;</div>
   		<sec:ifAnyGranted roles="ROLE_ADMIN">
   			<div>
   				<g:if test="${discussion.locked}">
   					<g:link action="unlock" controller="discussion" id="${discussion.id}" params="[return: returnURLTop]">Unlock</g:link>
   				</g:if>
   				<g:else>
   					<g:link action="lock" controller="discussion" id="${discussion.id}" params="[return: returnURLTop]">Lock</g:link>
   				</g:else>
   			</div>
   			<div>
   				<g:if test="${discussion.premoderate}">
   					<g:link action="unpremoderate" controller="discussion" id="${discussion.id}" params="[return: returnURLTop]">Stop Pre-moderating</g:link>
   				</g:if>
   				<g:else>
   					<g:link action="premoderate" controller="discussion" id="${discussion.id}" params="[return: returnURLTop]">Pre-moderate</g:link>
   				</g:else>
   			</div>
 			<sec:ifAnyGranted roles="ROLE_ADMIN">
   				<div>
	   				<g:if test="${discussion.status == com.notthetalk.Discussion.STATUS_DELETED_BY_ADMIN}">
	   					<g:link action="adminundelete" controller="discussion" id="${discussion.id}">Undelete discussion</g:link>
	   				</g:if>
	   				<g:else>
	   					<g:link action="admindelete" controller="discussion" id="${discussion.id}">Delete discussion</g:link>
	   				</g:else>
   				</div>
   				<div>
   					<g:link action="edit" controller="discussion" id="${d_id}">Edit discussion</g:link>
   				</div>
   			</sec:ifAnyGranted>
   		</sec:ifAnyGranted>
	</div>


	<div class="centrecolumn">

		<div class="threadouterheader margin12">

			<div id="threadtop" class="threadheading margin12">

				<div class="margin12">
					Started by <span class="bold"><g:link controller="home" action="user" id="${discussion.user.username.encodeAsURL() }">${discussion.user.username}</g:link></span> on <g:formatDate date="${discussion.createdDate}" type="datetime" style="MEDIUM"/>
				</div>

				<div class="margin12">
					<span class="bold"><g:renderDiscussionHeader text="${discussion.title}" max="40"/></span>
				</div>

			</div>

			<div class="headertext margin12">
				<g:formatPost text="${discussion.header}" />
		  	</div>

			<g:if test="${discussion.locked}"><div class="margin12 threadlocked">[This thread is now closed to further comments]</div></g:if>

		</div>

		<g:if test="${discussion.id}">
			<sec:ifLoggedIn>

				<g:if test="${user.hasFacebook()}">
					<fb:like href="${createLink(controller:'discussion',action:'list',id:discussion.id,absolute:true) }" show_faces="false" width="416" font="">
					</fb:like>
				</g:if>

				<g:if test="${discussion.canEdit(user.id)}">
					<div id="margin12">
				   		<g:link action="edit" controller="discussion" id="${d_id}">Edit discussion</g:link> |
				   		<g:link action="delete" controller="discussion" id="${d_id}">Delete discussion</g:link>
				   		<br/><br/>
				   	</div>
				</g:if>
			</sec:ifLoggedIn>
		</g:if>

		<g:if test="${nav.numPosts >= 1 }">
			<g:render template="/common/threadnav" bean="${discussion }"/>
		</g:if>
		<g:else>
			<div class="bordered">&nbsp;</div>
		</g:else>


		<g:render template="/common/postlist" model="[posts:posts]" />

		<div id="newpostsborder" class="bordered" style="clear: both; display: none;"></div>
		<div id="newpostsindicator" class="margin12" style="display:none" onclick="getLatestPosts();"></div>
		<div id="newpostshere"></div>


		<g:render template="/common/threadnav" bean="${discussion}"/>


		<div class="margin12 threadnav">
			<g:link action="index" controller="home" >Home</g:link>
			<sec:ifLoggedIn>
				<g:if test="${discussion.id}">

					<g:if test="${nav.subscribed}">
						| <g:link action="unsubscribe" controller="subscriptions" id="${discussion.id}" params="[return: returnURLBottom]">Unsubscribe</g:link>
					</g:if>
					<g:else>
						| <g:link action="subscribe" controller="subscriptions" id="${discussion.id}" params="[return: returnURLBottom]">Subscribe</g:link>
					</g:else>
					| <g:link controller="subscriptions" action="check">Check Subscriptions</g:link>
				</g:if>
			</sec:ifLoggedIn>
		</div>


		<g:if test="${(nav.isLastPage || flash.postid != 'NEW') && !discussion.locked  && !nav.isBlocked}">

			<sec:ifLoggedIn>
				<g:form name="post" controller="post" action="save" class="margin12 bordered" useToken="true">

					<div class="margin12 top24">
						Write your reply here...
					</div>


					<div class="margin12">
						<g:textArea name="reply" class="posttextarea">${flash.messagetext}</g:textArea>
						<g:if test="${flash.posterror} != null">
							<div class="rederror" style="margin-top: 12px;">${flash.posterror}</div>
						</g:if>
					</div>
					<sec:ifAnyGranted roles="ROLE_ADMIN">
						<div class="margin12"><br/><g:checkBox name="postasadmin" value="${false}" /> Post as 'NOTtheTalk'</div>
					</sec:ifAnyGranted>

					<g:hiddenField name="discussionid" value="${discussion.id}"></g:hiddenField>
					<g:hiddenField name="postid" value="${flash.postid}"></g:hiddenField>
					<g:hiddenField name="folderid" value="${discussion.folder.id}"></g:hiddenField>
					<g:hiddenField name="start" value="${nav.start}"></g:hiddenField>
					<g:hiddenField name="sourceid" value="${nav.sourceid}"></g:hiddenField>
					<g:hiddenField name="sourcetype" value="${nav.sourcetype}"></g:hiddenField>

					<div class="margin12">
						<g:if test="${discussion.folder.type == com.notthetalk.Folder.TYPE_NORMAL || discussion.folder.type == com.notthetalk.Folder.TYPE_ADMIN}">
							<g:submitButton name="submit" value="Post Reply" />
						</g:if>
						<g:else>
							<g:render template="/common/posttofolder" />
						</g:else>
					</div>


				</g:form>

				<div class="margin12 bordered">
				You cannot rewrite history, but you will have 30 minutes to make any
				changes or fixes after you post a message. Just click on Edit in the
				drop down which appears in your message after you post it.
				</div>

			</sec:ifLoggedIn>
		</g:if>
		<g:if test="${nav.isBlocked}">
			<div class="rederror" style="margin-top: 12px;">You have been blocked from posting on this thread.</div>
		</g:if>

	</div>

</body>
</html>