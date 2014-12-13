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

import grails.plugin.javatime.SmartDateParser

import java.beans.PropertyEditorSupport
import grails.plugin.javatime.Html5DateTimeFormat
import grails.util.Holders
import org.springframework.context.i18n.LocaleContextHolder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

import static java.time.ZoneOffset.UTC
import static java.time.format.FormatStyle.SHORT

class DateTimeEditor extends PropertyEditorSupport {
	static final SUPPORTED_TYPES = [LocalTime, LocalDate, LocalDateTime, ZonedDateTime, Instant].asImmutable()
	protected final Class type
	ZoneId defaultTimeZoneId = ZoneId.systemDefault()
	@Lazy private ConfigObject config = Holders.config?.javatime?.format

	DateTimeEditor(Class type) {
		this.type = type
	}

	@Override
	String getAsText() {
		return value ? (useISO() ? value.toString() : formatter.format(value)) : ''
	}

	@Override
	void setAsText(String text) {
		value = text ? formatter.parse(text) : null
	}

	protected DateTimeFormatter getFormatter() {
		if (hasConfigPatternFor(type)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getConfigPatternFor(type))
			if (type == Instant) {
				formatter = formatter.withZone(UTC)
			}
			return formatter
		} else if (useISO()) {
			return getISOFormatterFor(type)
		} else {
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
					formatter = DateTimeFormatter.ofLocalizedDateTime(SHORT).withLocale(locale).withZone(UTC)
					break
				case ZonedDateTime:
				default:
					formatter = DateTimeFormatter.ofLocalizedDateTime(SHORT).withLocale(locale).withZone(defaultTimeZoneId)
			}
			return formatter
		}
	}

	private boolean hasConfigPatternFor(Class type) {
		config?.flatten()?."$type.simpleName"
	}

	private String getConfigPatternFor(Class type) {
		config?.flatten()?."$type.simpleName"
	}

	private boolean useISO() {
		config?.html5
	}

	private DateTimeFormatter getISOFormatterFor(Class type) {
//			DateTimeFormatter.ISO_DATE
//			DateTimeFormatter.ISO_TIME
//			DateTimeFormatter.ISO_LOCAL_DATE_TIME
//			DateTimeFormatter.ISO_ZONED_DATE_TIME
		return null
	}
}
