package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.DisparserExceptions;

/**
 * A simple argument for parsing bytes.
 * 
 * @author Luke Tonon
 */
public final class FloatArgument implements Argument<Float> {
	private final float minimum;
	private final float maximum;

	private FloatArgument(float minimum, float maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return The default instance.
	 */
	public static FloatArgument get() {
		return new FloatArgument(Float.MIN_VALUE, Float.MAX_VALUE);
	}

	/**
	 * Creates a new {@link FloatArgument} that clamps the parsable value.
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link FloatArgument} that clamps the parsable value.
	 */
	public static FloatArgument getClamped(float min, float max) {
		return new FloatArgument(min, max);
	}

	/**
	 * Creates a new {@link FloatArgument} that minimums the parsable value.
	 * @param min Minimum value
	 * @return a new {@link FloatArgument} that minimums the parsable value.
	 */
	public static FloatArgument getMin(float min) {
		return new FloatArgument(min, Float.MAX_VALUE);
	}

	/**
	 * Creates a new {@link FloatArgument} that maxes the parsable value.
	 * @param max Max value
	 * @return a new {@link FloatArgument} that maxes the parsable value.
	 */
	public static FloatArgument getMax(float max) {
		return new FloatArgument(Float.MIN_VALUE, max);
	}

	@Override
	public ParsedArgument<Float> parse(ArgumentReader reader) throws Exception {
		float afloat = reader.nextFloat();
		if (afloat > this.maximum) {
			throw DisparserExceptions.VALUE_TOO_HIGH.create(afloat, this.maximum);
		} else if (afloat < this.minimum) {
			throw DisparserExceptions.VALUE_TOO_LOW.create(afloat, this.minimum);
		}
		return ParsedArgument.parse(afloat);
	}
}