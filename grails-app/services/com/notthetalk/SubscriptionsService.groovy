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

import org.hibernate.transform.Transformers;

class SubscriptionsService {

    static transactional = false

	def springSecurityService
	def sessionFactory

    def getDiscussionSubs() {

		def user = springSecurityService.currentUser
		def session = sessionFactory.getCurrentSession()

		def query = session.createSQLQuery("{call GetSubscriptions(?, ?)}")
			.addScalar("discussionId", org.hibernate.Hibernate.LONG)
			.addScalar("discussionName", org.hibernate.Hibernate.STRING)
			.addScalar("folderId", org.hibernate.Hibernate.LONG)
			.addScalar("folderKey", org.hibernate.Hibernate.STRING)
			.addScalar("folderName", org.hibernate.Hibernate.STRING)
			.addScalar("lastPost", org.hibernate.Hibernate.TIMESTAMP)
			.addScalar("lastPostId", org.hibernate.Hibernate.LONG)
			.addScalar("postCount", org.hibernate.Hibernate.INTEGER)
			.setResultTransformer( Transformers.aliasToBean(FrontPageEntry.class) )

		query.setLong(0, user.id)
		query.setInteger(1, user.options.subscriptionSortOrder ?: 0)

		return query.list()

		/*
		def user = springSecurityService.currentUser
		def order = user.options.subscriptionSortOrder ?: 0

		def subs = null
		def q = "from Subscription s where s.user.id = :userid order by "

		switch(order) {
			case UserOptions.SORT_SUBS_FOLDER:
				q += "s.discussion.folder.description"
				break;
			case UserOptions.SORT_SUBS_DISCUSSION:
				q += "s.discussion.title"
				break;
			case UserOptions.SORT_SUBS_DATE:
				q += "s.discussion.createdDate desc"
				break;
			case UserOptions.SORT_SUBS_RECENT:
				q += "s.discussion.lastPost desc"
				break;
		}

		return Subscription.executeQuery(q, [userid: user.id])
		*/
    }

	def getFolderSubs() {

		def user = springSecurityService.currentUser

		def q = FolderSubscription.createCriteria()
		return q {
			eq("user", user)
			folder {
				order("description", "asc")
			}
		}

	}

	def setSortOrder(order) {
		def user = springSecurityService.currentUser
		user.options.subscriptionSortOrder = order
		user.options.save(failOnError: true)
	}

	def getUpdatedDiscussions() {
		def user = springSecurityService.currentUser
		return getUpdatedDiscussions(user)
	}

	def getUpdatedDiscussionsById(userId) {
		def user = User.get(userId)
		return getUpdatedDiscussions(user)
	}

	def getUpdatedDiscussions(user) {

		def session = sessionFactory.getCurrentSession()

		def query = session.createSQLQuery("{call GetUpdatedDiscussions(?,?)}").addEntity(FrontPageEntry.class)

		query.setLong(0, user.id)
		query.setInteger(1, user.options.subscriptionSortOrder)

		return (List<RecentDiscussion>)query.list()

	}


}
