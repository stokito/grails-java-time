package grails.plugin.javatime.binding

import org.grails.databinding.converters.ValueConverter

import java.time.ZoneId

class ZoneIdConverter implements ValueConverter {
    @Override
    boolean canConvert(Object value) {
        value instanceof String
    }

    @Override
    Object convert(Object value) {
        ZoneId.of(value)
    }

    @Override
    Class<?> getTargetType() {
        ZoneId
    }
}
