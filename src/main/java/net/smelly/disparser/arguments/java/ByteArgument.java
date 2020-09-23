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
public final class ByteArgument implements Argument<Byte> {
	private final byte minimum;
	private final byte maximum;

	private ByteArgument(byte minimum, byte maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	/**
	 * @return The default instance.
	 */
	public static ByteArgument get() {
		return new ByteArgument(Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

	/**
	 * Creates a new {@link ByteArgument} that clamps the parsable value.
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link ByteArgument} that clamps the parsable value.
	 */
	public static ByteArgument getClamped(byte min, byte max) {
		return new ByteArgument(min, max);
	}

	/**
	 * Creates a new {@link ByteArgument} that minimums the parsable value.
	 * @param min Minimum value
	 * @return a new {@link ByteArgument} that minimums the parsable value.
	 */
	public static ByteArgument getMin(byte min) {
		return new ByteArgument(min, Byte.MAX_VALUE);
	}

	/**
	 * Creates a new {@link ByteArgument} that maxes the parsable value.
	 * @param max Max value
	 * @return a new {@link ByteArgument} that maxes the parsable value.
	 */
	public static ByteArgument getMax(byte max) {
		return new ByteArgument(Byte.MIN_VALUE, max);
	}
	
	@Override
	public ParsedArgument<Byte> parse(ArgumentReader reader) throws Exception {
		byte abyte = reader.nextByte();
		if (abyte > this.maximum) {
			throw DisparserExceptions.VALUE_TOO_HIGH.create(abyte, this.maximum);
		} else if (abyte < this.minimum) {
			throw DisparserExceptions.VALUE_TOO_LOW.create(abyte, this.minimum);
		}
		return ParsedArgument.parse(abyte);
	}
}
