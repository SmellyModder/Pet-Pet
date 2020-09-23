package net.smelly.disparser.annotations;

import net.smelly.disparser.CommandHandler;
import net.dv8tion.jda.api.Permission;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to merge or overwrite a command instance's permissions when registering it to a {@link CommandHandler}.
 * 
 * @author Luke Tonon
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, FIELD})
public @interface Permissions {
	/**
	 * This will merge or overwrite the command's existing permissions depending on the value of {@link #mergePermissions()}.
	 * @return - The permissions for the command.
	 */
	Permission[] value();
	
	/**
	 * @return - If it should merge these permissions with the existing permissions.
	 */
	 boolean mergePermissions() default false;
}