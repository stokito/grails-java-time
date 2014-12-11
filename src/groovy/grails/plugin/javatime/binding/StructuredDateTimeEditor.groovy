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

import org.codehaus.groovy.grails.web.binding.StructuredPropertyEditor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class StructuredDateTimeEditor extends DateTimeEditor implements StructuredPropertyEditor {

    StructuredDateTimeEditor(Class type) {
        super(type)
    }

    private static final FIELDS_BY_TYPE = [
            (LocalDate)    : ["year", "month", "day"].asImmutable(),
            (LocalTime)    : ["hour", "minute", "second", 'nanoOfSecond'].asImmutable(),
            (LocalDateTime): ["year", "month", "day", "hour", "minute", "second", 'nanoOfSecond'].asImmutable(),
            (ZonedDateTime): ["year", "month", "day", "hour", "minute", "second", 'nanoOfSecond', "zone"].asImmutable()
    ].asImmutable()

    private static final DEFAULT_VALUES = [month: 1, day: 1, hour: 0, minute: 0, second: 0, nanoOfSecond: 0].asImmutable()

    @Override
    List getRequiredFields() {
        return [FIELDS_BY_TYPE[type].head()]
    }

    @Override
    List getOptionalFields() {
        return FIELDS_BY_TYPE[type].tail()
    }

    @Override
    Object assemble(Class type, Map fieldValues) throws IllegalArgumentException {
        if (fieldValues.isEmpty() || fieldValues.every { !it.value }) return null
        requiredFields.each {
            if (!fieldValues."$it") {
                throw new IllegalArgumentException("Can't populate a $type without a $it")
            }
        }
        try {
            List ofArgs = (requiredFields + optionalFields).collect { String fieldName ->
                if (fieldName == 'zone') {
                    ZoneId zone = fieldValues['zone'] ? ZoneId.of(fieldValues['zone'].toString()) : ZoneId.systemDefault()
                    return zone
                } else {
                    return getPropVal(fieldValues, fieldName)
                }
            }
            return type.of(*ofArgs)
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException('Unable to parse structured date from request for date ["+propertyName+"]"')
        }
    }

    private int getPropVal(Map fieldValues, String fieldName) {
        return fieldValues[fieldName]?.toInteger() ?: DEFAULT_VALUES[fieldName]
    }
}