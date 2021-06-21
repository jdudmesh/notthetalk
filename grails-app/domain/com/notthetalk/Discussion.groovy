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

class Discussion {

	static searchable = [only: ["title", "header"]]
	//static searchable = {
	//	title boost: 1.0
	//	header boost: 1.0
	//}

	static STATUS_OK = 0
	static STATUS_DELETED_BY_USER = 1
	static STATUS_DELETED_BY_ADMIN = 2
	static STATUS_ARCHIVED = 1024
	static STATUS_AGED = 1025

	String title
	String header

	Date createdDate
	Date lastUpdated

	Date lastPost
	Integer zorder
	boolean locked
	boolean premoderate = false
	Integer postCount
	Integer status
	Long lastPostId;

	static belongsTo = [ folder:Folder, user:User ]
	static hasMany = [ posts:Post, bookmarks:UserDiscussion, subscriptions:Subscription ]

    static constraints = {
		title blank: false, size:1..255, unique:true
		header blank: false, size:1..1024
		lastPostId nullable:true
    }

	def canEdit(currentUserId) {
		if(posts?.size() == 0 && user.id == currentUserId) {
			return true
		}
		else {
			return false
		}
	}

}
