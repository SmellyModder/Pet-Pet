package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.DisparserExceptions;

import javax.annotation.Nullable;

/**
 * A simple argument for parsing strings.
 * 
 * @author Luke Tonon
 */
public final class StringArgument implements Argument<String> {
	@Nullable
	private final Integer maxChars;

	private StringArgument(@Nullable Integer maxChars) {
		this.maxChars = maxChars;
	}
	
	/**
	 * @return The default instance.
	 */
	public static StringArgument get() {
		return new StringArgument(null);
	}

	/**
	 * @param maxChars - The max length of a string this argument can parse.
	 * @return An instance that has a set max length for the strings it can parse.
	 */
	public static StringArgument create(int maxChars) {
		return new StringArgument(maxChars);
	}
	
	@Override
	public ParsedArgument<String> parse(ArgumentReader reader) throws Exception {
		String nextArgument = reader.nextArgument();
		Integer maxChars = this.maxChars;
		if (maxChars != null && nextArgument.length() > maxChars) {
			throw DisparserExceptions.LENGTH_EXCEPTION.create(nextArgument, maxChars);
		}
		return ParsedArgument.parse(nextArgument);
	}
}