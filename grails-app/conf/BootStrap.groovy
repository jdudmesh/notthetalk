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

import com.notthetalk.SearchEngine
import com.notthetalk.User
import com.notthetalk.Role
import com.notthetalk.UserRole
import com.notthetalk.Folder
import com.notthetalk.SiteVar
import com.notthetalk.BannedWord

class BootStrap {

	def springSecurityService
	def searchEngine

    def init = { servletContext ->

		def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)
		def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)

		def folderUserSpace = Folder.findByDescription("User Space") ?: new Folder(description: "User Space").save(failOnError: true)
		def folderTheHaven = Folder.findByDescription("The Haven") ?: new Folder(description: "The Haven").save(failOnError: true)
		def folderAdvice = Folder.findByDescription("Advice") ?: new Folder(description: "Advice").save(failOnError: true)
		def folderIssues = Folder.findByDescription("Issues") ?: new Folder(description: "Issues").save(failOnError: true)
		def folderFamily = Folder.findByDescription("Family") ?: new Folder(description: "Family").save(failOnError: true)
		def folderEnvironment = Folder.findByDescription("Environment") ?: new Folder(description: "Environment").save(failOnError: true)
		def folderUKNews = Folder.findByDescription("UK News") ?: new Folder(description: "UK News").save(failOnError: true)
		def folderMedia = Folder.findByDescription("Media") ?: new Folder(description: "Media").save(failOnError: true)
		def folderMusic = Folder.findByDescription("Music") ?: new Folder(description: "Music").save(failOnError: true)
		def folderUsa = Folder.findByDescription("USA") ?: new Folder(description: "USA").save(failOnError: true)
		def folderInternational = Folder.findByDescription("International") ?: new Folder(description: "International").save(failOnError: true)
		def folderWorkCareers = Folder.findByDescription("Work and Careers") ?: new Folder(description: "Work and Careers").save(failOnError: true)
		def folderHistory = Folder.findByDescription("History") ?: new Folder(description: "History").save(failOnError: true)
		def folderCrosswords = Folder.findByDescription("Crosswords") ?: new Folder(description: "Crosswords").save(failOnError: true)
		def folderFoodDrink = Folder.findByDescription("Food and Drink") ?: new Folder(description: "Food and Drink").save(failOnError: true)
		def folderITComputers = Folder.findByDescription("IT & Computers") ?: new Folder(description: "IT & Computers").save(failOnError: true)
		def folderEurope = Folder.findByDescription("Europe") ?: new Folder(description: "Europe").save(failOnError: true)
		def folderNotesQueries = Folder.findByDescription("Notes and Queries") ?: new Folder(description: "Notes and Queries").save(failOnError: true)
		def folderArts = Folder.findByDescription("Arts") ?: new Folder(description: "Arts").save(failOnError: true)
		def folderScience = Folder.findByDescription("Science") ?: new Folder(description: "Science").save(failOnError: true)
		def folderMoney = Folder.findByDescription("Money") ?: new Folder(description: "Money").save(failOnError: true)
		def folderSport = Folder.findByDescription("Sport") ?: new Folder(description: "Sport").save(failOnError: true)
		def folderBooks = Folder.findByDescription("Books") ?: new Folder(description: "Books").save(failOnError: true)

	}

	def destroy = {

	}

}
