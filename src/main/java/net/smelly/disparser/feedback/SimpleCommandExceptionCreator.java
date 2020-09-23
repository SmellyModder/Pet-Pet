package net.smelly.disparser.feedback;

/**
 * A simple class for sending an exception with a message.
 * @see CommandExceptionCreator
 * @see CommandSyntaxException
 * @author Luke Tonon
 */
public final class SimpleCommandExceptionCreator implements CommandExceptionCreator<CommandSyntaxException> {
	private final String message;

	public SimpleCommandExceptionCreator(String message) {
		this.message = message;
	}

	/**
	 * @return Creates a new {@link CommandSyntaxException} with this creator's message.
	 */
	@Override
	public CommandSyntaxException create() {
		return new CommandSyntaxException(this.message);
	}

	/**
	 * @return Creates a new {@link CommandSyntaxException} with this creator's message.
	 */
	public CommandSyntaxException createForArgument(int argument) {
		return new CommandSyntaxException(this.message, argument);
	}
}
