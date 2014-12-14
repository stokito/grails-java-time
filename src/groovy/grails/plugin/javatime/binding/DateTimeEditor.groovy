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

import java.beans.PropertyEditorSupport
import grails.util.Holders
import org.springframework.context.i18n.LocaleContextHolder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import static java.time.ZoneOffset.UTC
import static java.time.format.DateTimeFormatter.ISO_INSTANT
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME
import static java.time.format.FormatStyle.SHORT

class DateTimeEditor extends PropertyEditorSupport {
    static final SUPPORTED_TYPES = [LocalTime, LocalDate, LocalDateTime, ZonedDateTime, Instant].asImmutable()
    protected final Class type
    @Lazy
    private ConfigObject config = Holders.config?.javatime?.format

    DateTimeEditor(Class type) {
        this.type = type
    }

    @Override
    String getAsText() {
        return value ? format() : ''
    }

    private String format() {
        return type == Instant ? formatter.withZone(ZoneId.systemDefault()).format(value) : formatter.format(value)
    }

    @Override
    void setAsText(String text) {
        value = text ? parse(text) : null
    }

    private parse(String text) {
        return type == Instant ? ZonedDateTime.parse(text, formatter).toInstant() : type.parse(text, formatter)
    }

    protected DateTimeFormatter getFormatter() {
        if (hasConfigPattern()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(configPattern)
            if (type == Instant) {
//                formatter = formatter.withZone(ZoneId.systemDefault())
            }
            return formatter
        } else if (useISO()) {
            return isoFormatter
        } else {
            return localeFormatter
        }
    }

    private boolean hasConfigPattern() {
        config?.flatten()?."$type.simpleName"
    }

    private String getConfigPattern() {
        config?.flatten()?."$type.simpleName"
    }

    private boolean useISO() {
        config?.html5
    }

    private DateTimeFormatter getIsoFormatter() {
        switch (type) {
            case LocalTime:
                return ISO_LOCAL_TIME
            case LocalDate:
                return ISO_LOCAL_DATE
            case LocalDateTime:
                return ISO_LOCAL_DATE_TIME
            case ZonedDateTime:
            case Instant:
                return ISO_OFFSET_DATE_TIME
        }
        return null
    }

    private DateTimeFormatter getLocaleFormatter() {
        Locale locale = LocaleContextHolder.locale
        DateTimeFormatter formatter
        switch (type) {
            case LocalTime:
                formatter = DateTimeFormatter.ofLocalizedTime(SHORT).withLocale(locale)
                break
            case LocalDate:
                formatter = DateTimeFormatter.ofLocalizedDate(SHORT).withLocale(locale)
                break
            case Instant:
            case ZonedDateTime:
            default:
                formatter = DateTimeFormatter.ofLocalizedDateTime(SHORT).withLocale(locale).withZone(ZoneId.systemDefault())
        }
        return formatter
    }
}
