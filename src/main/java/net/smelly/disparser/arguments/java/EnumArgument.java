package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.DisparserExceptions;

/**
 * An argument that parses values of an enum by their name.
 * 
 * @author Luke Tonon
 *
 * @param <E> - The type of enum.
 */
public final class EnumArgument<E extends Enum<?>> implements Argument<E> {
	private final E[] values;
	
	private EnumArgument(Class<E> type) {
		this.values = type.getEnumConstants();
	}
	
	/**
	 * @return An instance containing all the possible values of an enum.
	 */
	public static <E extends Enum<?>> EnumArgument<E> get(Class<E> type) {
		return new EnumArgument<>(type);
	}
	
	@Override
	public ParsedArgument<E> parse(ArgumentReader reader) throws Exception {
		return reader.parseNextArgument((arg) -> {
			for (E type : this.values) {
				if (type.toString().equalsIgnoreCase(arg)) {
					return ParsedArgument.parse(type);
				}
			}
			throw DisparserExceptions.INVALID_ENUM_EXCEPTION.create(arg);
		});
	}
}