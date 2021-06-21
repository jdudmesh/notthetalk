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


import org.apache.commons.logging.LogFactory

class PageTimerFilters {

	private static final log = LogFactory.getLog(this)

	def filters = {
		all(controller:'*', action:'*') {


			before = {
				try {
					flash.pageProcessStart = new Date().getTime()
				}
				catch(Exception e) {
					log?.error(e)
				}
			}

			afterView = { ex ->
				try {
					def pageProcessEnd = new Date().getTime()
					def pageProcessStart = flash.pageProcessStart
					response.addIntHeader("X-PageProcessTime", (Integer)(pageProcessEnd - pageProcessStart))
				}
				catch(Exception e) {
					log?.error(e)
				}
			}

		}
	}
}
