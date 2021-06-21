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

		<!--
        <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.6/dojo/resources/dojo.css">
        <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.6/dijit/themes/tundra/tundra.css"/>
        <script src="http://ajax.googleapis.com/ajax/libs/dojo/1.6/dojo/dojo.xd.js" type="text/javascript" djConfig="parseOnLoad: true"></script>
         -->

        <link rel="stylesheet" type="text/css" href="http://static.notthetalk.com/scripts/dojo/resources/dojo.css" />
        <link rel="stylesheet" type="text/css" href="http://static.notthetalk.com/scripts/dijit/themes/tundra/tundra.css" />

        <script src="http://static.notthetalk.com/scripts/dojo/dojo.xd.js" type="text/javascript" djConfig="parseOnLoad: true, useXDomain:true" ></script>

        <sec:ifAnyGranted roles="ROLE_HELLBAN">
        	<g:set var="stickIt" value="${new Random().nextDouble()}" />
        	<g:if test="${stickIt < 0.80}">
		        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
		        <!--[if IE 7]><link rel="stylesheet" href="${resource(dir:'css',file:'ie.css')}" /><![endif]-->
		        <!--[if IE 8]><link rel="stylesheet" href="${resource(dir:'css',file:'ie.css')}" /><![endif]-->
		        <!--[if IE 9]><link rel="stylesheet" href="${resource(dir:'css',file:'ie.css')}" /><![endif]-->
		        <!--  hellban ok -->
        	</g:if>
        </sec:ifAnyGranted>

        <sec:ifNotGranted roles="ROLE_HELLBAN">
	        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
	        <!--[if IE 7]><link rel="stylesheet" href="${resource(dir:'css',file:'ie.css')}" /><![endif]-->
	        <!--[if IE 8]><link rel="stylesheet" href="${resource(dir:'css',file:'ie.css')}" /><![endif]-->
	        <!--[if IE 9]><link rel="stylesheet" href="${resource(dir:'css',file:'ie.css')}" /><![endif]-->
		</sec:ifNotGranted>

		<link media="only screen and (max-device-width: 480px)" href="${resource(dir:'css',file:'iphone.css')}" type= "text/css" rel="stylesheet" />

		<script src="${resource(dir:'js',file:'socket.io.min.js')}" type="text/javascript"></script>

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



    	<div id="maincolumn">

			<g:bannerMessage />

    		<div id="header">
    			<g:link controller="home" action="index">
    				<img id="toplogo" src="${resource(dir:'images',file:'logo.png')}"/>
    			</g:link>
    		</div>

    		<div id="topbar">
    			<div style="float:left;">
    				<g:link controller="home" action="index"><img id="gutlogo" src="${resource(dir:'images',file:'gut.png')}"/></g:link>
    			</div>

		    	<div id="contentheader">

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

		    		<div id="contentheader-right">

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

   			<div id="headerText">
   			</div>

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
					<li><g:link controller="home" action="terms">Terms &amp; Conditions</g:link></li>
					<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_MODERATOR">
						<li><g:link controller="admin" action="moderate">Moderate</g:link></li>
						<sec:ifAnyGranted roles="ROLE_ADMIN">
							<li><g:link controller="admin" action="modhistory">Mod History</g:link></li>
							<li><g:link controller="admin" action="premods">Who's on Pre-mod</g:link></li>
							<li><g:link controller="admin" action="recentusers">Recent Signups</g:link></li>
						</sec:ifAnyGranted>
					</sec:ifAnyGranted>

					<g:donationFrontPage />

					<!--
					<div id="donate">
						<div>
							<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
								<input type="hidden" name="cmd" value="_s-xclick">
								<input type="hidden" name="hosted_button_id" value="RHJ5LD727ZK8N">
								<input type="image" src="${resource(dir:'images',file:'donate.png')}" border="0" name="submit" alt="PayPal — The safer, easier way to pay online.">
								<img alt="" border="0" src="https://www.paypalobjects.com/en_GB/i/scr/pixel.gif" width="1" height="1">
							</form>
						</div>
						<div id="donate_help">
							<g:link controller="home" action="donations">What's this?</g:link>
						</div>
					</div>
					 -->
				</ul>
			</div>


    	</div>

        <g:if test="${flash.pagehint == 'ADD'}">
			<img src="http://hummingbird.notthetalk.com:8000/tracking_pixel.gif?events=add&f=${Math.ceil(Math.random() * 1000000)}" />
		</g:if>
		<g:else>
        	<img src="http://hummingbird.notthetalk.com:8000/tracking_pixel.gif?f=${Math.ceil(Math.random() * 1000000)}" />
        </g:else>

    </body>
</html>