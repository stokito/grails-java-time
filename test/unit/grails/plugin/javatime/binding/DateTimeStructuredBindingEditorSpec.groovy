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

import org.grails.databinding.SimpleMapDataBindingSource
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Unroll
class DateTimeStructuredBindingEditorSpec extends Specification {
    @Shared
    TimeZone zone

    def setupSpec() {
        zone = TimeZone.default
        TimeZone.default = TimeZone.getTimeZone("Europe/Berlin")
    }

    def cleanupSpec() {
        TimeZone.default = zone
    }

    def "getPropertyValue() creates #expected from the fields #fields"() {
        given:
        def editor = new DateTimeStructuredBindingEditor(type)
        def dateProps = fields.collectEntries { ["some_date_param_name_$it.key".toString(), it.value] }
        def source = new SimpleMapDataBindingSource(dateProps)
        expect:
        editor.getPropertyValue(null, 'some_date_param_name', source) == expected
        where:
        type          | fields                                                                                      | expected
        LocalDate     | [year: "1977"]                                                                              | LocalDate.of(1977, 1, 1)
        LocalDate     | [year: "2009", month: "02", day: "20"]                                                      | LocalDate.of(2009, 2, 20)
        LocalDateTime | [year: "2009"]                                                                              | LocalDateTime.of(2009, 1, 1, 0, 0)
        LocalDateTime | [year: "2009", month: "03", day: "06", hour: "17", minute: "21", second: "33"]              | LocalDateTime.of(2009, 3, 6, 17, 21, 33)
        ZonedDateTime | [year: "2009"]                                                                              | ZonedDateTime.of(2009, 1, 1, 0, 0, 0, 0, ZoneId.of("Europe/Berlin"))
        ZonedDateTime | [year: "2009", month: "03", day: "06", hour: "17", minute: "21", second: "33"]              | ZonedDateTime.of(2009, 3, 6, 17, 21, 33, 0, ZoneId.of("Europe/Berlin"))
        LocalTime     | [hour: "17"]                                                                                | LocalTime.of(17, 0)
        LocalTime     | [hour: "17", minute: "55", second: "33"]                                                    | LocalTime.of(17, 55, 33)
        ZonedDateTime | [year: "2009", month: "08", day: "24", hour: "13", minute: "06"]                            | ZonedDateTime.of(2009, 8, 24, 13, 6, 0, 0, ZoneId.of("Europe/Berlin"))
        ZonedDateTime | [year: "2009", month: "08", day: "24", hour: "13", minute: "06", zone: "America/Vancouver"] | ZonedDateTime.of(2009, 8, 24, 13, 6, 0, 0, ZoneId.of("America/Vancouver"))
    }

    def "getPropertyValue() requires year for date types"() {
        given:
        def editor = new DateTimeStructuredBindingEditor(LocalTime)
        def dateProps = ['some_date_param_name_month': 11, 'some_date_param_name_day': 29]
        def source = new SimpleMapDataBindingSource(dateProps)
        when:
        editor.getPropertyValue(null, 'some_date_param_name', source)
        then:
        thrown(IllegalArgumentException)
    }

    def "getPropertyValue() requires hour for time types"() {
        given:
        def editor = new DateTimeStructuredBindingEditor(LocalTime)
        def dateProps = ['some_date_param_name_minute': 29]
        def source = new SimpleMapDataBindingSource(dateProps)
        when:
        editor.getPropertyValue(null, 'some_date_param_name', source)
        then:
        thrown(IllegalArgumentException)
    }
}