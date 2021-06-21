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
<title>Search</title>

<script type="text/javascript">
	dojo.addOnLoad(function(){dojo.byId("searchterm").focus();});
</script>

</head>
<body>
  <div class="centrecolumn-nofolders">
  	<h2 class="margin12">Search for posts</h2>

  	<g:form name="searchform" action="results">
	  	<div class="margin12">
	  		Enter search term <g:textField name="searchterm" maxlength="64" style="width: 240px;"/>
	  	</div>
	  	<div><g:submitButton name="submit" value="Search" /></div>
	 </g:form>

  </div>
</body>
</html>