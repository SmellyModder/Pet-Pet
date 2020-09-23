package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.TextChannel;

/**
 * This interface is used to build {@link FeedbackHandler}s.
 * Implement this on types that will be used to build a {@link FeedbackHandler}.
 * @see FeedbackHandler
 * @author Luke Tonon
 */
public interface FeedbackHandlerBuilder {
	FeedbackHandlerBuilder SIMPLE_BUILDER = SimpleFeedbackHandler::new;

	/**
	 * Builds a {@link FeedbackHandler}.
	 * @param textChannel The {@link TextChannel} for building this {@link FeedbackHandler}.
	 * @return a built {@link FeedbackHandler}.
	 */
	FeedbackHandler build(TextChannel textChannel);
}
