package net.smelly.disparser.feedback;

import net.smelly.disparser.util.MessageUtil;

import javax.annotation.Nullable;

/**
 * An exception that represents a command syntax exception.
 * @see SimpleCommandExceptionCreator
 * @author Luke Tonon
 */
@SuppressWarnings("serial")
public class CommandSyntaxException extends Exception {
	private final String message;
	@Nullable
	private final Integer argumentIndex;

	/**
	 * A simple constructor that sets a message for this exception.
	 * @param message Message for the exception, {@link #getMessage()}.
	 */
	public CommandSyntaxException(String message) {
		this(message, null);
	}

	/**
	 * A constructor that sets a message for this exception and an argument index to be processed in {@link #getMessage()}.
	 * @param message Message for the exception.
	 * @param argumentIndex Index of the argument to be processed in {@link #getMessage()}.
	 * @see #getMessage()
	 */
	public CommandSyntaxException(String message, @Nullable Integer argumentIndex) {
		super(message, null, true, false);
		this.message = message;
		this.argumentIndex = argumentIndex;
	}

	@Override
	public String getMessage() {
		if (this.argumentIndex != null) {
			return String.format("Error at **%o%s** argument: %s", this.argumentIndex, MessageUtil.getOrdinalForInteger(this.argumentIndex), this.message);
		}
		return this.message;
	}
}
