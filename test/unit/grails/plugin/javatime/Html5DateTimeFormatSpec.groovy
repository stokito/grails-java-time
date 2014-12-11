package grails.plugin.javatime

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.ZoneOffset
import java.time.ZonedDateTime


@Unroll
class Html5DateTimeFormatSpec extends Specification {

    @Shared
    ZonedDateTime dateTime = ZonedDateTime.of(2008, 10, 2, 2, 50, 43, 123_000_000, ZoneOffset.UTC)

    @Unroll
    def "HTML5 '#format' format is printed correctly"() {
        expect:
        Html5DateTimeFormat."$format"().format(dateTime) == expected
        where:
        format          | expected
        "month"         | "2008-10"
        "week"          | "2008-W40-4Z"
        "date"          | "2008-10-02Z"
        "datetimeLocal" | "2008-10-02T02:50:43.123"
        "datetime"      | "2008-10-02T02:50:43.123Z"
        "time"          | "02:50:43.123Z"
    }
}
