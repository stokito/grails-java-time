package grails.plugin.javatime

import java.time.format.DateTimeFormatter

/**
 * Provides a set of DateTimeFormatters that parse and format correctly for the various HTML5 date & time input types.
 */
class Html5DateTimeFormat {

	static DateTimeFormatter month() {
		DateTimeFormatter.ofPattern("yyyy-MM")
	}

	static DateTimeFormatter week() {
		DateTimeFormatter.ISO_WEEK_DATE
	}

	static DateTimeFormatter date() {
		DateTimeFormatter.ISO_DATE
	}

	static DateTimeFormatter time() {
		DateTimeFormatter.ISO_TIME
	}

	static DateTimeFormatter datetimeLocal() {
		DateTimeFormatter.ISO_LOCAL_DATE_TIME
	}

	static DateTimeFormatter datetime() {
		DateTimeFormatter.ISO_ZONED_DATE_TIME
	}

	private Html5DateTimeFormat() {}

}
