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
package grails.plugin.javatime.converters

import grails.converters.JSON
import grails.converters.XML
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class JavaTimeConverters {
	static void registerJsonAndXmlMarshallers() {
		[JSON, XML].each { converter ->
			converter.registerObjectMarshaller(ZonedDateTime, 1) { ZonedDateTime it ->
				if (!it) return null
				DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(it?.zone).format(it)
			}
			converter.registerObjectMarshaller(LocalDate, 2) { LocalDate it ->
				if (!it) return null
				DateTimeFormatter.ISO_LOCAL_DATE.format(it)
			}
			converter.registerObjectMarshaller(LocalTime, 3) { LocalTime it ->
				if (!it) return null
				DateTimeFormatter.ISO_LOCAL_TIME.format(it)
			}
			converter.registerObjectMarshaller(LocalDateTime, 4) { LocalDateTime it ->
				if (!it) return null
				DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(it)
			}
			converter.registerObjectMarshaller(ZoneId, 5) { ZoneId it ->
				if (!it) return null
				it?.id
			}
		}
	}
}
