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
	dojo.addOnLoad(function(){dojo.byId("q").focus();});
</script>

</head>
<body>
  <div class="centrecolumn-nofolders">
  	<h2 class="margin12">Search for posts</h2>

	<div id="search_header" class="margin12">
		<g:form url='[controller: "search", action: "index"]' id="searchableForm" name="searchableForm" method="get">
			<g:textField name="q" value="${params.q}" size="50"/> <input type="submit" value="Search" />
			<g:hiddenField name="max" value="50" />
			<g:hiddenField name="offset" value="0" />
		</g:form>
		<div style="clear: both; display: none;" class="hint">See <a href="http://lucene.apache.org/java/docs/queryparsersyntax.html">Lucene query syntax</a> for advanced queries</div>
	</div>

	<g:if test="${parseException}">
		<div class="rederror">${message}</div>
	</g:if>
	<g:else>
		<g:if test="${searchResult != null}">

			<g:if test="${searchResult.size() == 0}">
				<h3>Your query returned no results</h3>
			</g:if>
			<g:else>
				<div style="margin-top: 24px;" class="margin12"><h2>${total} Posts Found</h2></div>
				<div style="clear: both;">
					<g:each in="${searchResult}" var="res">
						<g:renderSearchResult postId="${res.id}" />
					</g:each>
				</div>
				<div style="padding-top:24px;border-top: 1px solid #c0c0c0; clear: both;">
					<g:paginate controller="search" action="index" params="${params}" max="50" offset="0" total="${total}"/>
				</div>
			</g:else>

		</g:if>
		<g:else>
			<div style="margin-top: 24px;">
				<h3>How to search</h3>
				<br />
				<p>To search the text of posts simply type the keywords you are looking for and click 'Search'.</p>
				<br />
				<p>For more advanced searches you can specify which fields to search. For example, to search for a specific
				user enter username:johnnythesailor.</p>
				<br />
				<p>You can combine search terms using the 'and' keyword, for example 'username: johnnythesailor and poppy'.
				The available search fields are: 'post' (the text of a post), 'username' (the poster), 'thread' (the title
				of the discussion containing the post), 'folder' (the folder the discussion is in) and 'date', (the date of
				the post). Note that the date is stored in the same format as posts (e.g. 22 Nov 2011).</p>
				<br />
				For complete instructions on query syntax see <a href="http://lucene.apache.org/java/3_4_0/queryparsersyntax.html">this article</a>.
			</div>
		</g:else>
	</g:else>

  </div>
</body>
</html>