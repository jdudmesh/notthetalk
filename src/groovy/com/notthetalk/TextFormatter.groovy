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

class TextFormatter {

	static def fixLineBreaks(line) {
		throw new Exception()
	}

	static def formatText(text) {

		StringBuilder sb = new StringBuilder()

		// Fix the formatting according to the old GUT rules
		def match = text =~ /https?:\/\/([-\w\.]+)+(:\d+)?\S+\/?/
		match.each {
			text = text.replace(it[0], "<a href='" + it[0].decodeHTML() + "'>" + it[0] + "</a>" )
		}

		def lines = text.split("\\r?\\n");

		sb << "<p>"

		for(line in lines) {

			if(line ==~ /&gt; .*$/) {
				sb << "<p class='post-quoted'>" + line.substring(5) + "</p> "
			}
			else if(line ==~ /s .*$/) {
				sb << " <span class='post-strikethrough'>" + line.substring(2) + "</span> "
			}
			else if(line ==~ /b .*$/) {
				sb << " <span class='post-bold'>" + line.substring(2) + "</span> "
			}
			else if(line ==~ /i .*$/) {
				sb << " <span class='post-italic'>" + line.substring(2) + "</span> "
			}
			else if(line ==~ /c .*$/) {
				sb << " <p class='post-centre'>" + line.substring(2) + "</p> "
			}
			else if(line ==~ /u .*$/) {
				sb << " <span class='post-underline'>" + line.substring(2) + "</span> "
			}
			else if(line ==~ /` .*$/) {
				sb << " <div style='margin-left: 36px;margin-right: 36px;'><code>" + line.substring(2) + "</code></div> "
			}
			else if(line ==~ /\* .*$/) {
				sb << "<ul class='post-noindentbullet'><li>" + line.substring(2) + "</li></ul> "
			}
			else if(line ==~ /^\]+ .*$/) {
				def indent = (line =~ /^\]+/)[0].size()
				sb << "<p style='margin-left: " + (indent * 8).toString() + "px;'>" + line.substring(indent + 1) + "</p>"
			}
			else if(line ==~ /} .*$/) {
				sb << "<p>" + line.substring(2) + "</p>"
			}
			else if(line.length() == 0) {
				sb << "<br/><br/>"
			}
			else {
				sb << line
			}

		}

		sb << "</p>"

		return renderLineBreaks(sb.toString(), 50)

	}

	static def renderLineBreaks(text, max) {

		StringBuilder sb = new StringBuilder()
		def tag = false
		def width = 0
		text.each {

			switch(it) {
				case '<':
					tag = true
					break
				case '>':
					tag = false
					width = 0
					break
				case ' ':
				case '-':
				case '\n':
					width = 0
					break
				default:

					if(!tag) {
						width++
					}

					if(width > max) {
						sb << "<br />"
						width = 0
					}
			}

			sb << it
		}

		return sb.toString();

	}

	static def formatTextRaw(text, out) {

		// Dump out raw text
		def lines = text.split("\\r?\\n");

		for(line in lines) {
			if(line ==~ /&gt; .*$/) {
				out << line.substring(5) << "\r\n"
			}
			else if(line ==~ /s .*$/) {
				out << line.substring(2)
			}
			else if(line ==~ /b .*$/) {
				out << line.substring(2)
			}
			else if(line ==~ /i .*$/) {
				out << line.substring(2)
			}
			else if(line ==~ /c .*$/) {
				out << line.substring(2)
			}
			else if(line ==~ /u .*$/) {
				out << line.substring(2)
			}
			else if(line ==~ /\* .*$/) {
				out << "*" << line.substring(2) << "\r\n"
			}
			else if(line ==~ /^\]+ .*$/) {
				def indent = (line =~ /^\]+/)[0].size()
				out << "\t" << line.substring(indent + 1) << "\r\n"
			}
			else if(line ==~ /} .*$/) {
				out << line.substring(2) << "\r\n"
			}
			else if(line.length() == 0) {
				out << "\r\n"
			}
			else {
				out << line
			}

		}

	}


}
