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


<g:getFoldersJSON folders="${folders}" />

<script type="text/javascript">

	function doFormSubmission(menuitem) {
		var hidden = dojo.byId("folderid")
		dojo.attr(hidden, "value", menuitem.folderId);
		dojo.byId("post").submit();
	}

	dojo.addOnLoad(function() {

        var menu = new dijit.Menu({
            style: "display: none;"
        });

	 	// set up the menus for each post
	 	for(var i in folders) {

		 	var f = folders[i];

	        var menuItem1 = new dijit.MenuItem({
	            label: f.description,
	            id: "folder_" + f.id,
	            folderId: f.id,
	            onClick:  function() { doFormSubmission(this); }
	        });

	        menu.addChild(menuItem1);

	 	}

	 	var button = new dijit.form.DropDownButton({
            label: "Start discussion in...",
            showLabel: true,
            name: "post_in_button",
            dropDown: menu,
            id: "post_in_button"
        });

    	dojo.byId("folder_dropdown").appendChild(button.domNode);

	});

</script>

<div id="folder_dropdown">
</div>

<noscript>
<div class="nojavascript">
<p>Your browser does not have javascript enabled or it does not support it. This feature requires javascript. Please
upgrade your browser or enable javascript.</p>
</div>
</noscript>