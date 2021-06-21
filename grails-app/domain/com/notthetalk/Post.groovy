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

package com.notthetalk

//import org.compass.annotations.*

class Post {

	def searchEngine

	static STATUS_OK = 0
	static STATUS_SUSPENDED_BY_ADMIN = 1
	static STATUS_DELETED_BY_ADMIN = 2
	static STATUS_POSTED_BY_NOTTHETALK = 3
	static STATUS_WATCH = 4

	static STATUS_MAX_DISPLAY = 255
	static STATUS_DELETED_BY_USER = 256
	static STATUS_INVISIBLE = 257

	String text
	Date createdDate
	Double moderationScore
	Integer moderationResult
	Integer status
	Date lastEditDate
	boolean markdown
	Integer postNum

	static belongsTo = [ discussion:Discussion, user:User ]
	static hasMany = [ reports:PostReport, moderatorComments:ModeratorComment ]

    static constraints = {
		text blank: false, maxSize: 8192
		lastEditDate nullable:true
    }

	def isOk() {

		if(!user.enabled) {
			return false
		}
		else {
			return ((status == STATUS_OK || status == STATUS_POSTED_BY_NOTTHETALK || status == STATUS_WATCH) && discussion.status == Discussion.STATUS_OK)
		}

	}

	def canEdit(currentUserId) {
		def now = new Date()
		if(now.getTime() - createdDate.getTime() < 1800000 && user.id == currentUserId) {
			return true
		}
		else {
			return false
		}
	}

	def statusText() {

		def text = ""

		if(!user.enabled) {
			text = "Post by deleted user"
		}
		else if(discussion.status != Discussion.STATUS_OK) {
			text = "Discussion deleted"
		}
		else {
			switch(status) {
				case STATUS_OK:
					break;
				case STATUS_SUSPENDED_BY_ADMIN:
					text = "Post suspended pending moderation"
					break;
				case STATUS_DELETED_BY_USER:
					text = "Post deleted by user"
					break;
				case STATUS_DELETED_BY_ADMIN:
				text = "Post deleted by NOTtheTalk"
					break;
			}
		}

		return text

	}

	def afterInsert() {
		try {
			if(discussion.folder.type == Folder.TYPE_NORMAL && status == STATUS_OK) {
				searchEngine.indexPost(this)
			}
		}
		catch(Exception ex) {
			log?.error(ex)
		}
	}

	def afterUpdate() {

		try {
			if(discussion.folder.type == Folder.TYPE_NORMAL) {
				switch(status) {
					case STATUS_OK:
					searchEngine.updatePost(this)
						break;
					case STATUS_SUSPENDED_BY_ADMIN:
						searchEngine.deletePost(this)
						break;
					case STATUS_DELETED_BY_USER:
						searchEngine.deletePost(this)
						break;
					case STATUS_DELETED_BY_ADMIN:
						searchEngine.deletePost(this)
						break;
				}
			}
		}
		catch(Exception ex) {
			log?.error(ex)
		}

	}

}
