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

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class DonationTagLib {

	def ONE_HOUR = 3600

	def redisService

	def donationFrontPage = { attrs, body ->


		def w = new StringWriter()
		def b = new MarkupBuilder(new IndentPrinter(w, "", false))
		def g = new ApplicationTagLib()

		def vars = SiteVar.get(1)

		b.div(id: "donate") {

			a(href: g.createLink(controller:"home", action:"donations")) {
				b.img(alt:"Donate",  border: "0", src: g.resource(dir:'images', file:"donate$vars.donationStatus" + ".png"))
			}

		}

		out << w.toString()

	}

	def donation = { attrs, body ->


		def w = new StringWriter()
		def b = new MarkupBuilder(new IndentPrinter(w, "", false))
		def g = new ApplicationTagLib()

		def markup = redisService.memoize("donate_status", [expire: ONE_HOUR]) {

			def vars = SiteVar.get(1)

			b.div(id: "donate", class: "margin12") {

				b.div {

					b.form(action: "https://www.paypal.com/cgi-bin/webscr", method: "post") {
						b.input(type: "hidden", name: "cmd", value: "_s-xclick")
						b.input(type: "hidden", name: "hosted_button_id", value: "RHJ5LD727ZK8N")
						b.input(type: "image", src: g.resource(dir:'images', file:"donate$vars.donationStatus" + ".png"), border: "0", name: "submit", alt:"PayPal � The safer, easier way to pay online.")
						b.img(alt:"",  border: "0", src: "https://www.paypalobjects.com/en_GB/i/scr/pixel.gif", width:"1", height: "1")
					}

				}

			}

			return w.toString()
		}

		out << markup

	}

}
