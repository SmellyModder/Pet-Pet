package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.DisparserExceptions;

/**
 * A simple argument for parsing doubles.
 * 
 * @author Luke Tonon
 */
public final class DoubleArgument implements Argument<Double> {
	private final double minimum;
	private final double maximum;

	private DoubleArgument(double minimum, double maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return The default instance.
	 */
	public static DoubleArgument get() {
		return new DoubleArgument(Double.MIN_VALUE, Double.MAX_VALUE);
	}

	/**
	 * Creates a new {@link DoubleArgument} that clamps the parsable value.
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link DoubleArgument} that clamps the parsable value.
	 */
	public static DoubleArgument getClamped(double min, double max) {
		return new DoubleArgument(min, max);
	}

	/**
	 * Creates a new {@link DoubleArgument} that minimums the parsable value.
	 * @param min Minimum value
	 * @return a new {@link DoubleArgument} that minimums the parsable value.
	 */
	public static DoubleArgument getMin(double min) {
		return new DoubleArgument(min, Double.MAX_VALUE);
	}

	/**
	 * Creates a new {@link DoubleArgument} that maxes the parsable value.
	 * @param max Max value
	 * @return a new {@link DoubleArgument} that maxes the parsable value.
	 */
	public static DoubleArgument getMax(double max) {
		return new DoubleArgument(Double.MIN_VALUE, max);
	}

	@Override
	public ParsedArgument<Double> parse(ArgumentReader reader) throws Exception {
		double adouble = reader.nextDouble();
		if (adouble > this.maximum) {
			throw DisparserExceptions.VALUE_TOO_HIGH.create(adouble, this.maximum);
		} else if (adouble < this.minimum) {
			throw DisparserExceptions.VALUE_TOO_LOW.create(adouble, this.minimum);
		}
		return ParsedArgument.parse(adouble);
	}
}