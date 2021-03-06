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

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.springframework.context.i18n.LocaleContextHolder
import spock.lang.*

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

import static java.time.ZoneOffset.UTC
import static java.util.Locale.*

@TestMixin(GrailsUnitTestMixin)
@Unroll
class DateTimeEditorSpec extends Specification {
    static final ZoneOffset SYSTEM_DEFAULT_TIME_ZONE = ZoneOffset.ofHours(-12)

    void setup() {
        TimeZone.default = TimeZone.getTimeZone(SYSTEM_DEFAULT_TIME_ZONE)
    }

    void cleanup() {
        // it is frankly shocking that Grails requires me to do this. The test environment is not properly idempotent as configuration changes will leak from one test to another
        grailsApplication.config.remove("javatime")
    }

    def "getAsText converts null to empty string"() {
        given:
        def editor = new DateTimeEditor(LocalDate)
        when:
        editor.value = null
        then:
        editor.asText == ""
    }

    def "setAsText converts empty string to null"() {
        given:
        def editor = new DateTimeEditor(LocalDate)
        when:
        editor.asText = ""
        then:
        editor.value == null
    }

    def "getAsText formats #type.simpleName instances correctly for #locale locale"() {
        given:
        def editor = new DateTimeEditor(type)
        and:
        LocaleContextHolder.locale = locale
        when:
        editor.value = value
        then:
        editor.asText == expected
        where:
        type          | value                                                             | locale | expected
        LocalDate     | LocalDate.of(1971, 11, 29)                                        | UK     | "29/11/71"
        LocalDate     | LocalDate.of(1971, 11, 29)                                        | US     | "11/29/71"
        LocalDateTime | LocalDateTime.of(2009, 3, 6, 17, 0)                               | UK     | "06/03/09 17:00"
        LocalDateTime | LocalDateTime.of(2009, 3, 6, 17, 0)                               | US     | "3/6/09 5:00 PM"
        ZonedDateTime | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneId.systemDefault()) | UK     | "06/03/09 17:00"
        ZonedDateTime | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneId.systemDefault()) | US     | "3/6/09 5:00 PM"
        LocalTime     | LocalTime.of(23, 59)                                              | UK     | "23:59"
        LocalTime     | LocalTime.of(23, 59)                                              | US     | "11:59 PM"
        Instant       | Instant.ofEpochMilli(92554380000L)                                | UK     | "06/12/72 17:33"
        Instant       | Instant.ofEpochMilli(92554380000L)                                | US     | "12/6/72 5:33 PM"
    }

    def "getAsText formats #type.simpleName instances correctly according to a configured pattern"() {
        given:
        grailsApplication.config.javatime.format."$type.simpleName" = config
        and:
        def editor = new DateTimeEditor(type)
        when:
        editor.value = value
        then:
        editor.asText == expected
        where:
        type          | config                | value                                                             | expected
        LocalDate     | "dd/MM/yyyy"          | LocalDate.of(1971, 11, 29)                                        | "29/11/1971"
        LocalDateTime | "dd/MM/yyyy h:mm a"   | LocalDateTime.of(1971, 11, 29, 17, 0)                             | "29/11/1971 5:00 PM"
        ZonedDateTime | "dd/MM/yyyy h:mm a"   | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneId.systemDefault()) | "06/03/2009 5:00 PM"
        ZonedDateTime | "dd/MM/yyyy h:mm a Z" | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneOffset.ofHours(1))  | "06/03/2009 5:00 PM +0100"
        LocalTime     | "h:mm a"              | LocalTime.of(23, 59)                                              | "11:59 PM"
        Instant       | "dd/MM/yyyy h:mm a"   | Instant.ofEpochMilli(92554380000L)                                | "06/12/1972 5:33 PM"
        Instant       | "dd/MM/yyyy h:mm a Z" | Instant.ofEpochMilli(92554380000L)                                | "06/12/1972 5:33 PM -1200"
    }

    def "getAsText formats #type.simpleName instances correctly for HTML5"() {
        given:
        grailsApplication.config.javatime.format.html5 = true
        and:
        def editor = new DateTimeEditor(type)
        when:
        editor.value = value
        then:
        editor.asText == expected
        where:
        type          | value                                                            | expected
        LocalDate     | LocalDate.of(1971, 11, 29)                                       | "1971-11-29"
        LocalDateTime | LocalDateTime.of(1971, 11, 29, 17, 0)                            | "1971-11-29T17:00:00"
        ZonedDateTime | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneOffset.ofHours(1)) | "2009-03-06T17:00:00+01:00"
        LocalTime     | LocalTime.of(23, 59)                                             | "23:59:00"
        Instant       | Instant.ofEpochMilli(92554380000L)                               | "1972-12-06T17:33:00-12:00"
    }

    def "setAsText parses #type.simpleName instances from #locale locale format text"() {
        given:
        def editor = new DateTimeEditor(type)
        and:
        LocaleContextHolder.locale = locale
        when:
        editor.asText = text
        then:
        editor.value == expected
        where:
        type          | text              | locale | expected
        LocalDate     | "29/11/71"        | UK     | LocalDate.of(2071, 11, 29)
        LocalDate     | "11/29/71"        | US     | LocalDate.of(2071, 11, 29)
        LocalDateTime | "06/03/09 17:00"  | UK     | LocalDateTime.of(2009, 3, 6, 17, 0)
        LocalDateTime | "3/6/09 5:00 PM"  | US     | LocalDateTime.of(2009, 3, 6, 17, 0)
        ZonedDateTime | "06/03/09 17:00"  | UK     | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneId.systemDefault())
        ZonedDateTime | "3/6/09 5:00 PM"  | US     | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneId.systemDefault())
//TODO        ZonedDateTime | "3/6/09"          | US     | ZonedDateTime.of(2009, 3, 6, 0, 0, 0, 0, ZoneId.systemDefault())
        LocalTime     | "23:59"           | UK     | LocalTime.of(23, 59)
        LocalTime     | "11:59 PM"        | US     | LocalTime.of(23, 59)
        Instant       | "07/12/72 05:33"  | UK     | ZonedDateTime.of(2072, 12, 7, 5, 33, 0, 0, ZoneId.systemDefault()).toInstant()
        Instant       | "12/7/72 5:33 AM" | US     | ZonedDateTime.of(2072, 12, 7, 5, 33, 0, 0, ZoneId.systemDefault()).toInstant()
//TODO        Instant       | "12/7/72"         | US     | ZonedDateTime.of(2072, 12, 7, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant()
    }

    def "setAsText parses #type.simpleName instances correctly according to a configured pattern"() {
        given:
        grailsApplication.config.javatime.format."$type.simpleName" = config
        and:
        def editor = new DateTimeEditor(type)
        when:
        editor.asText = text
        then:
        editor.value == expected
        where:
        type          | config                  | text                        | expected
        LocalDate     | "dd/MM/yyyy"            | "29/11/1971"                | LocalDate.of(1971, 11, 29)
        LocalDateTime | "dd/MM/yyyy h:mm a"     | "29/11/1971 5:00 PM"        | LocalDateTime.of(1971, 11, 29, 17, 0)
        ZonedDateTime | "dd/MM/yyyy h:mm a"     | "06/03/2009 5:00 PM"        | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneId.systemDefault())
        ZonedDateTime | "dd/MM/yyyy h:mm a Z"   | "06/03/2009 5:00 PM +0100"  | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneOffset.ofHours(1))
        ZonedDateTime | "dd/MM/yyyy[ h:mm a Z]" | "06/03/2009"                | ZonedDateTime.of(2009, 3, 6, 0, 0, 0, 0, ZoneId.systemDefault())
        LocalTime     | "h:mm a"                | "11:59 PM"                  | LocalTime.of(23, 59)
        Instant       | "dd/MM/yyyy h:mm a"     | "07/12/1972 11:33 AM"       | ZonedDateTime.of(1972, 12, 7, 11, 33, 0, 0, ZoneId.systemDefault()).toInstant()
        Instant       | "dd/MM/yyyy h:mm a Z"   | "07/12/1972 11:33 AM +0500" | ZonedDateTime.of(1972, 12, 7, 11, 33, 0, 0, ZoneOffset.ofHours(5)).toInstant()
        Instant       | "dd/MM/yyyy[ h:mm a Z]" | "07/12/1972"                | ZonedDateTime.of(1972, 12, 7, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant()
    }

    def "setAsText parses #type.simpleName instances correctly using HTML5 format"() {
        given:
        grailsApplication.config.javatime.format.html5 = true
        and:
        def editor = new DateTimeEditor(type)
        when:
        editor.asText = text
        then:
        editor.value == expected
        where:
        type          | text                        | expected
        LocalDate     | "1971-11-29"                | LocalDate.of(1971, 11, 29)
        LocalDateTime | "1971-11-29T17:00:00"       | LocalDateTime.of(1971, 11, 29, 17, 0)
        ZonedDateTime | "2009-03-06T17:00:00"       | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneId.systemDefault())
        ZonedDateTime | "2009-03-06T17:00:00+01:00" | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, ZoneOffset.ofHours(1))
        ZonedDateTime | "2009-03-06T17:00:00Z"      | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 0, UTC)
        ZonedDateTime | "2009-03-06T17:00:00.123Z"  | ZonedDateTime.of(2009, 3, 6, 17, 0, 0, 123_000_000, UTC)
        ZonedDateTime | "2009-03-06"                | ZonedDateTime.of(2009, 3, 6, 0, 0, 0, 0, ZoneId.systemDefault())
        LocalTime     | "23:59:00"                  | LocalTime.of(23, 59)
        Instant       | "1972-12-07T05:33:00"       | ZonedDateTime.of(1972, 12, 7, 5, 33, 0, 0, ZoneId.systemDefault()).toInstant()
        Instant       | "1972-12-07T05:33:00Z"      | ZonedDateTime.of(1972, 12, 7, 5, 33, 0, 0, UTC).toInstant()
        Instant       | "1972-12-07T05:33:00+01:00" | ZonedDateTime.of(1972, 12, 7, 5, 33, 0, 0, ZoneOffset.ofHours(1)).toInstant()
        Instant       | "1972-12-07"                | ZonedDateTime.of(1972, 12, 7, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant()
    }

    def "configured format trumps HTML5"() {
        given:
        grailsApplication.config.javatime.format."$LocalDate.simpleName" = "dd/MM/yyyy"
        grailsApplication.config.javatime.format.html5 = true
        and:
        def editor = new DateTimeEditor(LocalDate)
        when:
        editor.value = LocalDate.of(1971, 11, 29)
        then:
        editor.asText == "29/11/1971"
    }
}
