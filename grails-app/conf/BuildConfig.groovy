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

grails.servlet.version = "3.0"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.dependency.resolver="maven"

grails.project.dependency.resolution = {

    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }

    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	checksums true // Whether to verify checksums on resolve
	legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenLocal()
        mavenRepo "https://repo1.maven.org/maven2/"
        mavenRepo "https://mvnrepository.com/maven2"
        mavenRepo "https://download.java.net/maven/2/"
        mavenRepo "https://repository.jboss.com/maven2/"

    }
    dependencies {

		// specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		compile("org.codehaus.groovy.modules.http-builder:http-builder:0.6") {
			excludes "groovy"
		}

		//compile "org.springframework.mobile:spring-mobile-device:1.0.0.M3"
		//compile "net.sourceforge.wurfl:wurfl:1.3.1.1"

        runtime	"mysql:mysql-connector-java:5.1.49",
				"org.owasp.antisamy:antisamy:1.4.3",
				"com.icegreen:greenmail:1.3",
				"javax.mail:mail:1.4.3",
				"commons-codec:commons-codec:1.2",
				'org.twitter4j:twitter4j-core:2.2.1',
				'org.apache.httpcomponents:httpclient:4.1.1',
				'com.rabbitmq:amqp-client:2.8.7',
				"org.apache.lucene:lucene-core:4.6.0",
				"org.apache.lucene:lucene-queryparser:4.6.0",
				"org.apache.lucene:lucene-queries:4.6.0",
				"org.apache.lucene:lucene-analyzers-common:4.6.0"

    }

	plugins {

		runtime ":hibernate:3.6.10.3"
		runtime ":resources:1.2.1"
		runtime ":webxml:1.4.1"

		build ":tomcat:7.0.42"
		compile ":mail:1.0.1"
		compile ":markdown:1.1.1"
		compile ":quartz:1.0.1"
		compile ":recaptcha:0.6.2"
		compile ":redis:1.4.2"
		compile ":sanitizer:0.8.0"
		compile ":spring-security-core:1.2.7.3"
		compile ":cache-headers:1.1.5"

	}

}
