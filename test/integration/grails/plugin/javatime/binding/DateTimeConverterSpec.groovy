package grails.plugin.javatime.binding

import grails.persistence.Entity
import org.grails.databinding.SimpleMapDataBindingSource
import org.springframework.context.i18n.LocaleContextHolder
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DateTimeConverterSpec extends Specification {

    def grailsWebDataBinder
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

    void "test conversion"() {
        given:
        DateTimeConverterSpecEntity entity = new DateTimeConverterSpecEntity()
        def params = [:]
        params.localTime = '16:55'
        params.localDate = '22/10/2013'
        params.localDateTime = '22/10/2013 17:33'
        params.zonedDateTime = '22/10/2013 17:33'
        params.instant = '22/10/2013 17:33'
        when:
        grailsWebDataBinder.bind entity, params as SimpleMapDataBindingSource
        then:
        entity.localTime == LocalTime.of(16, 55)
        entity.localDate == LocalDate.of(2013, 10, 22)
        entity.localDateTime == LocalDateTime.of(2013, 10, 22, 17, 33)
        entity.zonedDateTime == ZonedDateTime.of(2013, 10, 22, 17, 33, 0, 0, ZoneId.systemDefault())
        entity.instant == ZonedDateTime.of(2013, 10, 22, 17, 33, 0, 0, ZoneId.systemDefault()).toInstant()
    }

    void "test conversion from constructor"() {
        given:
        def params = [:]
        params.localTime = '16:55'
        params.localDate = '22/10/2013'
        params.localDateTime = '22/10/2013 17:33'
        params.zonedDateTime = '22/10/2013 17:33'
        params.instant = '22/10/2013 17:33'
        when:
        DateTimeConverterSpecEntity entity = new DateTimeConverterSpecEntity(params)
        then:
        entity.localTime == LocalTime.of(16, 55)
        entity.localDate == LocalDate.of(2013, 10, 22)
        entity.localDateTime == LocalDateTime.of(2013, 10, 22, 17, 33)
        entity.zonedDateTime == ZonedDateTime.of(2013, 10, 22, 17, 33, 0, 0, ZoneId.systemDefault())
        entity.instant == ZonedDateTime.of(2013, 10, 22, 17, 33, 0, 0, ZoneId.systemDefault()).toInstant()
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
