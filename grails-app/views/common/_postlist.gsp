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

		<div id="postlist">

			<g:set var="returnURL" value="${URLEncoder.encode(createLink(controller: controllerName, action: actionName, params: params).toString())}" />

			<script type="text/javascript">

				dojo.addOnLoad(function() {
				 	// float the page list dropdown off to the right if we're running script
				 	dojo.query(".pagelist").style({
					 	clear: "none",
					 	float: "right"
					});
				});

 <sec:ifAnyGranted roles="ROLE_ADMIN">

				function adminDelete(postId) {
					window.location.href = "${createLink(controller:'post', action:'adminDelete')}/" + postId + "?return=${returnURL}";
				}

				function adminUndelete(postId) {
					window.location.href = "${createLink(controller:'post',action:'adminUndelete')}/" + postId + "?return=${returnURL}";
				}

</sec:ifAnyGranted>

				function reportPost(postId) {
					window.location.href = "${createLink(controller:'post',action:'report')}/" + postId;
				}

				function ignorePoster(postId) {
					var dlg = dijit.byId("ignoreConfirmDialog");
					dlg.postId = postId;
					dlg.show();
				}

				function ignoreUserConfirmed(dialog) {
					window.location.href = "${createLink(controller:'post',action:'ignore')}/" + dialog.postId + "?return=${returnURL}";
				}

				function editPost(postId) {
					window.location.href = "${createLink(controller:'post',action:'edit')}/" + postId + "?start=${nav.start}";
				}

				function deletePost(postId) {
					var dlg = dijit.byId("deleteConfirmDialog");
					dlg.postId = postId;
					dlg.show();
				}

				function deletePostConfirmed(dialog) {
					window.location.href = "${createLink(controller:'post',action:'delete')}/" + dialog.postId + "?start=${nav.start}";
				}

				function shareFacebook(postId) {

					dojo.xhrPost({
						url: "${createLink(controller:'facebook',action:'share')}/" + postId,
						postData: "POSTIT",
						handleAs: "text",
						load: function(data) {
							},
						error: function(error) {
							alert("Doh! Something went wrong sending this to facebook")
							}
						});
				}
				function shareTwitter(postId) {

					dojo.xhrPost({
						url: "${createLink(controller:'twitter',action:'tweet')}/" + postId,
						postData: "POSTIT",
						handleAs: "text",
						load: function(data) {
							},
						error: function(error) {
							alert("Doh! Something went wrong sending this to Twitter")
							}
						});
				}
			</script>

			<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_MODERATOR">
				<script type="text/javascript">

					function submitModerationResult(form, postId) {

						if(form.validate()) {
							dojo.xhrPost({
								url: "${createLink(controller:'post',action:'submitmoderation')}",
								form: form.id,
								handleAs: "text",
								load: function(data) {

									var dlg = dijit.getEnclosingWidget(form.domNode.parentNode);
									dlg.destroyRecursive();

									var n = "post_" + postId;
									dojo.fadeOut({
										node: n,
										onEnd: function() {
											dojo.place(data, n, "replace");
											dojo.fadeIn({node: n}).play();
										}
									}).play();
									dojo.parser.parse(n);

								},
								error: function(error) {
									var dlg = dijit.getEnclosingWidget(form.domNode.parentNode);
									dlg.destroy();
									alert("Doh! Something went wrong!");
								}
							});

						}
					}

				</script>
			</sec:ifAnyGranted>

			<g:set var="counter" value="${1 }" />
			<g:set var="now" value="${new Date()}" />

			<g:if test="${posts.size() == 0}">
				<div class="margin12 post-deleted">Nobody has posted on this thread yet</div>
			</g:if>

			<!--  actually render each post -->
			<div>
				<g:each var="post" in="${posts}">
					<g:renderPostCached postItem="${post}" postCount="${discussion?.postCount}" />
				</g:each>
			</div>

		</div>

<!--  DIALOGS -->

<div dojoType="dijit.Dialog" id="deleteConfirmDialog" style="display:none" "title="Delete post..." execute="deletePostConfirmed(this);">
	<table style="width: 220px;">
		<tr colspan="2">
			<td>Are you sure you want to delete this post?</td>
		</tr>
		<tr>
			<td align="center"  colspan="2">
	        	<button dojoType="dijit.form.Button" type="submit" onClick="return dijit.byId('deleteConfirmDialog').isValid();">Yes</button>

				<button dojoType="dijit.form.Button" type="button" onClick="dijit.byId('deleteConfirmDialog').hide();">No</button>
			</td>
		</tr>
	</table>
</div>

<div dojoType="dijit.Dialog" id="ignoreConfirmDialog" style="display:none" "title="Ignore Poster..." execute="ignoreUserConfirmed(this);">
	<table style="width: 220px;">
		<tr colspan="2">
			<td>Are you sure you want to ignore this poster?</td>
		</tr>
		<tr>
			<td align="center"  colspan="2">
	        	<button dojoType="dijit.form.Button" type="submit" onClick="return dijit.byId('ignoreConfirmDialog').isValid();">Yes</button>

				<button dojoType="dijit.form.Button" type="button" onClick="dijit.byId('ignoreConfirmDialog').hide();">No</button>
			</td>
		</tr>
	</table>
</div>

<!--  END OF DIALOGS -->