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
<title>Signup Confirmation</title>
</head>
<body>
  <div class="body">
  <div><img id="toplogo" src="http://www.notthetalk.com/images/logo.png"/></div>

  <p>Dear ${user.username }</p>
  <br/>
  <p>Thank you for signing up to NOTtheTalk. Welcome aboard! We hope that you enjoy the time you spend here. In order to prevent abuse
  of the site you must confirm your e-mail address before you can log in. To confirm your e-mail address, click on the link...</p>
  <div>
	  <a href="http://talk.notthetalk.com/signup/confirm/${conf.confirmationKey}">http://talk.notthetalk.com/signup/confirm/${conf.confirmationKey}</a>
  </div>
  <p>Once you have confirmed your e-mail address you can start posting but your initial posts will be moderated automatically. Don't panic, we do this to
  stop people abusing the site and its users. You'll soon be a fully fledged poster!</p>
  <br/>
  <p>If you have any problems, please contact <a href="mailto:help@justthetalk.com">help@justthetalk.com</a></p>
  <br/>
  <p>If you forget your password you can reset it by following this link: <a href="http://notthetalk.com/login/forgotpassword">http://notthetalk.com/login/forgotpassword</a></p>
  <br/>
  <br/>
  <p>Best Regards,</p>
  <br/>
  <p>NOTtheTalk</p>

  </div>
</body>
</html>