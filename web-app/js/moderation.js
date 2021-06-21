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

function submitModerationResult(btn) {

    var dlg = dijit.getEnclosingWidget(btn.domNode.parentNode);
    var txt = dojo.query('textarea[name=comment]', dlg.domNode)[0];
    var action = dojo.query('input[name=mod_action]', dlg.domNode)[0];
    var form = dojo.query('form', dlg.domNode)[0];

    if (!txt.value) {
        dlg.destroy();
        alert("You must enter some text");
    } else {

        dojo.xhrPost({
            url: doModerationUrl + dlg.postId,
            form: form,
            handleAs: "text",
            load: function(data) {
                window.location.reload();
                window.location.href = moderateUrl + "#post_" + dlg.postId;
            },
            error: function(error) {
                alert("Doh! Something went wrong!");
            }
        });

        dlg.destroy();

    }
}

function reloadNode(postId) {

    dojo.xhrGet({
        url: getPostUrl + postId,
        handleAs: "text",
        load: function(data) {
            var n = "post_" + postId;
            dojo.fx.wipeOut({ node: n }).play();
            dojo.place(data, n, "only");
            dojo.fx.wipeIn({ node: n }).play();
        },
        error: function(error) {
            alert("Doh! Something went wrong!");
        }
    });

}