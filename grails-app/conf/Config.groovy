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

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// set per-environment serverURL stem for creating absolute links
environments {

	def appName = "notthetalk"

    production {

		def catalinaBase = System.properties.getProperty('catalina.base') ?: "/usr/share/tomcat5"
		def logDirectory = "${catalinaBase}/logs"

        grails.serverURL = "http://notthetalk.com"
		notthetalk.eventsUrl = "ws://events.notthetalk.com:9091"

		log4j = {

			appenders {
				rollingFile name:'tomcatLog', file:"${logDirectory}/${appName}.log".toString(), maxFileSize:'1GB'
			}

			root {
				error 'tomcatLog'
				additivity = true
			}

			error tomcatLog:'StackTrace'
			error 'grails.app'

		}

		searchIndex.location = "/home/servers/lucene_460"
		geocitylite.datafile = "/usr/local/share/GeoIP/GeoLiteCity.dat"

    }
    development {

		def logDirectory = "."

        grails.serverURL = "http://localhost:8080/notthetalk.com"
		notthetalk.eventsUrl = "ws://events.dev.notthetalk.com:9091"

		log4j = {
			appenders {
				rollingFile name:'tomcatLog', file:"${logDirectory}/${appName}.log".toString()
			}
			root {
				trace 'tomcatLog'
			}

			trace 'org.springframework.security'
			/*
			debug 'org.codehaus.groovy.grails'
			debug 'grails.app'
			*/
			debug 'com.notthetalk'
			debug 'com.notthetalk.SessionListener'
			error 'org.codehaus.groovy.grails.orm.hibernate'

		}

		searchIndex.location = "lucene2"
		geocitylite.datafile = "/usr/local/share/GeoIP/GeoLiteCity.dat"


    }
    test {
        grails.serverURL = "http://localhost:8080/${appName}"
    }

}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.useSecurityEventListener = true
grails.plugins.springsecurity.userLookup.userDomainClassName = 'com.notthetalk.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'com.notthetalk.UserRole'
grails.plugins.springsecurity.authority.className = 'com.notthetalk.Role'
grails.plugins.springsecurity.anon.key = '<ENTER KEY HERE>'
sanitizer.trustSanitizer=true

grails.plugins.springsecurity.onInteractiveAuthenticationSuccessEvent = { e, appCtx ->

	def userService = appCtx.getBean("userService")
	def source = e.getSource()
	def principal = source.principal

	def request = org.codehaus.groovy.grails.plugins.springsecurity.SecurityRequestHolder.getRequest()
	def ip = request.getHeader("x-forwarded-for") ?: request.getRemoteAddr()
	def location = request.getHeader("X-Geo-Location")

	userService.createUserHistory(principal.id, ip, location)

}

grails {
	mail {
        host = "<MAIL HOST>"
        port = 465
        username = "<MAIL USER>"
        password = "<MAIL PASSWORD>"
        props = [
            "mail.smtp.auth":"true",
            "mail.smtp.starttls.enable":"true",
            "mail.smtp.EnableSSL.enable":"true",
            "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
            "mail.smtp.socketFactory.port":"465",
            "mail.smtp.socketFactory.fallback":"false",
        ]
	}
}

guardian.apiKey="<GUARDIAN API KEY>"
bitly.login="<BITLY LOGIN>"
bitly.appKey="<BITLY APP KEY>"

facebook.applicationSecret="<FACEBOOK APP SECRET>"
facebook.applicationId="<FACEBOOK APPLICATION ID>"
facebook.appid = '<FACEBOOK APP ID>'

twitter {
	'default' {
		debugEnabled           = true
		OAuthConsumerKey       = '<TWITTER CONSUMER KEY>'
		OAuthConsumerSecret    = '<TWITTER CONSUMER SECRET>'
	}
}

oauth {
	twitter {
		requestTokenUrl = 'https://api.twitter.com/oauth/request_token'
		accessTokenUrl = 'https://api.twitter.com/oauth/access_token'
		authUrl = 'https://api.twitter.com/oauth/authorize'
		consumer.key = '<TWITTER OAUTH KEY>'
		consumer.secret = '<TWITTER OAUTH SECRET>'
	}
	facebook {
		requestTokenUrl = 'https://www.facebook.com/dialog/oauth'
		accessTokenUrl = 'https://graph.facebook.com/oauth/access_token'
		authUrl = 'https://graph.facebook.com/oauth/authorize'
		consumer.key = '<FACEBOOK OAUTH KEY>'
		consumer.secret = '<FACEBOOK OAUTH SECRET>'
	}
}

springMobile {
	deviceResolver="wurfl"
}

grails {
	redis {
		//poolConfig {
			// jedis pool specific tweaks here, see jedis docs & src
			// ex: testWhileIdle = true
		//}
		port = 6379
		host = "redis"
		timeout = 2000 //default in milliseconds
		//password = "BeXuPHa3uC4c" //defaults to no password
	}
}

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
