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
    ZoneId defaultTimeZoneId = ZoneId.systemDefault()
    @Lazy
    private ConfigObject config = grailsApplication.config.javatime.format

    @Override
    public boolean canConvert(Object value) {
        value instanceof String
    }

    @Override
    public Object convert(Object value) {
        return value ? safeParse((String) value) : null
    }

    //HACK: http://stackoverflow.com/questions/23596530/unable-to-obtain-zoneddatetime-from-temporalaccessor-using-datetimeformatter-and/27285822#27285822
    def safeParse(String value) {
        if (type == ZonedDateTime.class) {
            SmartDateParser.parse(value, defaultTimeZoneId);
        } else {
            return type.parse(value)
        }
    }

    @Override
    public Class<?> getTargetType() {
        type
    }

    protected DateTimeFormatter getFormatter() {
        if (hasConfigPatternFor(type)) {
            return DateTimeFormatter.ofPattern(getConfigPatternFor(type))
        } else if (useISO()) {
            return getISOFormatterFor(type)
        } else {
            def style
            switch (type) {
                case LocalTime:
                    style = '-S'
                    break
                case LocalDate:
                    style = 'S-'
                    break
                default:
                    style = 'SS'
            }
            Locale locale = LocaleContextHolder.locale
            return DateTimeFormatter.ofPattern(style, locale)
        }
    }

    private boolean hasConfigPatternFor(Class type) {
        config.flatten()."$type.name"
    }

    private String getConfigPatternFor(Class type) {
        config.flatten()."$type.name"
    }

    private boolean useISO() {
        config.html5
    }

    private DateTimeFormatter getISOFormatterFor(Class type) {
        switch (type) {
            case LocalTime:
                return Html5DateTimeFormat.time()
            case LocalDate:
                return Html5DateTimeFormat.date()
            case LocalDateTime:
                return Html5DateTimeFormat.datetimeLocal()
            case ZonedDateTime:
            case Instant:
                return Html5DateTimeFormat.datetime()
        }
        return null
    }
}
