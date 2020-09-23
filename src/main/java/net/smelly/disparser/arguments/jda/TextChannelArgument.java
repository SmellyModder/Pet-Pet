package net.smelly.disparser.arguments.jda;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.SimpleCommandExceptionCreator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An argument that can parse text channels by their ID or a mention of the text channel.
 * Define a JDA to get the text channel from or leave null to use the JDA of the message that was sent.
 * 
 * @author Luke Tonon
 */
public final class TextChannelArgument implements Argument<TextChannel> {
	private static final SimpleCommandExceptionCreator MENTION_CHANNEL_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionCreator("Text Channel in mention could not be found");
	private static final Pattern MENTION_PATTERN = Pattern.compile("^<#(\\d+)>$");
	
	@Nullable
	private final JDA jda;
	
	private TextChannelArgument(@Nullable JDA jda) {
		this.jda = jda;
	}
	
	/**
	 * @return A default instance.
	 */
	public static TextChannelArgument get() {
		return new TextChannelArgument(null);
	}
	
	/**
	 * If you only want to get text channels of the guild that the message was sent from then use {@link #get()}.
	 * @param jda - JDA to get the channel from.
	 * @return An instance of this argument with a JDA.
	 */
	public static TextChannelArgument create(JDA jda) {
		return new TextChannelArgument(jda);
	}
	
	@Override
	public ParsedArgument<TextChannel> parse(ArgumentReader reader) throws Exception {
		return reader.parseNextArgument((arg) -> {
			Guild guild = reader.getChannel().getGuild();
			try {
				long parsedId = Long.parseLong(arg);
				TextChannel foundChannel = this.findhannelWithId(guild, parsedId);
				if (foundChannel != null) {
					return ParsedArgument.parse(foundChannel);
				} else {
					throw GuildChannelArgument.CHANNEL_NOT_FOUND_EXCEPTION.create(parsedId);
				}
			} catch (NumberFormatException exception) {
				Matcher matcher = MENTION_PATTERN.matcher(arg);
				
				if (matcher.matches()) {
					long parsedId = Long.parseLong(matcher.group(1));
					TextChannel foundChannel = this.findhannelWithId(guild, parsedId);
					if (foundChannel != null) {
						return ParsedArgument.parse(foundChannel);
					} else {
						throw MENTION_CHANNEL_NOT_FOUND_EXCEPTION.create();
					}
				}

				throw GuildChannelArgument.INVALID_ID_EXCEPTION.create(arg);
			}
		});
	}

	@Nullable
	private TextChannel findhannelWithId(Guild guild, long id) {
		return this.jda != null ? this.jda.getTextChannelById(id) : guild.getTextChannelById(id);
	}
}