package grails.plugin.javatime.binding

import grails.persistence.Entity
import org.grails.databinding.SimpleMapDataBindingSource
import spock.lang.Specification

import java.time.ZoneId

class DateTimeZoneConverterSpec extends Specification {
    def grailsWebDataBinder

    void "test conversion"() {
        given:
        DateTimeZoneConverterSpecEntity entity = new DateTimeZoneConverterSpecEntity()
        def params = [:]
        params.dateTimeZone = 'America/Chicago'
        when:
        grailsWebDataBinder.bind entity, params as SimpleMapDataBindingSource
        then:
        entity.dateTimeZone == ZoneId.of('America/Chicago')
    }

    void "test conversion from constructor"() {
        given:
        def params = [:]
        params.dateTimeZone = 'America/Chicago'
        when:
        DateTimeZoneConverterSpecEntity entity = new DateTimeZoneConverterSpecEntity(params)
        then:
        entity.dateTimeZone == ZoneId.of('America/Chicago')
    }
}

@Entity
class DateTimeZoneConverterSpecEntity {
    ZoneId dateTimeZone
}