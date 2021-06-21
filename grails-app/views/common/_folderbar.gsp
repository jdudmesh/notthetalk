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

    	<div id="folders">
    		<div class="margin12">
	    		<h2 class="margin12">Topics</h2>

	    		<sec:ifAnyGranted roles="ROLE_ADMIN">
	    			<div class="margin12">
		    			<g:link action="list" controller="folder" id="admin">Mod's Corner</g:link><br />
			    		<sec:ifAnyGranted roles="ROLE_ADMIN">
			    			<g:link action="listdeleted" controller="folder">Deleted Threads</g:link><br />
			    		</sec:ifAnyGranted>
			    	</div>
	    		</sec:ifAnyGranted>

	    		<div class="margin12">
		    		<div class="margin2"><g:link action="headlines" controller="folder">Headlines</g:link></div>
		    		<div class="margin2"><g:link action="twitter" controller="folder">Twitter</g:link></div>
	    		</div>

	    		<g:each var="folder" in="${folders}">
	    			<div class="margin2">
	    				<g:link action="list" controller="folder" id="${folder.folderKey}">${folder.description}</g:link>
	    			</div>
	    		</g:each>

			</div>

    		<div class="margin12">
    			<h2 class="margin12">Tags</h2>
	    		<g:each var="tag" in="${tags}">
	    			<div class="margin2">
	    				<g:link action="list" controller="tags" id="${tag.tag}">${tag.tag}</g:link>
	    			</div>
	    		</g:each>
    		</div>

    	</div>
