package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * This interface is used for sending feedback in text channels when a command is processed.
 * Implement this on types that will be used for sending command feedback.
 * @see SimpleFeedbackHandler
 * @see FeedbackHandlerBuilder#SIMPLE_BUILDER
 * @author Luke Tonon
 */
public interface FeedbackHandler {
	/**
	 * Sends a {@link CharSequence} to a text channel.
	 * @param charSequence The {@link CharSequence} to send.
	 */
	void sendFeedback(CharSequence charSequence);

	/**
	 * Sends a {@link MessageEmbed} to a text channel.
	 * @param messageEmbed The {@link MessageEmbed} to send.
	 */
	void sendFeedback(MessageEmbed messageEmbed);

	/**
	 * Sends a success message to a text channel.
	 * This is equivalent to {@link #sendFeedback(CharSequence)} with the difference being this should include a 'success' effect.
	 * @see SimpleFeedbackHandler#sendSuccess(String).
	 * @see net.smelly.disparser.util.MessageUtil#createSuccessfulMessage(String) .
	 * @param message The message to be used for the success message.
	 */
	void sendSuccess(String message);

	/**
	 * Sends an error message to a text channel.
	 * <p> Ideally this should send the exception's {@link Exception#getMessage()}. </p>
	 * @see SimpleFeedbackHandler#sendSuccess(String).
	 * @see net.smelly.disparser.util.MessageUtil#createErrorMessage(String).
	 * @param exception The exception to send the error message for.
	 */
	void sendError(Exception exception);
}
