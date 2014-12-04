package grails.plugin.javatime.binding

import org.grails.databinding.DataBindingSource
import org.grails.databinding.StructuredBindingEditor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DateTimeStructuredBindingEditor implements StructuredBindingEditor {

    static final SUPPORTED_TYPES = [LocalTime, LocalDate, LocalDateTime, ZonedDateTime].asImmutable()

    Class type

    DateTimeStructuredBindingEditor(Class type) {
        this.type = type
    }

    private static final FIELDS_BY_TYPE = [
            (LocalDate): ["year", "month", "day"].asImmutable(),
            (LocalTime): ["hour", "minute", "second", 'nanoOfSecond'].asImmutable(),
            (LocalDateTime): ["year", "month", "day", "hour", "minute", "second", 'nanoOfSecond'].asImmutable(),
            (ZonedDateTime): ["year", "month", "day", "hour", "minute", "second", 'nanoOfSecond', "zone"].asImmutable()
    ].asImmutable()

    private static final DEFAULT_VALUES = [month: 1, day: 1, hour: 0, minute: 0, second: 0, nanoOfSecond: 0].asImmutable()

    List getRequiredFields() {
        return [FIELDS_BY_TYPE[type].head()]
    }

    List getOptionalFields() {
        return FIELDS_BY_TYPE[type].tail()
    }

    @Override
    Object getPropertyValue(Object obj, String propertyName, DataBindingSource source) {
        requiredFields.each {
            if (!source["${propertyName}_${it}"]) {
                throw new IllegalArgumentException("Can't populate a $type without a $it")
            }
        }
        try {
            List ofArgs = (requiredFields + optionalFields).collect { String fieldName ->
                if (fieldName == 'zone') {
                    ZoneId zone = source["${propertyName}_zone"] ? ZoneId.of(source["${propertyName}_zone"].toString()) : ZoneId.systemDefault()
                    return zone
                } else {
                    return getPropVal(source, propertyName, fieldName)
                }
            }
            return type.of(*ofArgs)
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Unable to parse structured date from request for $type [$propertyName]", nfe)
        }
    }

    private int getPropVal(DataBindingSource source, String propertyName, String fieldName) {
        return source["${propertyName}_${fieldName}"]?.toInteger() ?: DEFAULT_VALUES[fieldName]
    }
}
