package grails.plugin.javatime.binding

import grails.persistence.Entity
import grails.util.GrailsNameUtils
import org.grails.databinding.SimpleMapDataBindingSource
import org.springframework.context.i18n.LocaleContextHolder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Unroll
class DateTimeConverterSpec extends Specification {
    @Shared
    static Locale currentLocale
    static TimeZone currentTimeZone

    def setupSpec() {
        currentLocale = LocaleContextHolder.locale
        LocaleContextHolder.locale = Locale.UK
        currentTimeZone = TimeZone.default
        TimeZone.default = TimeZone.getTimeZone(ZoneOffset.UTC)
    }

    def cleanupSpec() {
        LocaleContextHolder.locale = currentLocale
        TimeZone.default = currentTimeZone
    }

    void "test conversion from constructor #expected.class.simpleName object from a #value"() {
        given:
        def params = [:]
        params[propertyName] = value
        when:
        DateTimeConverterSpecEntity entity = new DateTimeConverterSpecEntity(params)
        then:
        entity."$propertyName" == expected
        where:
        value            | expected
        '16:55'          | LocalTime.of(16, 55)
        '22/10/13'       | LocalDate.of(2013, 10, 22)
        '22/10/13'       | LocalDate.of(2013, 10, 22)
        '22/10/13 17:33' | LocalDateTime.of(2013, 10, 22, 17, 33)
        '22/10/13 17:33' | ZonedDateTime.of(2013, 10, 22, 17, 33, 0, 0, ZoneId.systemDefault())
        '22/10/13 17:33' | ZonedDateTime.of(2013, 10, 22, 17, 33, 0, 0, ZoneId.systemDefault()).toInstant()

        propertyName = GrailsNameUtils.getPropertyNameRepresentation(expected.class.simpleName)
    }
}

@Entity
class DateTimeConverterSpecEntity {
    LocalTime localTime
    LocalDate localDate
    LocalDateTime localDateTime
    ZonedDateTime zonedDateTime
    Instant instant
}
