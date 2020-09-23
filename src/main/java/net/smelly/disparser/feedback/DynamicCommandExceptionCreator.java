package net.smelly.disparser.feedback;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * This class contains a function for creating a {@link CommandSyntaxException}.
 * The function takes in a generic type object and parses it to a string to be used for creating a message for a {@link CommandSyntaxException}.
 * This class can store a object internally to be re-used for creating a {@link CommandSyntaxException}.
 * @author Luke Tonon
 */
public class DynamicCommandExceptionCreator<T> implements CommandExceptionCreator<CommandSyntaxException> {
	private final Function<T, String> function;
	@Nullable
	private final T object;

	public DynamicCommandExceptionCreator(Function<T, String> function) {
		this(null, function);
	}

	public DynamicCommandExceptionCreator(@Nullable T object, Function<T, String> function) {
		this.object = object;
		this.function = function;
	}

	public static <T> DynamicCommandExceptionCreator<T> createInstance(Function<T, String> function) {
		return new DynamicCommandExceptionCreator<>(function);
	}

	@Override
	public CommandSyntaxException create() {
		return new CommandSyntaxException(this.function.apply(this.object));
	}

	public CommandSyntaxException create(T object) {
		return new CommandSyntaxException(this.function.apply(object));
	}

	public CommandSyntaxException createForArgument(int argument) {
		return new CommandSyntaxException(this.function.apply(this.object), argument);
	}

	public CommandSyntaxException createForArgument(T object, int argument) {
		return new CommandSyntaxException(this.function.apply(object), argument);
	}
}
