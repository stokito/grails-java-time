grails.project.work.dir = 'target'
grails.project.class.dir = 'target/classes'
grails.project.test.class.dir = 'target/test-classes'
grails.project.test.reports.dir = 'target/test-reports'
grails.project.dependency.resolver='maven'
grails.project.dependency.resolution = {
grails.project.target.level = 1.8
grails.project.source.level = 1.8

	inherits 'global'
	log 'warn'

	repositories {
    grailsHome()
    grailsPlugins()
		grailsCentral()

		mavenCentral()
		mavenRepo 'http://repo.grails.org/grails/libs-releases'
		mavenRepo 'https://oss.sonatype.org/content/groups/public'
	}

	dependencies {
		test("org.grails:grails-datastore-test-support:1.0.2-grails-2.4")
	    test("org.grails:grails-web-databinding-spring:$grailsVersion")
		test('org.hamcrest:hamcrest-all:1.1') { export = false }
		test('org.jodd:jodd-lagarto:3.4.1') { export = false }
		compile 'org.jadira.usertype:usertype.spi:3.2.0.GA'
		compile 'org.jadira.usertype:usertype.core:3.2.0.GA'
		compile 'org.jadira.usertype:usertype.extended:3.2.0.GA'
	}

	plugins {
		build ':release:3.0.1', ':rest-client-builder:2.0.1', {
			export = false
		}
	}
}
