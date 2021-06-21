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

//import org.compass.core.engine.SearchEngineQueryParseException
import grails.plugins.springsecurity.Secured

class SearchController {

	def searchEngine
	def springSecurityService

	@Secured(['ROLE_USER'])
    def index = {

		if (!params.q?.trim()) {
            return [:]
        }
		else {
	        try {

				def user = springSecurityService.currentUser
				def ip = request.getHeader("x-forwarded-for") ?: request.getRemoteAddr()
				def history = new SearchHistory(user: user, ipAddress: ip, query: params.q, searchDate: new Date())
				history.save(flush:true)

				def results = searchEngine.search(params.q, params.int("max"), params.int("offset"))
	            return [searchResult: results.hits, params: params, total: results.count]

	        }
			catch (Exception ex) {
				println ex.toString()
	            return [parseException: true, message: ex.getMessage()]
	        }
		}
    }

	@Secured(['ROLE_ADMIN'])
    def indexAll = {
		/*
        Thread.start {
            searchableService.index()
        }

        render("bulk index started in a background thread")
		*/
    }

	@Secured(['ROLE_ADMIN'])
    def unindexAll = {
		/*
        searchableService.unindex()
        render("unindexAll done")
        */
    }

}
