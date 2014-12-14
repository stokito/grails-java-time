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
package grails.plugin.javatime.binding

import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Unroll
class DateTimeConverterUnitSpec extends Specification {

    static final ZoneOffset SYSTEM_DEFAULT_TIME_ZONE = ZoneOffset.ofHours(-12)

    def "convert(): #expected.class.simpleName object  #value"() {
        given:
//        dtc.defaultTimeZoneId = SYSTEM_DEFAULT_TIME_ZONE

        def dtc = new DateTimeConverter()
        dtc.type = type
        dtc.grailsApplication = new DefaultGrailsApplication()
        dtc.grailsApplication.config.javatime.format.html5 = true
        dtc.grailsApplication.config.javatime.format.java.time.ZonedDateTime = 'yyyy-MM-dd'
//   		dtc.grailsApplication.config.javatime.format.java.time.ZonedDateTime = 'yyyy-MM-dd HH:mm:ss'
        dtc.grailsApplication.config.javatime.format.java.time.LocalDate = 'yyyy-MM-dd'
        dtc.grailsApplication.config.javatime.format.java.time.LocalTime = 'HH:mm:ss'
        expect:
        dtc.canConvert(value)
        dtc.convert(value) == expected
        where:
        value                           | type          | expected
        '2014-04-23T04:30:45.123Z'      | ZonedDateTime | ZonedDateTime.of(2014, 4, 23, 4, 30, 45, 123_000_000, ZoneOffset.UTC)
        '2014-04-23T04:30:45.123+01:00' | ZonedDateTime | ZonedDateTime.of(2014, 4, 23, 4, 30, 45, 123_000_000, ZoneOffset.ofHours(1))
        '2014-04-23T04:30:45.123'       | ZonedDateTime | ZonedDateTime.of(2014, 4, 23, 4, 30, 45, 123_000_000, SYSTEM_DEFAULT_TIME_ZONE)
        '2014-04-23T04:30'              | ZonedDateTime | ZonedDateTime.of(2014, 4, 23, 4, 30, 0, 0, SYSTEM_DEFAULT_TIME_ZONE)
        '2014-04-23'                    | ZonedDateTime | ZonedDateTime.of(2014, 4, 23, 0, 0, 0, 0, SYSTEM_DEFAULT_TIME_ZONE)
        '2014-04-23T04:30:45.123'       | LocalDateTime | LocalDateTime.of(2014, 4, 23, 4, 30, 45, 123_000_000)
        '2014-04-23T04:30:45'           | LocalDateTime | LocalDateTime.of(2014, 4, 23, 4, 30, 45)
        '04:30:45.123'                  | LocalTime     | LocalTime.of(4, 30, 45, 123_000_000)
        '04:30:45'                      | LocalTime     | LocalTime.of(4, 30, 45)
        '2014-04-23'                    | LocalDate     | LocalDate.of(2014, 4, 23)
    }
}

