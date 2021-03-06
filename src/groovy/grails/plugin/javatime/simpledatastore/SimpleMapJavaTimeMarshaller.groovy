package grails.plugin.javatime.simpledatastore

import org.grails.datastore.mapping.engine.types.AbstractMappingAwareCustomTypeMarshaller
import org.grails.datastore.mapping.query.Query
import org.grails.datastore.mapping.simple.query.SimpleMapResultList
import org.grails.datastore.mapping.model.*

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.MonthDay
import java.time.OffsetDateTime
import java.time.Period
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * A marshaller for Java-Time types usable in the Simple Map datastore.
 * @param < T >
 */
class SimpleMapJavaTimeMarshaller<T> extends AbstractMappingAwareCustomTypeMarshaller<T, Map, SimpleMapResultList> {

    SimpleMapJavaTimeMarshaller(Class<T> targetType) {
        super(targetType)
    }

    @Override
    protected Object writeInternal(PersistentProperty property, String key, T value, Map nativeTarget) {
        nativeTarget[key] = value
    }

    @Override
    protected T readInternal(PersistentProperty property, String key, Map nativeSource) {
        nativeSource[key]
    }

    private static final SUPPORTED_OPERATIONS = [Query.Equals, Query.NotEquals]
    private static final SUPPORTED_OPERATIONS_FOR_COMPARABLE = SUPPORTED_OPERATIONS + [Query.GreaterThan, Query.GreaterThanEquals, Query.LessThan, Query.LessThanEquals, Query.Between]

    @Override
    protected void queryInternal(PersistentProperty property, String key, Query.PropertyCriterion criterion, SimpleMapResultList nativeQuery) {
        def supportedOperations = Comparable.isAssignableFrom(targetType) ? SUPPORTED_OPERATIONS_FOR_COMPARABLE : SUPPORTED_OPERATIONS
        def op = criterion.getClass()
        if (op in supportedOperations) {
            Closure handler = nativeQuery.query.handlers[op]
            nativeQuery.results << handler.call(criterion, property)
        } else {
            throw new RuntimeException("unsupported query type $criterion for property $property")
        }
    }

    static final Iterable<Class> SUPPORTED_TYPES = [LocalTime, LocalDate, LocalDateTime, MonthDay, YearMonth, ZonedDateTime, OffsetDateTime, Instant, Duration, ZoneId, ZoneOffset, Period]

    static initialize() {
        for (type in SUPPORTED_TYPES) {
            MappingFactory.registerCustomType(new SimpleMapJavaTimeMarshaller(type))
        }
    }
}
