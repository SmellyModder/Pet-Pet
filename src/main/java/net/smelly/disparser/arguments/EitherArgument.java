package net.smelly.disparser.arguments;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.arguments.EitherArgument.Either;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * A class to hold two arguments in which it will attempt to parse the first argument and then if it fails to do so then attempt to parse the next argument.
 * Due to this logic the second argument's error message will always be the error message displayed.
 * This is very useful for arguments that can be two types, such as a channel argument that can be for Text Channels or Voice Channels.
 * 
 * @author Luke Tonon
 *
 * @param <F> - The first argument's type.
 * @param <S> - The second argument's type.
 * @param <FA> - The first argument matching the type of F
 * @param <SA> - The second argument matching the type of S
 */
public final class EitherArgument<F, S, FA extends Argument<F>, SA extends Argument<S>> implements Argument<Either<F, S>> {
	private final FA firstArgument;
	private final SA secondArgument;
	
	private EitherArgument(FA firstArgument, SA secondArgument) {
		this.firstArgument = firstArgument;
		this.secondArgument = secondArgument;
	}
	
	/**
	 * Constructs a new {@link EitherArgument} instance for two arguments.
	 * @param firstArgument - The first argument.
	 * @param secondArgument - The second argument.
	 * @return a new {@link EitherArgument} instance for two arguments. 
	 */
	public static <F, S, FA extends Argument<F>, SA extends Argument<S>> EitherArgument<F, S, FA, SA> of(FA firstArgument, SA secondArgument) {
		return new EitherArgument<>(firstArgument, secondArgument);
	}
	
	@Override
	public ParsedArgument<Either<F, S>> parse(ArgumentReader reader) throws Exception {
		ParsedArgument<F> first = reader.tryToParseArgument(this.firstArgument);
		if (first.hasResult()) {
			return ParsedArgument.parse(Either.first(first.getResult()));
		}
		return ParsedArgument.parse(Either.second(this.secondArgument.parse(reader).getResult()));
	}
	
	public static final class Either<F, S> {
		@Nullable
		public final F first;
		@Nullable
		public final S second;
		
		private Either(@Nullable F first, @Nullable S second) {
			this.first = first;
			this.second = second;
		}

		/**
		 * @param first - The first object to contain in the {@link Either}.
		 * @param <F> - The type of the first object.
		 * @param <S> - The type of the second object.
		 * @return A {@link Either} containing a first object.
		 */
		public static <F, S> Either<F, S> first(@Nonnull F first) {
			return new Either<>(first, null);
		}

		/**
		 * @param second - The second object to contain in the {@link Either}.
		 * @param <F> - The type of the first object.
		 * @param <S> - The type of the second object.
		 * @return A {@link Either} containing a second object.
		 */
		public static <F, S> Either<F, S> second(@Nonnull S second) {
			return new Either<>(null, second);
		}
		
		/**
		 * Converts an either instance to a type.
		 * @param function - The function for converting.
		 * @return A new instance of a type converted from this either instance.
		 */
		public <T> T convertTo(Function<Either<F, S>, T> function) {
			return function.apply(this);
		}
	}
}