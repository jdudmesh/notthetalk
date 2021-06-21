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
<title>Password Reset</title>
</head>
<body>
  <div class="body">
  <div><img id="toplogo" src="http://www.notthetalk.com/images/logo.png"/></div>

  <p>Dear ${user.username }</p>
  <br/>
  <p>A password reset request has been received for your account. If you did not request this yourself you should contact
  <a href="mailto:help@justthetalk.com">help@justthetalk.com</a>. If you did make this request then please follow
  the link below to rest you password</p>
  <br/>
  <p><a href="${url }">Reset my password...</a></p>
  <br/>
  <p>If your e-mail client will not allow you click on links then please copy and paste the following URL into your browser address bar...</p>
  <br/>
  <p>${url }</p>

  <br/>
  <p>Best Regards,</p>
  <br/>
  <p>NOTtheTalk</p>

  </div>
</body>
</html>