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

import grails.converters.JSON
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils;
import redis.clients.jedis.Jedis
import org.hibernate.transform.Transformers;
import groovy.transform.Synchronized

class PostService {

	static USERID_NOTTHETALK = 1

	static DELETE_THRESHOLD = -2
	static KEEP_THRESHOLD = 2
	static PREMOD_DELETE_THRESHOLD = -1
	static PREMOD_KEEP_THRESHOLD = 1

	static VOTE_FOR = 1
	static VOTE_AGAINST = -1

	def ONE_HOUR = 3600

    static transactional = true

	def springSecurityService
	def discussionService
	def subscriptionsService
	def sessionFactory
	def redisService
	def postTagLib
	def rabbitService

	@Synchronized
    def savePost(discussionId, postId, postText, folderId, sourceId, sourceType, postAsAdmin, ip) {

		def user = springSecurityService.currentUser
		def post = null
		def oldtext = ""
		def isedit = false
		def discussion = null

		if(!discussionId) {
			switch(sourceType) {
				case Folder.TYPE_HEADLINES:
					discussion = discussionService.createFromFeed(sourceId, sourceType, folderId)
					break;
			}
		}
		else {
			discussion = Discussion.get(discussionId)
		}

		assert discussion

		if(discussion.locked) {
			throw new InvalidPostException(reason: "The thread is locked")
		}

		// reawaken a thread if it gets posted on
		if(discussion.status == Discussion.STATUS_ARCHIVED) {
			discussion.status = Discussion.STATUS_OK
		}

		// can't post on an invalid thread
		if(discussion.status != Discussion.STATUS_OK) {
			throw new InvalidPostException(reason: "The thread is not open for posting")
		}

		if(discussion.folder.type == Folder.TYPE_ADMIN && !SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN,ROLE_MODERATOR")) {
			throw new InvalidUserException(reason:"You do not have permission to post on admin threads")
		}

		user.check()

		//log?.debug postId
		assert postId ==~ /\d{1,10}|NEW/

		// if we're an edit we'll get a postid if not it'll be "NEW"
		if(postId == "NEW")
		{
			post = new Post(user: user,
				discussion: discussion,
				createdDate: new Date(),
				markdown: user.options.markdown,
				moderationScore:0f,
				moderationResult: 0,
				status: Post.STATUS_OK)

			// move the discussion to a real folder if it comes from a feed
			if(discussion.folder.type != Folder.TYPE_NORMAL) {

				assert folderId

				def folder = Folder.get(folderId)
				discussion.folder = folder
			}


		}
		else
		{
			post = Post.get(postId)

			if(post.user.id != user.id) {
				throw new InvalidUserException(reason:"You do not have permission to edit this post")
			}

			def age = new Date().getTime() - post.createdDate.getTime()
			if(age > 3600000) {
				throw new InvalidPostException(reason: "You only have 1 hour to edit your posts. This post is too old.")
			}

			post.lastEditDate = new Date()
			oldtext = post.text
			isedit = true
		}

		post.text = postText

		if(user.options.premoderate || discussion.premoderate) {
			post.status = Post.STATUS_SUSPENDED_BY_ADMIN
		}

		if(user.options.watch) {
			post.status = Post.STATUS_WATCH
		}

		def words = BannedWord.list()
		for(word in words) {
			if(word.isBanned(postText)) {
				post.status = Post.STATUS_SUSPENDED_BY_ADMIN
			}
		}

		if(postAsAdmin && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN,ROLE_MODERATOR")) {
			post.status = Post.STATUS_POSTED_BY_NOTTHETALK;
		}

		if(postId == "NEW") {
			discussion.postCount++
			post.postNum = discussion.postCount
		}

		//if(SpringSecurityUtils.ifAnyGranted("ROLE_INVISIBLE")) {
		//	def rnd = new Random()
		//	if(rnd.nextInt(100) <= user.invisibilityThreshold) {
		//		post.status = Post.STATUS_INVISIBLE
		//	}
		//}
		//else
		post.validate()

		//log?.debug post.discussion.hasErrors()
		//log?.debug post.hasErrors()

		if(!discussion.hasErrors() && !post.hasErrors())
		{
			post.save(flush:true, failOnError:true)

			if(postId == "NEW") {

				discussion.lastPost = post.createdDate;
				discussion.lastPostId = post.id;

				FrontPageEntry.findAllByDiscussionId(discussion.id).each {
					it.delete(flush:true)
				}

				def frontPage = new FrontPageEntry(
					discussionId: discussion.id,
					lastPost: discussion.lastPost,
					lastPostId: discussion.lastPostId,
					postCount: discussion.postCount,
					discussionName: discussion.title,
					folderId: discussion.folder.id,
					folderName: discussion.folder.description,
					folderKey: discussion.folder.folderKey,
					userId: discussion.user.id,
					adminOnly: (discussion.folder.type == Folder.TYPE_ADMIN) )

				frontPage.save(flush:true)

				def tokens = post.text.tokenize(" ,.(\r\n");
				tokens.each {
					if(it.size() > 2 && it.size() < 16 && it.getAt(0) == "#" && !it.getAt(1).isNumber()) {
						def tags = DiscussionTag.findAllByTag(it);
						def tag = null
						if(tags.size() > 0) {
							tag = tags[0]
							tag.weight += 1.0;
							tag.lastUpdated = new Date();
						}
						else {
							tag = new DiscussionTag(discussion: discussion, tag: it, weight: 1.0, createdDate: new Date(), lastUpdated: new Date());
						}
						tag.save();
					}
				}

			}

			discussion.validate()
			if(!discussion.hasErrors()) {
				discussion.save(failOnError:true)
			}

			if(isedit) {
				def edit = new PostEdit(post: post, createdDate: post.lastEditDate, text: oldtext)
				edit.save(flush:true, failOnError:true)
			}

			def subscription = Subscription.findByUserAndDiscussion(post.user, post.discussion)
			if(user.options && user.options.autoSubs) {
				if(!subscription) {
					subscription = new Subscription(user: post.user, discussion: post.discussion)
					subscription.save(failOnError:true)
				}
			}

			addToModQueue(post)

			cachePost(post)

			if(postId == "NEW") {

				//runAsync {

					try {

						/*
						def session = sessionFactory.getCurrentSession()
						def update = new ClientUpdate()


						def query = session.createSQLQuery("{call GetSubscribedUsers(?)}").setLong(0, discussion.id)
						def users = query.list()
						users.each {

							def client = it
							def subs = new SubscriptionUpdate()

							subs.topic = "front_page_" + client.toString()
							subs.markup = ""

							update.data.add(subs)

						}

						def item = new DiscussionUpdate(
							topic: "discussion_" + discussion.id.toString(),
							lastPostId: discussion.lastPostId,
							postCount: discussion.postCount)

						update.data.add(item)

						def json = new JSON(update).toString();

						def httpClient = new DefaultHttpClient()
						def url = "http://events.notthetalk.com:9090/discussion"

						def put = new HttpPut(url)
						put.setHeader("Content-Type", "application/json")
						def entity = new StringEntity(json)
						put.setEntity(entity)

						httpClient.execute(put)
						*/

						def tags = DiscussionTag.findAllByDiscussion(discussion, [max: 10, sort:"weight", order:"desc"])
						def message = [
								user: [username: post.user.username, id: post.user.id],
								folder: [id: discussion.folder.id, name: discussion.folder.description],
								discussion: [id: discussion.id, title: discussion.title, lastPostId: discussion.lastPostId, postCount: discussion.postCount],
								postId: post.id,
								tags: tags
							]

						def routingKey = "ntt." + discussion.folder.id.toString() + "." + discussion.id.toString()
						rabbitService.send(new JSON(message).toString(), routingKey)

					}
					catch(Exception e) {
						log?.error e.toString()
					}

				}
			//}

		}
		else {
			post.errors.allErrors.each {
				println it
			}
			discussion.errors.allErrors.each {
				println it
			}
		}


 		return post

	}

	def deletePost(post) {

		def user = springSecurityService.currentUser

		post.status = Post.STATUS_DELETED_BY_USER
		post.save()

		cachePost(post)

		return post

	}
	/*
	def getPrecedingPost(post) {

		// Find the preceding post in the thread
		def crit = Post.createCriteria()
		def last = crit.get {
			and {
				eq("discussion", post.discussion)
				or {
					between("status", Post.STATUS_OK, Post.STATUS_MAX_DISPLAY)
					and {
						eq("status", Post.STATUS_INVISIBLE)
						eq("user", springSecurityService.currentUser)
					}
				}
				lt("createdDate", post.createdDate)
			}
			order("createdDate", "desc")
			maxResults(1)
		}

		return last

	}
	*/

	/*
	def getLastPostDate() {

		def var = SiteVar.get(1)
		return var.lastPostDate

	}
    */

	/*
	def getFolderLastPostDate(f) {

		def crit = Post.createCriteria()
		def last = crit.get {
			and {
				or {
					between("status", Post.STATUS_OK, Post.STATUS_MAX_DISPLAY)
					and {
						eq("status", Post.STATUS_INVISIBLE)
						eq("user", springSecurityService.currentUser)
					}
				}
				discussion {
					eq("folder", f)
				}
			}
			order("createdDate", "desc")
			maxResults(1)
		}

		return last.createdDate

	}
	*/
	/*
	def getNumPosts(discussion) {

		def crit = Post.createCriteria()
		def last = crit.get {
			projections { count "id" }
			and {
				eq("discussion", discussion)
				or {
					between("status", Post.STATUS_OK, Post.STATUS_MAX_DISPLAY)
					and {
						eq("status", Post.STATUS_INVISIBLE)
						eq("user", springSecurityService.currentUser)
					}
				}
			}
		}

		return last - 1

	}
	*/

	/*
	def getPostsSince(discussion, lastRead, max) {

		def crit = Post.createCriteria()
		def newPosts = crit {
			and {
				eq("discussion", discussion)
				or {
					between("status", Post.STATUS_OK, Post.STATUS_MAX_DISPLAY)
					and {
						eq("status", Post.STATUS_INVISIBLE)
						eq("user", springSecurityService.currentUser)
					}
				}
				ge("createdDate", lastRead)
			}
			maxResults(max)
		}

		return newPosts

	}
	*/
	/*
	def getNumPostsSince(discussion, lastRead) {

		def crit = Post.createCriteria()
		def newPosts = crit.get {
			and {
				eq("discussion", discussion)
				or {
					between("status", Post.STATUS_OK, Post.STATUS_MAX_DISPLAY)
					and {
						eq("status", Post.STATUS_INVISIBLE)
						eq("user", springSecurityService.currentUser)
					}
				}
				gt("createdDate", lastRead)
			}
			projections {
				count "id"
			}
		}

		return newPosts

	}
	*/

	def getPostsFrom(discussion, start, max) {

		def session = sessionFactory.getCurrentSession()

		def query = session.createSQLQuery("{call GetPostsForDiscussion(?, ?, ?)}")
			.addScalar("id", org.hibernate.Hibernate.LONG)
			.addScalar("userId", org.hibernate.Hibernate.LONG)
			.addScalar("showModerationReport", org.hibernate.Hibernate.BOOLEAN)
			.addScalar("isLastPost", org.hibernate.Hibernate.BOOLEAN)
			.addScalar("postNum", org.hibernate.Hibernate.INTEGER)
			.addScalar("createdDate", org.hibernate.Hibernate.TIMESTAMP)
			.addScalar("status", org.hibernate.Hibernate.INTEGER)
			.setResultTransformer(new PostListItemTransformer())
		query.setLong(0, discussion.id)
		query.setInteger(1, start)
		query.setInteger(2, max)

		return query.list()

	}

	def saveReport(post, report) {

		def previous = 0
		def grudges = 0
		def busybody = 0

		if(report.user) {

			previous = PostReport.executeQuery("select count(*) from PostReport r where r.user = :user and r.post = :post",
				[user: report.user, post: post])[0]

			grudges = PostReport.executeQuery("select count(*) from PostReport r where r.user = :user and r.post.user = :poster",
				[user: report.user, poster: post.user])[0]

			busybody = PostReport.countByUser(report.user)

		}
		else {

			previous = PostReport.executeQuery("select count(*) from PostReport r where r.ipaddress = :ipaddress and r.post = :post",
				[ipaddress: report.ipaddress, post: post])[0]

			grudges = PostReport.executeQuery("select count(*) from PostReport r where r.ipaddress = :ipaddress and r.post.user = :poster",
				[ipaddress: report.ipaddress, poster: post.user])[0]

			busybody = PostReport.countByIpaddress(report.ipaddress)

		}

		if(previous == 0) {

			report.score = 1.0f / (1 + grudges + busybody)
			report.post.moderationScore += report.score

			report.save()

			//post.status = Post.STATUS_SUSPENDED_BY_ADMIN
			post.save()

			if(report.user) {
				UserHistory.CreateUserHistory(post.user, "POST REPORTED", report.user.username + " - " + post.discussion.id.toString() + "/" + post.id.toString());
				UserHistory.CreateUserHistory(report.user, "REPORTED POST", post.user.username + " - " + post.discussion.id.toString() + "/" + post.id.toString());
			}
			else {
				UserHistory.CreateUserHistory(post.user, "POST REPORTED", report.email + " - " + post.discussion.id.toString() + "/" + post.id.toString());
			}

			addToModQueue(post)

			cachePost(post)

		}
		else {
			report.errors.rejectValue "post", "postreport.post.duplicate"
		}
	}

	def moderatePost(post, result, text) {

		def user = springSecurityService.currentUser
		def comment = ModeratorComment.findByUserAndPost(user, post)
		if(!comment) {

			comment = new ModeratorComment(user: user, post: post, createdDate: new Date())
			comment.comment = text
			comment.result = result

			comment.validate()

			def ok = !comment.hasErrors()
			if(ok) {

				comment.save(flush: true)

				def dirty = false
				def votesFor = 0
				def votesAgainst = 0
				ModeratorComment.findAllByPost(post).each {
					if(it.result == VOTE_FOR) {
						votesFor += VOTE_FOR
					}
					else if(it.result == VOTE_AGAINST) {
						votesAgainst += VOTE_AGAINST
					}
				}

				def deleteThreshold = DELETE_THRESHOLD
				def keepThreshold = KEEP_THRESHOLD
				switch(post.status) {
					case Post.STATUS_SUSPENDED_BY_ADMIN:
					case Post.STATUS_WATCH:
						deleteThreshold = PREMOD_DELETE_THRESHOLD
						keepThreshold = PREMOD_KEEP_THRESHOLD
					break;
				}

				if(votesAgainst <= deleteThreshold) {
					post.status = Post.STATUS_DELETED_BY_ADMIN
					post.moderationResult = -1
					dirty = true
				}
				else if(votesFor >= keepThreshold) {
					post.status = Post.STATUS_OK
					post.moderationResult = 1
					dirty = true
				}

				if(dirty) {

					post.discussion.lastUpdated = new Date()
					post.discussion.save()
					post.save()

					if(post.moderationResult < 0) {
						UserHistory.CreateUserHistory(post.user, "POST MODERATED", "Post deleted: " + post.discussion.id.toString() + "/" + post.id.toString());
					}
					else if(post.moderationResult > 0) {
						UserHistory.CreateUserHistory(post.user, "POST MODERATED", "Post accepted: " + post.discussion.id.toString() + "/" + post.id.toString());
					}

					removeFromModQueue(post)

					cachePost(post)

				}
			}
		}

	}

	def adminUndeletePost(post) {
		post.status = Post.STATUS_OK
		post.discussion.lastUpdated = new Date()
		post.discussion.save()
		post.save()

		UserHistory.CreateUserHistory(post.user, "ADMINUNDELETE", "Post: " + post.discussion.id.toString() + "/" + post.id.toString() + ", Actioned by: " + springSecurityService.currentUser.username)

		cachePost(post)

	}

	def adminDeletePost(post) {

		post.status = Post.STATUS_DELETED_BY_ADMIN
		post.discussion.lastUpdated = new Date()
		post.discussion.save()
		post.save()

		UserHistory.CreateUserHistory(post.user, "ADMINDELETE", "Post: " + post.discussion.id.toString() + "/" + post.id.toString() + ", Actioned by: " + springSecurityService.currentUser.username)

		cachePost(post)

	}

	def getPostsToModerate() {

		def session = sessionFactory.getCurrentSession()

		def query = session.createSQLQuery("{call GetPostsForModeration()}")
			.addScalar("id", org.hibernate.Hibernate.LONG)
			.addScalar("userId", org.hibernate.Hibernate.LONG)
			.addScalar("showModerationReport", org.hibernate.Hibernate.BOOLEAN)
			.addScalar("isLastPost", org.hibernate.Hibernate.BOOLEAN)
			.addScalar("postNum", org.hibernate.Hibernate.INTEGER)
			.addScalar("createdDate", org.hibernate.Hibernate.TIMESTAMP)
			.addScalar("status", org.hibernate.Hibernate.INTEGER)
			.setResultTransformer(new PostListItemTransformer())

		return query.list()

	}

	def getRecentlyModeratedPosts() {

		def session = sessionFactory.getCurrentSession()

		def query = session.createSQLQuery("{call GetRecentlyModeratedPosts()}")
			.addScalar("id", org.hibernate.Hibernate.LONG)
			.addScalar("userId", org.hibernate.Hibernate.LONG)
			.addScalar("showModerationReport", org.hibernate.Hibernate.BOOLEAN)
			.addScalar("isLastPost", org.hibernate.Hibernate.BOOLEAN)
			.addScalar("postNum", org.hibernate.Hibernate.INTEGER)
			.addScalar("createdDate", org.hibernate.Hibernate.TIMESTAMP)
			.addScalar("status", org.hibernate.Hibernate.INTEGER)
			.setResultTransformer(new PostListItemTransformer())

		return query.list()

	}

	def getPostCounts() {

		def postcounts = new HashMap()

		if(springSecurityService.isLoggedIn())
		{
			def user = springSecurityService.currentUser
			for(bookmark in user.bookmarks) {
				postcounts.put(bookmark.discussion.id, bookmark.lastPostCount)
			}
		}

		return postcounts

	}

	def getUserModerationScore(user) {

		GregorianCalendar cal = new GregorianCalendar()
		cal.add Calendar.DAY_OF_YEAR, -30
		def last30days = cal.getTime()

		def q = Post.createCriteria()
		def score = q.get {
			and {
				eq("user", user)
				ge("createdDate", last30days)
			}
			projections {
				sum("moderationScore")
			}
		}

		return score

	}
	/*
	def fixPostNumbers() {

		Discussion.getAll().each {
			def discussion = it

			def count = 1
			def posts = Post.findAllByDiscussion(discussion, [sort: "id"]).each {
				def post = it
				post.postNum = count++
				post.save()
			}
		}

	}
	*/

	def cachePost(post) {

		def user = springSecurityService.currentUser
		def markup = ""

		redisService.withRedis { Jedis redis ->

			def key = "post:$post.id"
			redis.del(key)

			def postItem = new PostListItem(id: post.id,
				userId: post.user.id,
				showModerationReport: (post.status == 1 || post.status == 4 || post.moderationScore > 0),
				isLastPost : true,
				postNum: post.postNum,
				createdDate: post.createdDate,
				status: post.status)

			markup = postTagLib.buildCachedPost(postItem, user, post.discussion.postCount, post)

		}

		return markup

	}

	def addToModQueue(post) {
		if(post.status == Post.STATUS_SUSPENDED_BY_ADMIN || post.status == Post.STATUS_WATCH || post.moderationScore != 0) {
			def modQueue = ModerationQueue.findByPostId(post.id)
			if(!modQueue) {
				modQueue = new ModerationQueue(postId: post.id, createdDate: new Date())
				modQueue.save()
			}
		}
	}

	def removeFromModQueue(post) {
		def modQueue = ModerationQueue.findByPostId(post.id)
		modQueue.delete()
	}


}
