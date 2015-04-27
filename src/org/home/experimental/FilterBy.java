package org.home.experimental;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * In order to use repeating annotations, TWO annotations need to be in the same file. The top-level annotation should
 * not be public, but its value() needs to be the class of the inner annotation.
 */
@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@interface FiltersBy {
    FilterBy[] value();
}

/**
 *
 */
@Target( { ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(FiltersBy.class)
public @interface FilterBy {
    String value();
}
