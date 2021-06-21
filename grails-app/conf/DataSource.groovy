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

dataSource {
    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    username = "<DB USER>"
    password = "<DB PASSWORD>"
    dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
	properties {
	      maxActive = 100
	      maxIdle = 25
	      minIdle = 1
	      initialSize = 1

	      numTestsPerEvictionRun = 3
	      maxWait = 10000

	      testOnBorrow = true
	      testWhileIdle = true
	      testOnReturn = true

	      validationQuery = "select now()"

	      minEvictableIdleTimeMillis = 1000 * 60 * 5
	      timeBetweenEvictionRunsMillis = 1000 * 60 * 5
   }
}

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop','update'
            url = "jdbc:mysql://mysql/notthetalk?characterEncoding=UTF-8"
			loggingSql = true
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:mysql://mysql/notthetalk_uat?characterEncoding=UTF-8"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
			url = "jdbc:mysql://mysql/notthetalk?characterEncoding=UTF-8"
			loggingSql = false
        }
    }
}
