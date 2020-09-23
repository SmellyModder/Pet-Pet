package net.smelly.disparser.arguments.jda;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.DynamicCommandExceptionCreator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Webhook;

import javax.annotation.Nullable;

/**
 * An argument that can parse webhooks by their ID.
 * Define a JDA to get the webhook from or leave null to use the JDA of the message that was sent.
 * 
 * @author Luke Tonon
 */
public final class WebhookArgument implements Argument<Webhook> {
	private static final DynamicCommandExceptionCreator<Long> WEBHOOK_NOT_FOUND_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return String.format("Webhook with id `%d` could not be found", id);
	}));
	private static final DynamicCommandExceptionCreator<String> INVALID_ID_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return String.format("`%s` is not a valid webhook id", id);
	}));
	@Nullable
	private final JDA jda;
	
	private WebhookArgument(JDA jda) {
		this.jda = jda;
	}
	
	/**
	 * @return A default instance.
	 */
	public static WebhookArgument get() {
		return new WebhookArgument(null);
	}
	
	/**
	 * If you only want to get webhooks of the guild that the message was sent from then use {@link #get()}.
	 * @param jda - JDA to get the webhook from.
	 * @return An instance of this argument with a JDA.
	 */
	public static WebhookArgument create(JDA jda) {
		return new WebhookArgument(jda);
	}
	
	@Override
	public ParsedArgument<Webhook> parse(ArgumentReader reader) throws Exception {
		return reader.parseNextArgument((arg) -> {
			try {
				long parsedLong = Long.parseLong(arg);
				Webhook foundWebhook = this.jda == null ? reader.getChannel().getJDA().retrieveWebhookById(parsedLong).submit().get() : this.jda.retrieveWebhookById(parsedLong).submit().get();
				if (foundWebhook != null) {
					return ParsedArgument.parse(foundWebhook);
				} else {
					throw WEBHOOK_NOT_FOUND_EXCEPTION.create(parsedLong);
				}
			} catch (NumberFormatException exception) {
				throw INVALID_ID_EXCEPTION.create(arg);
			}
		});
	}
}