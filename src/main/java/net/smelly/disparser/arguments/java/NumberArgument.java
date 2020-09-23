package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.DisparserExceptions;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * A simple argument for parsing a number.
 * This allows for more broad use of number values and not just targeting ints, floats, doubles, etc.
 *
 * @author Luke Tonon
 */
public final class NumberArgument implements Argument<Number> {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private final double minimum;
    private final double maximum;

    private NumberArgument(double minimum, double maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    /**
     * @return The default instance.
     */
    public static NumberArgument get() {
        return new NumberArgument(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Creates a new {@link NumberArgument} that clamps the parsable value.
     * @param min Minimum value
     * @param max Maximum value
     * @return a new {@link NumberArgument} that clamps the parsable value.
     */
    public static NumberArgument getClamped(double min, double max) {
        return new NumberArgument(min, max);
    }

    /**
     * Creates a new {@link NumberArgument} that minimums the parsable value.
     * @param min Minimum value
     * @return a new {@link NumberArgument} that minimums the parsable value.
     */
    public static NumberArgument getMin(double min) {
        return new NumberArgument(min, Double.MAX_VALUE);
    }

    /**
     * Creates a new {@link NumberArgument} that maxes the parsable value.
     * @param max Maximum value
     * @return a new {@link NumberArgument} that maxes the parsable value.
     */
    public static NumberArgument getMax(double max) {
        return new NumberArgument(Double.MIN_VALUE, max);
    }

    @Override
    public ParsedArgument<Number> parse(ArgumentReader reader) throws Exception {
        return reader.parseNextArgument((arg) -> {
            try {
                Number number = NUMBER_FORMAT.parse(arg);
                double adouble = number.doubleValue();
                if (adouble > this.maximum) {
                    throw DisparserExceptions.VALUE_TOO_HIGH.create(adouble, this.maximum);
                } else if (adouble < this.minimum) {
                    throw DisparserExceptions.VALUE_TOO_LOW.create(adouble, this.minimum);
                }
                return ParsedArgument.parse(number);
            } catch (ParseException e) {
                throw DisparserExceptions.INVALID_NUMBER_EXCEPTION.create(arg);
            }
        });
    }
}
