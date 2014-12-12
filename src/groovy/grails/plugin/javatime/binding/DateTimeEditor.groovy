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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DateTimeEditor extends PropertyEditorSupport {
	static final SUPPORTED_TYPES = [LocalTime, LocalDate, LocalDateTime, ZonedDateTime, Instant].asImmutable()
	protected final Class type
	@Lazy private ConfigObject config = Holders.config?.javatime?.format

	DateTimeEditor(Class type) {
		this.type = type
	}

	@Override
	String getAsText() {
		return value ? formatter.format(value) : ''
	}

	@Override
	void setAsText(String text) {
		value = text ? safeParse((String) text) : null
	}

	//HACK: http://stackoverflow.com/questions/23596530/unable-to-obtain-zoneddatetime-from-temporalaccessor-using-datetimeformatter-and/27285822#27285822
	def safeParse(String value) {
		if (type == ZonedDateTime.class) {
			SmartDateParser.parse(value, defaultTimeZoneId);
		} else {
			return type.parse(value)
		}
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
		config?.flatten()?."$type.simpleName"
	}

	private String getConfigPatternFor(Class type) {
		config?.flatten()?."$type.simpleName"
	}

	private boolean useISO() {
		config?.html5
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
