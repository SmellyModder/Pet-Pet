package net.smelly.disparser.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate this on argument types to make them optional.
 * 
 * @author Luke Tonon
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Optional {}