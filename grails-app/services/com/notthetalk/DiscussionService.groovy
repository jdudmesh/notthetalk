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

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils;
import java.util.Date;
import redis.clients.jedis.Jedis
import groovy.transform.Synchronized

class DiscussionService {

	def ONE_HOUR = 3600
    static transactional = true

	def springSecurityService
	def sessionFactory
	def redisService

	@Synchronized
	def save(id, title, header, folderId) {

		def user = springSecurityService.currentUser
		def discussion = null

		user.check()

		if(user.options.premoderate) {
			throw new InvalidUserException(reason:"You cannot start threads while on pre-mod")
		}

		assert id ==~ /\d{1,10}|NEW/

		if(id == "NEW") {

			def folder = Folder.get(folderId)

			discussion = new Discussion(user: user,
				folder: folder,
				title: title,
				header: header,
				createdDate: new Date(),
				lastPost: new Date(),
				postCount: 0,
				status: Discussion.STATUS_OK,
				locked: false,
				zorder: 0)

		}
		else {
			discussion = Discussion.get(id)
			discussion.title = title
			discussion.header = header
		}

		discussion.validate()

		if(!discussion.hasErrors() && discussion.save(flush:true))
		{
			def subscription = Subscription.findByUserAndDiscussion(user, discussion)
			if(!subscription) {
				subscription = new Subscription(user: user, discussion: discussion)
				subscription.save()
			}

			if(id == "NEW") {
				def frontPage = new FrontPageEntry(
							discussionId: discussion.id,
							lastPost: discussion.lastPost,
							lastPostId: null,
							postCount: discussion.postCount,
							discussionName: discussion.title,
							folderId: discussion.folder.id,
							folderName: discussion.folder.description,
							folderKey: discussion.folder.folderKey,
							userId: discussion.user.id,
							adminOnly: discussion.folder.type == Folder.TYPE_ADMIN )

				frontPage.save(flush: true)

				def tokens = header.tokenize(" ,\r\n");
				tokens.each {
					if(it.size() > 2 && it.size() < 16 && it.getAt(0) == "#" && !it.getAt(1).isNumber()) {
						def tag = new DiscussionTag(discussion: discussion, tag: it, weight: 10.0, createdDate: new Date(), lastUpdated: new Date());
						tag.save();
					}
				}


			}
			else {
				def frontPage = FrontPageEntry.findByDiscussionId(discussion.id)
				frontPage.discussionName = discussion.title
				frontPage.save(flush: true)
			}


		}

		return discussion

	}

	def createFromFeed(sourceId, sourceType, folderId) {

		def user = springSecurityService.currentUser
		def discussion = null

		user.check()

		if(user.options.premoderate) {
			throw new InvalidUserException(reason:"You cannot start threads while on pre-mod")
		}

		def folder = Folder.get(folderId)
		def headline = null

		switch(sourceType) {
			case Folder.TYPE_HEADLINES:
				headline = GuardianHeadline.get(sourceId)
				break;
		}

		assert headline

		discussion = new Discussion(
			user: user,
			folder: folder,
			title: headline.titleText(),
			header: headline.headerText(),
			createdDate: new Date(),
			lastPost: new Date(),
			postCount: 0,
			status: Discussion.STATUS_OK,
			locked: false,
			zorder: 0)

		discussion.validate()

		if(!discussion.hasErrors() && discussion.save(flush:true, failOnError:true))
		{
			def subscription = Subscription.findByUserAndDiscussion(user, discussion)
			if(!subscription) {
				subscription = new Subscription(user: user, discussion: discussion)
				subscription.save()
			}

			headline.assignDiscussion(discussion)
			headline.save()

		}
		else {

			discussion.errors.each {
				log?.error it.toString()
			}

			throw new InvalidDiscussionFromHeadlineException()
		}

		return discussion

	}

	def getCurrentDiscussions() {

		def user = springSecurityService.currentUser
		def storedProc;
		def discussions = null

		if(SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN,ROLE_MODERATOR")) {
			storedProc = "{call GetFrontPageAdmin(?)}";
		}
		else if(SpringSecurityUtils.ifAnyGranted("ROLE_USER")) {
			storedProc = "{call GetFrontPage(?)}";
		}
		else {
			storedProc = "{call GetFrontPageAnon()}";
		}

		def session = sessionFactory.getCurrentSession()

		def query = session.createSQLQuery(storedProc)
			.addScalar("discussion_id", org.hibernate.Hibernate.LONG)
			.addScalar("last_post", org.hibernate.Hibernate.TIMESTAMP)
			.addScalar("post_count", org.hibernate.Hibernate.INTEGER)
			.addScalar("discussion_name", org.hibernate.Hibernate.STRING)
			.addScalar("folder_id", org.hibernate.Hibernate.LONG)
			.addScalar("folder_name", org.hibernate.Hibernate.STRING)
			.addScalar("folder_key", org.hibernate.Hibernate.STRING)
			.addScalar("last_post_count", org.hibernate.Hibernate.INTEGER)
			.addScalar("new_post_count", org.hibernate.Hibernate.INTEGER)
			.setResultTransformer(new RecentDiscussionTransformer())

		if(user) {
			query.setLong(0, user.id)
		}

		return (List<RecentDiscussion>)query.list()

    }

    def getDiscussionsByFolder(folder, offset, max) {

		def user = springSecurityService.currentUser
		def session = sessionFactory.getCurrentSession()
		def query = null

		if(user) {
			query = session.createSQLQuery("{call GetDiscussionsByFolder(?,?,?,?)}")
				.addScalar("discussion_id", org.hibernate.Hibernate.LONG)
				.addScalar("last_post", org.hibernate.Hibernate.TIMESTAMP)
				.addScalar("post_count", org.hibernate.Hibernate.INTEGER)
				.addScalar("discussion_name", org.hibernate.Hibernate.STRING)
				.addScalar("folder_id", org.hibernate.Hibernate.LONG)
				.addScalar("folder_name", org.hibernate.Hibernate.STRING)
				.addScalar("folder_key", org.hibernate.Hibernate.STRING)
				.addScalar("last_post_count", org.hibernate.Hibernate.INTEGER)
				.addScalar("new_post_count", org.hibernate.Hibernate.INTEGER)
				.setResultTransformer(new RecentDiscussionTransformer())

			query.setLong(0, user.id)
			query.setLong(1, folder.id)
			query.setInteger(2, offset)
			query.setInteger(3, max)
		}
		else {
			query = session.createSQLQuery("{call GetDiscussionsByFolderAnon(?,?,?)}")
				.addScalar("discussion_id", org.hibernate.Hibernate.LONG)
				.addScalar("last_post", org.hibernate.Hibernate.TIMESTAMP)
				.addScalar("post_count", org.hibernate.Hibernate.INTEGER)
				.addScalar("discussion_name", org.hibernate.Hibernate.STRING)
				.addScalar("folder_id", org.hibernate.Hibernate.LONG)
				.addScalar("folder_name", org.hibernate.Hibernate.STRING)
				.addScalar("folder_key", org.hibernate.Hibernate.STRING)
				.addScalar("last_post_count", org.hibernate.Hibernate.INTEGER)
				.addScalar("new_post_count", org.hibernate.Hibernate.INTEGER)
				.setResultTransformer(new RecentDiscussionTransformer())

			query.setLong(0, folder.id)
			query.setInteger(1, offset)
			query.setInteger(2, max)

		}

		return (List<RecentDiscussion>)query.list()

    }

	def getDeletedDiscussions() {

		def user = springSecurityService.currentUser
		def session = sessionFactory.getCurrentSession()

		def query = session.createSQLQuery("{call GetDeletedDiscussions(?)}")
			.addScalar("discussion_id", org.hibernate.Hibernate.LONG)
			.addScalar("last_post", org.hibernate.Hibernate.TIMESTAMP)
			.addScalar("post_count", org.hibernate.Hibernate.INTEGER)
			.addScalar("discussion_name", org.hibernate.Hibernate.STRING)
			.addScalar("folder_id", org.hibernate.Hibernate.LONG)
			.addScalar("folder_name", org.hibernate.Hibernate.STRING)
			.addScalar("folder_key", org.hibernate.Hibernate.STRING)
			.addScalar("last_post_count", org.hibernate.Hibernate.INTEGER)
			.addScalar("new_post_count", org.hibernate.Hibernate.INTEGER)
			.setResultTransformer(new RecentDiscussionTransformer())

		query.setLong(0, user.id)

		return query.list()
	}

    def getArchivedDiscussionsByFolder(folder, start, num) {

		// need to sort by zorder and last post desc
		// zorder makes posts sticky
		def crit = Discussion.createCriteria()
		def discussions = crit {
			and {
				eq("folder", folder)
				eq("status", Discussion.STATUS_ARCHIVED)
			}
			order("zorder", "desc")
			order("lastPost", "desc")
			firstResult(start)
			maxResults(num)
		}

		return discussions

    }

	def userIsSubscribed(discussion, user) {

		def exception = null
		def discussionSubscription = Subscription.findByUserAndDiscussion(user, discussion)
		def folderSubscription = FolderSubscription.findByFolderAndUser(discussion.folder, user)

		if(folderSubscription) {
			exception = FolderSubscriptionException.findBySubscriptionAndDiscussion(folderSubscription, discussion)
		}

		return (discussionSubscription || (folderSubscription && !exception))

	}

	def delete(discussion) {

		def user = springSecurityService.currentUser

		if(discussion.user.id == user.id && discussion.posts.size() == 0)
		{
			discussion.status = Discussion.STATUS_DELETED_BY_USER
			discussion.save()

			FrontPageEntry.findAllByDiscussionId(discussion.id).each {
				it.delete()
			}
		}
	}

	def adminDelete(discussion) {

		discussion.status = Discussion.STATUS_DELETED_BY_ADMIN
		discussion.save()

		UserHistory.CreateUserHistory(discussion.user, "ADMINDELETE", "Discussion: " + discussion.id.toString() + ", Actioned by: " + springSecurityService.currentUser.username)


		Subscription.findAllByDiscussion(discussion).each {
			it.delete()
		}

		UserDiscussion.findAllByDiscussion(discussion).each {
			it.delete()
		}

		FrontPageEntry.findAllByDiscussionId(discussion.id).each {
			it.delete()
		}

	}

	def adminUndelete(discussion) {

		discussion.status = Discussion.STATUS_OK
		discussion.save()

		UserHistory.CreateUserHistory(discussion.user, "ADMINUNDELETE", "Discussion: " + discussion.id.toString() + ", Actioned by: " + springSecurityService.currentUser.username)

		def frontPage = new FrontPageEntry(
			discussionId: discussion.id,
			lastPost: discussion.lastPost,
			postCount: discussion.postCount,
			discussionName: discussion.title,
			folderId: discussion.folder.id,
			folderName: discussion.folder.description,
			folderKey: discussion.folder.folderKey,
			userId: discussion.user.id )

		frontPage.save(flush: true)


	}

	@Synchronized
	def updateBookmark(lastPost, discussion) {

		try {

			def user = springSecurityService.currentUser

			def bookmark = UserDiscussion.findByUserAndDiscussion(user, discussion)
			if(!bookmark) {
				bookmark = new UserDiscussion(user:user,
					discussion:discussion,
					lastPostId: 0,
					lastPost: new Date(),
					lastPostCount: 0)
			}

			if(lastPost) {
				if(lastPost.id > bookmark.lastPostId) {
					bookmark.lastPostId = lastPost.id
					bookmark.lastPost = lastPost.createdDate
					bookmark.lastPostCount = lastPost.postNum
				}
			}
			else {
				bookmark.lastPostId = 0
				bookmark.lastPost = new Date()
				bookmark.lastPostCount = 0
			}

			bookmark.save(flush:true)

			setCurrentPostCount(discussion.id, user.id, bookmark.lastPostCount)

		}
		catch(Exception e) {
			log?.error(e)
		}

	}

	def setCurrentPostCount(discussionId, userId, postCount) {

		def ONE_HOUR = 3600

		redisService.withRedis { Jedis redis ->
			def key = "user:$userId:discussion:$discussionId"
			redis.setex(key, ONE_HOUR, postCount as String)
		}

	}

	def getCurrentPostCount(discussionId, userId) {

		return redisService.memoize("user:$userId:discussion:$discussionId", [expire: ONE_HOUR]) {

			def session = sessionFactory.getCurrentSession()
			def query = session.createSQLQuery("{call GetUserPostCount(?, ?)}")
			query.setLong(0, userId)
			query.setLong(1, discussionId)

			def result = query.list()

			return (result.size() != 0 ? result[0] : 0).toString()
		}

	}

	def userIsBlockedFromDiscussion(discussion, user) {

		def isBlocked = false

		if(user) {
			def discussionUser = DiscussionUser.findByDiscussionAndUser(discussion, user)
			if(discussionUser && discussionUser.userStatus == DiscussionUser.STATUS_BLOCKED) {
				isBlocked = true
			}
		}

		return isBlocked

	}

}
