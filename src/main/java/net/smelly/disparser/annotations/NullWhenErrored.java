package net.smelly.disparser.annotations;

import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.Argument;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated over fields and methods to signify that the element it's annotated over is null when an error occurs when parsing an {@link Argument} into a {@link ParsedArgument}.
 * Moreover, when an error occurs when parsing an {@link Argument} to a {@link ParsedArgument} the {@link ParsedArgument#getResult()} will be null, and this is a signifier for that.
 */
@Documented
@TypeQualifierNickname
@Nonnull(when = When.MAYBE)
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface NullWhenErrored {}