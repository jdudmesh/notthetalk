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

class TagsService {

    static transactional = false

	def sessionFactory

    def getTop10() {

		def session = sessionFactory.getCurrentSession()

		def query = session.createSQLQuery("{call GetTop10Tags()}")
			.addScalar("id", org.hibernate.Hibernate.LONG)
			.addScalar("tag", org.hibernate.Hibernate.STRING)
			.addScalar("weight", org.hibernate.Hibernate.DOUBLE)
			.setResultTransformer(new TagTransformer())

		return query.list()

    }

	def getDiscussionsForTag(tag) {

		def session = sessionFactory.getCurrentSession()

		def query = session.createSQLQuery("{call GetDiscussionsForTag(?)}")
			.addScalar("id", org.hibernate.Hibernate.LONG)
			.addScalar("version", org.hibernate.Hibernate.LONG)
			.addScalar("discussion_id", org.hibernate.Hibernate.LONG)
			.addScalar("discussion_name", org.hibernate.Hibernate.STRING)
			.addScalar("folder_id", org.hibernate.Hibernate.LONG)
			.addScalar("folder_key", org.hibernate.Hibernate.STRING)
			.addScalar("folder_name", org.hibernate.Hibernate.STRING)
			.addScalar("last_post", org.hibernate.Hibernate.TIMESTAMP)
			.addScalar("last_post_id", org.hibernate.Hibernate.LONG)
			.addScalar("post_count", org.hibernate.Hibernate.INTEGER)
			.setResultTransformer(new FrontPageEntryTransformer())

		query.setString(0, tag)

		return (List<RecentDiscussion>)query.list()

	}
}
