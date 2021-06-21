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

<h3 class="margin12">Current Discussion Subscriptions</h3>
<g:if test="${discussionSubs == null || discussionSubs.size() == 0}">
	You don't have any discussion subscriptions at the moment<br/>
</g:if>
<g:else>
	<div class="margin12" style="font-style:italic;">
		To unsubscribe tick the appropriate discussions and click the "Unsubscribe" button at the bottom of the list.
	</div>

	<g:form name="unsubscribe" controller="subscriptions" action="unsubselected" useToken="true">
		<table>
			<g:each var="sub" in="${discussionSubs}">
				<tr>
						<td style="width:24px;"><input type="checkbox" name="unsub" value="${sub.discussionId}"></td> <td><g:discussionTitle header="${sub}" max="1000" showfolders="${true}" /></td>
						<!--  (<g:link controller="subscriptions" action="unsubscribe" id="${sub.discussionId}">Unsubscribe</g:link>)  -->
				</tr>
			</g:each>
		</table>
		<g:submitButton name="submit" value="Unsubscribe" />
	</g:form>

	<br/>
	<div class="margin12">
	Sort by:<br/>
	<g:link controller="subscriptions" action="sort" params="[order:0]">Folder</g:link>
	| <g:link controller="subscriptions" action="sort" params="[order:1]">Discussion Title</g:link>
	| <g:link controller="subscriptions" action="sort" params="[order:2]">Discussion Created</g:link>
	| <g:link controller="subscriptions" action="sort" params="[order:3]">Most Recent</g:link>
	</div>


</g:else>
<br/>
<h3 class="margin12">Current Folder Subscriptions</h3>
<g:if test="${folderSubs == null || folderSubs.size() == 0}">
	You don't have any discussion subscriptions at the moment
</g:if>
<g:else>
	<g:each var="sub" in="${folderSubs}">
		<div class="margin12">
			<g:set var="d_id" value="${sub.folderId}" />
			<g:link controller="folder" action="list" id="${d_id}">
				${sub.folder.description}
			</g:link>
			<br/>
			 (<g:link controller="folder" action="unsubscribe" id="${d_id}">Unsubscribe</g:link>)
		</div>
	</g:each>
</g:else>
