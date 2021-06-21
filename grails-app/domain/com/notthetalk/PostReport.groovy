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

class PostReport {

	String comment
	Date createdDate
	Double score
	User user
	String ipaddress
	String email
	String name

	static belongsTo = [ post:Post ]

    static constraints = {
		user nullable:true
		comment blank:false, maxSize:512
		ipaddress blank:false, maxSize:15
		email blank:false, maxSize:64, email:true
		name blank:false, maxSize:64
    }
}
