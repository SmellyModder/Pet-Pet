package net.smelly.disparser;

import net.smelly.disparser.annotations.NullWhenErrored;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A container object for holding a parsed result from an {@link Argument}. Ideally similar to {@link java.util.Optional}
 * <p>
 * {@link #result} should <b> always </b> be null when a parsing error occurs.
 * </p>
 * @author Luke Tonon
 *
 * @param <A> - The type for this parsed argument.
 */
public final class ParsedArgument<A> {
	private static final ParsedArgument<?> EMPTY = new ParsedArgument<>(null);

	@NullWhenErrored
	private final A result;
	
	private ParsedArgument(@Nullable final A readArgument) {
		this.result = readArgument;
	}

	/**
	 * @return The parsed result.
	 */
	@NullWhenErrored
	public A getResult() {
		return this.result;
	}

	/**
	 * @throws NullPointerException if other value is null
	 * @return The parsed result or other result if null.
	 */
	public A getOrOtherResult(@Nonnull A other) {
		Objects.requireNonNull(other);
		return this.result == null ? other : this.result;
	}

	/**
	 * @return If this {@link ParsedArgument} has a parsed result.
	 */
	public boolean hasResult() {
		return this.result != null;
	}

	/**
	 * Checks if this {@link ParsedArgument} has a parsed result and then accepts a consumer on the result.
	 * @param consumer - The consumer to accept on the result.
	 */
	public void ifHasResult(Consumer<A> consumer) {
		if (this.hasResult()) consumer.accept(this.result);
	}

	/**
	 * @param result - The result.
	 * @param <A> - The type of the result.
	 * @throws NullPointerException if value is null
	 * @return A new {@link ParsedArgument} that contains a non-null result.
	 */
	public static <A> ParsedArgument<A> parse(@Nonnull final A result) {
		Objects.requireNonNull(result);
		return new ParsedArgument<>(result);
	}

	/**
	 * @param result - The result.
	 * @param <A> - The type of the result.
	 * @return A new {@link ParsedArgument} that contains a nullable result.
	 */
	public static <A> ParsedArgument<A> parseNullable(@Nullable final A result) {
		return result == null ? empty() : new ParsedArgument<>(result);
	}

	/**
	 * @param <A> - The type of the result.
	 * @return A new {@link ParsedArgument} that contains a null result.
	 */
	@SuppressWarnings("unchecked")
	public static <A> ParsedArgument<A> empty() {
		return (ParsedArgument<A>) EMPTY;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.result);
	}

	@Override
	public String toString() {
		return this.result != null ? String.format("ParsedArgument[%s]", this.result) : "ParsedArgument.empty";
	}
}