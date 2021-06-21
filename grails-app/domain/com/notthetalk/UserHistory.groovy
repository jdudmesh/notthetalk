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

import com.maxmind.geoip.Location
import com.maxmind.geoip.LookupService
import org.apache.commons.logging.LogFactory

class UserHistory {

	private static final log = LogFactory.getLog(this)

	User user
	Date createdDate
	String eventType
	String eventData

    static constraints = {
		eventData blank:false
		eventType blank:false
    }

	static void CreateUserHistoryLocation(User user, String eventType, String ip) {

		UserHistory hist = new UserHistory()
		hist.user = user
		hist.createdDate = new Date()
		hist.eventType = eventType;

		def addr = ip
		def location = "UNKNOWN"

		try {

			addr = InetAddress.getByName(ip)

			LookupService cl = new LookupService("/usr/local/share/GeoIP/GeoLiteCity.dat", LookupService.GEOIP_MEMORY_CACHE );
			Location l1 = cl.getLocation(ip)
			if(l1 != null) {
				location = l1.city + ", " + l1.countryName
			}

		}
		catch(Exception e) {
			log?.error(e)
		}


		hist.eventData = ip + " (" + addr + ")" + ": " + location;

		hist.save()

	}

	static void CreateUserHistory(User user, String eventType, String eventData) {

		UserHistory hist = new UserHistory()
		hist.user = user
		hist.createdDate = new Date()
		hist.eventType = eventType
		hist.eventData = eventData

		hist.save()

	}

	static void CreateUserHistory(User user, String eventType, User admin) {

		UserHistory hist = new UserHistory()
		hist.user = user
		hist.createdDate = new Date()
		hist.eventType = eventType;
		hist.eventData = "Actioned by: " + admin.username

		hist.save()

	}

}
