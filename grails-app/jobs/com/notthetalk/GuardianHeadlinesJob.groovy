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

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import grails.converters.*

import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.util.EntityUtils
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.protocol.HTTP;

class GuardianHeadlinesJob {

	def timeout = 5000l // execute job once in 5 seconds

	static triggers = {
		simple name: 'baseTrigger', startDelay: 60000, repeatInterval: 3600000
	}

    def execute() {

		try {

			processPage(1)

			def deleteDate = DateUtils.addDays(new Date(), -2)

			GuardianHeadline.get().each {
				if(it.publicationDate < deleteDate) {
					it.delete
				}
			}

		}
		catch(Exception e) {
			log?.error e.toString()
		}

	}

	def processPage(start) {

		def d1 = DateUtils.addDays(new Date(), -1)
		def d2 = new Date()

		def fromDate = DateFormatUtils.format(d1, "yyyy-MM-dd")
		def toDate = DateFormatUtils.format(d2, "yyyy-MM-dd")
		def apiKey = grailsApplication.config.guardian.apiKey

		def httpClient = new DefaultHttpClient()
		def url = "http://content.guardianapis.com/search?tag=tone%2fnews&page=$start&page-size=50&from-date=$fromDate&to-date=$toDate&show-fields=headline,trailText&format=json&api-key=$apiKey"

		def requestMethod = new HttpGet(url)

		/*
		NameValuePair[] query = new NameValuePair[8]
		query[0] = new BasicNameValuePair("tag", "tone/news")
		query[1] = new BasicNameValuePair("page", start.toString())
		query[2] = new BasicNameValuePair("page-size", "50")
		query[3] = new BasicNameValuePair("from-date", fromDate)
		query[4] = new BasicNameValuePair("to-date", toDate)
		query[5] = new BasicNameValuePair("show-fields", "headline,trailText")
		query[6] = new BasicNameValuePair("format", "json")
		query[7] = new BasicNameValuePair("api-key", "hjck27n4fad6vhdcujcxxmfz")
		requestMethod.setQueryString(query)
		*/

		def resp = httpClient.execute(requestMethod)

		def body = EntityUtils.toString(resp.getEntity())
		def json = JSON.parse(body)

        if(json.response.status == "ok") {
            json.response.results.each {

				def result = it
				def headline = GuardianHeadline.findByGuardianId(result.id) ?: new GuardianHeadline(guardianId: result.id, deleted: false)

                headline.with {
                    it.section = result.sectionName
                    it.publicationDate = DateUtils.parseDate(result.webPublicationDate.substring(0, 19), "yyyy-MM-dd'T'HH:mm:ss")
                    it.url = result.webUrl
                    it.headline = result.fields.headline
                    it.trailText = result.fields.trailText
				}

                headline.save(failOnError: true, flush: true)

            }

            if(json.response.currentPage < json.response.pages) {
                processPage(start + 1)
            }
        }


    }
}
