package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.DisparserExceptions;

/**
 * A simple argument for parsing shorts.
 * 
 * @author Luke Tonon
 */
public final class ShortArgument implements Argument<Short> {
	private final short minimum;
	private final short maximum;

	private ShortArgument(short minimum, short maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return The default instance.
	 */
	public static ShortArgument get() {
		return new ShortArgument(Short.MIN_VALUE, Short.MAX_VALUE);
	}

	/**
	 * Creates a new {@link ShortArgument} that clamps the parsable value.
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link ShortArgument} that clamps the parsable value.
	 */
	public static ShortArgument getClamped(byte min, byte max) {
		return new ShortArgument(min, max);
	}

	/**
	 * Creates a new {@link ShortArgument} that minimums the parsable value.
	 * @param min Minimum value
	 * @return a new {@link ShortArgument} that minimums the parsable value.
	 */
	public static ShortArgument getMin(byte min) {
		return new ShortArgument(min, Short.MAX_VALUE);
	}

	/**
	 * Creates a new {@link ShortArgument} that maxes the parsable value.
	 * @param max Maximum value
	 * @return a new {@link ShortArgument} that maxes the parsable value.
	 */
	public static ShortArgument getMax(byte max) {
		return new ShortArgument(Short.MIN_VALUE, max);
	}

	@Override
	public ParsedArgument<Short> parse(ArgumentReader reader) throws Exception {
		short ashort = reader.nextShort();
		if (ashort > this.maximum) {
			throw DisparserExceptions.VALUE_TOO_HIGH.create(ashort, this.maximum);
		} else if (ashort < this.minimum) {
			throw DisparserExceptions.VALUE_TOO_LOW.create(ashort, this.minimum);
		}
		return ParsedArgument.parse(ashort);
	}
}