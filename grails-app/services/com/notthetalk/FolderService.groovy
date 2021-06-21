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

class FolderService {

    static transactional = false
	static scope = "singleton"

	def folderMap

	def springSecurityService

	def init = {

		folderMap = new HashMap()
		for(folder in Folder.getAll()) {
			folderMap.put folder.folderKey, folder.id
		}

	}

    def getIdForKey(key) {

		if(!folderMap) {
			init()
		}

		if(!folderMap[key]) {
			init()
		}

		return folderMap[key]
    }

	def getFolders() {

		def user = springSecurityService.currentUser

		if(user && user.options.sortFoldersByActivity) {
			return getFoldersByActivity()
		}
		else {
			return getFoldersByName()
		}
	}

	def getFoldersByActivity() {

		def crit = Folder.createCriteria()
		def folders = crit {
			eq("type", Folder.TYPE_NORMAL)
			order("activity", "desc")
		}

		return folders

	}

	def getFoldersByName() {

				def crit = Folder.createCriteria()
				def folders = crit {
					eq("type", Folder.TYPE_NORMAL)
					order("type", "desc")
					order("description", "asc")
				}

				return folders

	}

	def getHeadlines() {

		def crit = GuardianHeadline.createCriteria()
		def folders = crit {
			isNull("discussionId")
			order("publicationDate", "desc")
		}

		return folders

	}

	def userIsSubscribed(folder, user) {
		def sub = FolderSubscription.findByFolderAndUser(folder, user)
		if(sub) {
			return true
		}
		else {
			return false
		}
	}

	def subscribeToFolder(folder, user) {

		def sub = FolderSubscription.findByFolderAndUser(folder, user)

		if(!sub) {

			log?.debug "Creating subscription..."

			sub = new FolderSubscription(folder:folder, user:user, lastRead: new Date(), createdDate: new Date())
			sub.validate()
			if(!sub.hasErrors()) {
				sub.save(failOnError:true)
			}
			else {
				log?.debug "...failed"
				sub.errors.each {
					log?.debug it.toString()
				}
			}
		}

	}

	def unsubscribeFromFolder(folder, user) {

		def sub = FolderSubscription.findByFolderAndUser(folder, user)

		if(sub) {
			sub.delete(failOnError:true)
		}

	}

}
