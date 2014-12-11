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

import spock.lang.*
import grails.plugins.spock.*
import java.time.ZoneId

class ZoneIdEditorSpec extends Specification {

    def editor = new ZoneIdEditor()

    def "getAsText handles null"() {
        when:
        editor.value = null
        then:
        editor.asText == ""
    }

    def "getAsText returns zone ID"() {
        when:
        editor.value = ZoneId.of("Europe/London")
        then:
        editor.asText == "Europe/London"
    }

    def "setAsText accepts zone ID"() {
        when:
        editor.asText = "Europe/London"
        then:
        editor.value == ZoneId.of("Europe/London")
    }

    def "setAsText handles null"() {
        when:
        editor.asText = ""
        then:
        editor.value == null
    }

}