package net.smelly.disparser;

import net.smelly.disparser.annotations.Optional;

/**
 * Implemented on classes to be used as arguments in {@link Command}.
 * 
 * @author Luke Tonon
 * 
 * @param <T> Type of this argument
 */
public interface Argument<T> {
	/**
	 * Parses the argument into a {@link ParsedArgument}
	 * 
	 * @param reader - The {@link ArgumentReader} for this argument
	 * @return - The parsed argument containing the parsed object and an error message if an error occurs
	 */
	ParsedArgument<T> parse(final ArgumentReader reader) throws Exception;
	
	/**
	 * If this argument is optional.
	 * <p> Optional arguments will let a {@link CommandHandler} treat this argument as optional. <p>
	 * <b> IMPORTANT: When this is true any command that uses this argument can have this argument's parsed result be null </b>
	 * @return If this argument is optional
	 */
	default boolean isOptional() {
		return this.getClass().getAnnotation(Optional.class) != null;
	}
	
	/**
	 * Creates a new optional instance of this argument
	 * @see Argument#asOptional(Argument)
	 * @return a new optional instance of this argument
	 */
	default Argument<T> asOptional() {
		return Argument.asOptional(this);
	}
	
	/**
	 * Creates a new instance of an argument that's optional
	 * @param <T> - The argument's type
	 * @param <A> - The argument
	 * @param argument - The argument to make a new instance of
	 * @return A new instance of an argument that is optional
	 */
	static <T, A extends Argument<T>> Argument<T> asOptional(A argument) {
		return new Argument<T>() {
			@Override
			public ParsedArgument<T> parse(ArgumentReader reader) throws Exception {
				return argument.parse(reader);
			}
			
			@Override
			public boolean isOptional() {
				return true;
			}
		};
	}
}