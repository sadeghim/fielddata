// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

headerAndFooter.baseURL = "http://www2.ala.org.au/commonui"
security.cas.urlPattern = ""
security.cas.loginUrl = "https://auth.ala.org.au/cas/login"
security.cas.logoutUrl = "https://auth.ala.org.au/cas/logout"
ala.baseURL = "http://www.ala.org.au/"
bie.baseURL = "http://bie.ala.org.au"
bie.searchPath = "/search"
brokerURL = 'tcp://localhost:61616'
enableJMS = false
userDetails.url ="http://auth.ala.org.au/userdetails/userDetails/getUserListWithIds"
userDetails.emails.url = "http://auth.ala.org.au/userdetails/userDetails/getUserListFull"

grails.project.groupId = "au.org.ala.fielddata" // change this to alter the default package name and Maven publishing destination
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

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

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
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
        grails.serverURL = "http://moyesyside.ala.org.au:8086"
        fielddata.mediaUrl = "http://moyesyside.ala.org.au/fielddata/"
        fielddata.mediaDir = "/data/fielddata/"
        enableJMS = true //change to allow broadcast to queue
        brokerURL = 'tcp://localhost:61616'
        queueName = "org.ala.jms.cs"
    }
    production {
        grails.logging.jul.usebridge = false
        grails.serverURL = "http://fielddata.ala.org.au"
        grails.serverURL = "http://fielddata.ala.org.au"
        fielddata.mediaUrl = "http://fielddata.ala.org.au/media/"
        fielddata.mediaDir = "/data/fielddata/media/"
        enableJMS = true //change to allow broadcast to queue
        brokerURL = 'tcp://ala-biocache1.vm.csiro.au:61616'
        queueName = "org.ala.jms.cs"
    }
}

// log4j configuration
log4j = {

    appenders {
        //console name: "stdout", layout: pattern(conversionPattern: "%d [%c{1}]  %m%n"), threshold: org.apache.log4j.Level.DEBUG
        rollingFile name: "fielddataDebug",
                maxFileSize: 104857600,
                file: "/var/log/tomcat6/fielddata-debug.log",
                threshold: org.apache.log4j.Level.DEBUG,
                layout: pattern(conversionPattern: "%d [%c{1}]  %m%n")
    }
    appenders {
        rollingFile name: "stacktrace", maxFileSize: 1024, file: "/var/log/tomcat6/fielddata-stacktrace.log"
    }


    root {
        debug  'fielddataDebug'
    }

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
	       'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
	       'org.codehaus.groovy.grails.web.mapping', // URL mapping
	       'org.codehaus.groovy.grails.commons', // core / classloading
	       'org.codehaus.groovy.grails.plugins', // plugins
           'org.springframework.jdbc',
           'org.springframework.transaction',
           'org.codehaus.groovy',
           'org.grails',
           'org.apache',
           'grails.spring',
           'grails.util.GrailsUtil'

    debug  'au.org.ala'
}

