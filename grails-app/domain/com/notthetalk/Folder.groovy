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

class Folder {

	static TYPE_NORMAL = 0
	static TYPE_HEADLINES = 1
	static TYPE_TWITTER = 2
	static TYPE_ADMIN = 3

	String description
	String folderKey
	Integer activity
	Integer type

	static hasMany = [ discussions:Discussion ]

    static constraints = {
		description blank:false
    }
}
