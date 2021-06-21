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
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://www.facebook.com/2008/fbml">
    <head>

        <title><g:layoutTitle default="not the talk" /></title>

        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

        <link rel="stylesheet" type="text/css" href="http://static.notthetalk.com/scripts/dojo/resources/dojo.css" />
        <link rel="stylesheet" type="text/css" href="http://static.notthetalk.com/scripts/dijit/themes/tundra/tundra.css" />
        <script src="http://static.notthetalk.com/scripts/dojo/dojo.xd.js" type="text/javascript" djConfig="parseOnLoad: true, useXDomain:true" ></script>

        <link rel="stylesheet" href="${resource(dir:'css',file:'mobile.css')}" />

		<link media="only screen and (max-device-width: 480px)" href="${resource(dir:'css',file:'iphone.css')}" type= "text/css" rel="stylesheet" />

        <g:javascript library="application" />

        <g:layoutHead />

<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-21706480-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
    </head>
    <body class="tundra">

			<g:bannerMessage />

    		<div id="header">
    			<g:link controller="home" action="index">
    				<img id="toplogo" src="${resource(dir:'images',file:'logo.png')}"/>
    			</g:link>
    		</div>

    		<div>

				<sec:ifNotLoggedIn>
				  <g:link controller="login" action="auth">Login</g:link> or
				  <g:link controller="signup" action="">Sign Up</g:link>
				</sec:ifNotLoggedIn>

				<sec:ifLoggedIn>
	    			<div>
	    				<sec:username /><br/>
	    				<g:link controller="logout" action="index">Sign Out</g:link>
	    			</div>
	    		</sec:ifLoggedIn>

    		</div>


    		<div>

		    		<div class="toptext">
		   				<g:link controller="home" action="index" class="headerTextLink">Home</g:link>
		   				<g:if test="${nav}">
			   				<g:each var="crumb" in="${nav.breadcrumbs }">
			   					<g:if test="${crumb.link }">
			   						&gt; <g:link controller="${crumb.controller }" action="${crumb.action }" id="${crumb.id }" class="headerTextLink">${crumb.text }</g:link>
			   					</g:if>
			   					<g:else>
			   						&gt; <span class="headerTextLink">${crumb.text }</span>
			   					</g:else>
			   				</g:each>
		   				</g:if>
		    		</div>


			</div>

			<div id="contentmain">

    			<g:layoutBody />

    		</div>

			<div id="rightcontent">
				<h2 class="margin12">Tools</h2>
				<ul class="nobullets">
					<li><g:link controller="home" action="index">Home</g:link></li>

					<sec:ifLoggedIn>
						<li><g:link controller="home" action="bio">Profile</g:link></li>
						<li><g:link controller="subscriptions" action="check">Subscriptions</g:link></li>
						<li><g:link controller="search" action="index">Search</g:link></li>
					</sec:ifLoggedIn>

					<li><g:link controller="home" action="charter">Charter</g:link></li>
					<li><g:link controller="home" action="help">Help</g:link></li>
					<li><g:link controller="home" action="policy">Policy</g:link></li>
					<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_MODERATOR">
						<li><g:link controller="admin" action="moderate">Moderate</g:link></li>
					</sec:ifAnyGranted>
				</ul>
			</div>


    </body>
</html>