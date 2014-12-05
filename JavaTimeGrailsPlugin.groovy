import grails.plugin.javatime.binding.DateTimeConverter
import grails.plugin.javatime.binding.DateTimeStructuredBindingEditor
import grails.plugin.javatime.binding.JavaTimePropertyEditorRegistrar
import grails.plugin.javatime.binding.ZoneIdConverter
import grails.plugin.javatime.converters.JavaTimeConverters

/*
 * Copyright 2014 Rob Fletcher, Sergey Ponomarev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class JavaTimeGrailsPlugin {

	def version = '0.1-SNAPSHOT'
	def grailsVersion = '2.4 > *'
	def dependsOn = [converters: '2.0 > *']

	def title = 'Java-Time Plugin'
	def description = 'JSR 310 Java Time integration for Grails'
	def author = 'Sergey Ponomarev'
	def authorEmail = 'rob@freeside.co'

	def license = 'APACHE'
	def developers = [[name: 'Sergey Ponomarev', email: 'stokito@gmail.com'], [name: 'Rob Fletcher', email: 'rob@freeside.co', role: 'original author']]
	def documentation = 'https://github.com/stokito/grails-java-time/'
	def issueManagement = [system: 'GitHub', url: 'https://github.com/stokito/grails-java-time/issues']
	def scm = [url: 'https://github.com/stokito/grails-java-time/']

	def pluginExcludes = [
			'grails-app/controllers/**',
			'grails-app/domain/**',
			'grails-app/i18n/**',
			'web-app/**'
	]

	def doWithSpring = {
		javaTimePropertyEditorRegistrar(JavaTimePropertyEditorRegistrar)

		DateTimeConverter.SUPPORTED_TYPES.each{ javaTimeType ->
			"${javaTimeType.simpleName}Converter"(DateTimeConverter) {
				grailsApplication = ref("grailsApplication")
				type = javaTimeType
			}
		}
		"javaTimeZoneConverter"(ZoneIdConverter)
	}

	def doWithDynamicMethods = { ctx ->
		JavaTimeConverters.registerJsonAndXmlMarshallers()
		DateTimeStructuredBindingEditor.SUPPORTED_TYPES.each{ type ->
			application.mainContext.grailsWebDataBinder.registerStructuredEditor type, new DateTimeStructuredBindingEditor(type)
		}
	}
}
