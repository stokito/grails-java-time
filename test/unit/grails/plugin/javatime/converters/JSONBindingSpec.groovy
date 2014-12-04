/*
 * Copyright 2010 Rob Fletcher
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
package grails.plugin.javatime.converters

import grails.converters.JSON
import grails.persistence.Entity
import grails.plugin.javatime.binding.DateTimeConverter
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import grails.util.GrailsNameUtils
import groovy.transform.CompileStatic
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.*

@TestMixin(ControllerUnitTestMixin)
@Mock(Timestamp)
@Unroll
class JSONBindingSpec extends Specification {

	@Shared TimeZone originalTimeZone

	void setupSpec() {
		originalTimeZone = TimeZone.default
		TimeZone.default = TimeZone.getTimeZone("America/Vancouver")
	}

	void cleanupSpec() {
		TimeZone.default = originalTimeZone
	}

	void setup() {
		defineBeans {
			dateTimeConverter(DateTimeConverter) {
				type = ZonedDateTime
				grailsApplication = ref("grailsApplication")
			}
		}
	}

	def "can unmarshal a #expected.class.simpleName object from a JSON element #value"() {
		given:
		def json = JSON.parse("""{$propertyName: "$value"}""")
		when:
		def bean = new Timestamp(json)
		then:
		bean[propertyName] == expected
		where:
		value                           | expected
		'2014-04-23T04:30:45.123Z'      | ZonedDateTime.of(2014, 4, 23, 4, 30, 45, 123_000_000, ZoneOffset.UTC)
		'2014-04-23T04:30:45.123+01:00' | ZonedDateTime.of(2014, 4, 23, 4, 30, 45, 123_000_000, ZoneOffset.ofHours(1))
		'2014-04-23T04:30:45.123'       | ZonedDateTime.of(2014, 4, 23, 4, 30, 45, 123_000_000, ZoneId.systemDefault())
		'2014-04-23T04:30'              | ZonedDateTime.of(2014, 4, 23, 4, 30, 0, 0, ZoneId.systemDefault())
		'2014-04-23T04:30:45.123'       | LocalDateTime.of(2014, 4, 23, 4, 30, 45, 123_000_000)
		'2014-04-23T04:30:45'           | LocalDateTime.of(2014, 4, 23, 4, 30, 45)
		'04:30:45.123'                  | LocalTime.of(4, 30, 45, 123)
		'04:30:45'                      | LocalTime.of(4, 30, 45)

		propertyName = GrailsNameUtils.getPropertyNameRepresentation(expected.class.simpleName)
	}

}

@CompileStatic
@Entity
class Timestamp {
	ZonedDateTime zonedDateTime
	LocalDateTime localDateTime
	LocalTime localTime
}
