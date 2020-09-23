package net.smelly.disparser.feedback;

/**
 * A simple interface used for creating new instances of an exception.
 * Ideally all types implementing this interface can be used as builders for an exception.
 * Types implementing this interface can add more create methods if they wish.
 *
 * @author Luke Tonon
 * @param <E> The type of exception to create.
 */
public interface CommandExceptionCreator<E extends Exception> {
	E create();
}
