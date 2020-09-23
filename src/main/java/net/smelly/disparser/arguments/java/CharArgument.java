package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;

/**
 * A simple argument for parsing characters.
 * 
 * @author Luke Tonon
 */
public final class CharArgument implements Argument<Character> {

	private CharArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static CharArgument get() {
		return new CharArgument();
	}
	
	@Override
	public ParsedArgument<Character> parse(ArgumentReader reader) throws Exception {
		return ParsedArgument.parse(reader.nextChar());
	}

}