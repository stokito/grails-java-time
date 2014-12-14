package grails.plugin.javatime.binding

import grails.plugin.javatime.Html5DateTimeFormat
import grails.plugin.javatime.SmartDateParser
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.grails.databinding.converters.ValueConverter
import org.springframework.context.i18n.LocaleContextHolder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class DateTimeConverter implements ValueConverter {
    static final SUPPORTED_TYPES = [LocalTime, LocalDate, LocalDateTime, ZonedDateTime, Instant].asImmutable()
    Class type
    GrailsApplication grailsApplication
    @Lazy
    private ConfigObject config = grailsApplication.config.javatime.format

    @Override
    public boolean canConvert(Object value) {
        value instanceof String
    }

    @Override
    public Object convert(Object value) {
        def pe = new DateTimeEditor(type)
        pe.asText = value
        return value ? pe.value : null
    }

    @Override
    public Class<?> getTargetType() {
        type
    }
}
