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

import grails.web.JSONBuilder

class FolderTagLib {

	def getFoldersJSON = { attrs, body ->

			def builder = new JSONBuilder()
			def folders = attrs.folders

			def result = builder.build {
				array {
					folders.each {
						def f = it
						if(f.type == 0) {
							folder = {
								id = f.id
								description = f.description
							}
						}
					}
				}
			}

			out << "<script type=\"text/javascript\">\r\n"
			out << "var folders = " << result.toString() << "\r\n"
			out << "</script>\r\n"


		}


}
