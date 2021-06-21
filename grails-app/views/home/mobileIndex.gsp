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
<meta name="layout" content="mobile"/>
<title>NOTtheTalk</title>

<script type="text/javascript">
	dojo.require("dojo.fx");
	dojo.require("dojox.timing._base");
</script>


</head>
<body>
<sec:ifLoggedIn>
		<script type="text/javascript">

			var subsUpdatesVisible = false;

			dojo.addOnLoad(function() {
				timer = new dojox.timing.Timer(300000);

				timer.onTick = function() {
					getSubsUpdates();
				};

				timer.start();
			});

			function getSubsUpdates() {

				dojo.xhrGet({
					url: "${createLink(controller:'subscriptions', action:'checkAjax')}",
					handleAs: "text",
					load: function(data) {
						if(data) {
							dojo.place(data, "subsupdates", "only");
							if(!subsUpdatesVisible) {
								dojo.fx.wipeIn({node: "subsupdatesouter" }).play();
								subsUpdatesVisible = true;
							}
						}
					},
					error: function(error) {
						console.log(error);
					}
				});

			}

		</script>
</sec:ifLoggedIn>
xxxx
   		<g:if test="${flash.errorMessage}">
   			<div style="width: 100%; text-align: center; float: left;"><h2 class="margin12" style="color: red;">${flash.errorMessage}</h2></div>
   		</g:if>

		<g:render template="/common/folderbar" bean="${folders }" />

    	<div id="posts" class="centrecolumn">
xxxxx
    		<g:if test="${!updates?.size()}">
	    		<div id="subsupdatesouter" style="display: none;" class="margin12">
	    			<h2 class="margin12">Subscriptions</h2>
	    			<div id="subsupdates">
	    			</div>
	    		</div>
    		</g:if>
    		<g:else>
	    		<div id="subsupdatesouter" class="margin12">
	    			<h2 class="margin12">Subscriptions</h2>
	    			<div id="subsupdates">
	    				<g:render template="/common/discussionlist" model="[discussions:updates, postcounts:postcounts]" var="showfolders"/>
	    			</div>
	    		</div>
    		</g:else>

    		<h2 class="margin12">Discussions</h2>

    		<g:render template="/common/discussionlist" model="[discussions:discussions, postcounts:postcounts]" var="showfolders"/>

    	</div>


</body>
</html>