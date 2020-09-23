package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;

/**
 * A simple argument for parsing booleans.
 * 
 * @author Luke Tonon
 */
public final class BooleanArgument implements Argument<Boolean> {
	
	private BooleanArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static BooleanArgument get() {
		return new BooleanArgument();
	}
	
	@Override
	public ParsedArgument<Boolean> parse(ArgumentReader reader) {
		return ParsedArgument.parse(reader.nextBoolean());
	}
	
}