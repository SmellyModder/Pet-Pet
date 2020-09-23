package net.smelly.disparser.util;

import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.java.StringArgument;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple base command for an info command.
 * 
 * @author Luke Tonon
 */
public class InfoCommand extends Command {
	private MessageEmbed mainInfoMessage;
	private Map<String, MessageEmbed> commandInfoMessages;
	
	public InfoCommand(MessageEmbed mainInfoMessage) {
		super("info", StringArgument.get().asOptional());
		this.mainInfoMessage = mainInfoMessage;
	}

	/**
	 * Sets the main info {@link MessageEmbed} for this {@link InfoCommand}.
	 * @param mainInfoMessage - The main info {@link MessageEmbed}.
	 * @return This {@link InfoCommand}.
	 */
	public InfoCommand setMainInfoMessages(MessageEmbed mainInfoMessage) {
		this.mainInfoMessage = mainInfoMessage;
		return this;
	}

	/**
	 * Sets the map for the info of commands.
	 * This map controls what {@link MessageEmbed} will be sent when a string is put after {prefix}info.
	 * Example: !info my_command
	 *
	 * @param commandInfoMessages - The map for the info of commands.
	 * @return This {@link InfoCommand}.
	 */
	public InfoCommand setCommandInfoMessages(Map<String, MessageEmbed> commandInfoMessages) {
		this.commandInfoMessages = commandInfoMessages;
		return this;
	}

	/**
	 * Puts a {@link MessageEmbed} for a command name.
	 * @see {@link #setCommandInfoMessages}.
	 *
	 * @param commandName - The name to put as the key.
	 * @param commandInfo - The {@link MessageEmbed} to be displayed for the info of the {@param commandName} key.
	 * @return This {@link InfoCommand}.
	 */
	public InfoCommand putCommandInfo(String commandName, MessageEmbed commandInfo) {
		if (this.commandInfoMessages == null) this.commandInfoMessages = Collections.synchronizedMap(new HashMap<>());
		this.commandInfoMessages.put(commandName, commandInfo);
		return this;
	}

	/**
	 * Processes the command.
	 * @param context - The {@link CommandContext} for this command.
	 */
	@Override
	public void processCommand(CommandContext context) {
		TextChannel channel = context.getEvent().getChannel();
		if (this.commandInfoMessages != null) {
			String commandName = context.getParsedResult(0);
			if (commandName != null) {
				MessageEmbed commandDescription = this.commandInfoMessages.get(commandName);
				if (commandDescription != null) {
					channel.sendMessage(commandDescription).queue();
					return;
				}
			}
		}
		channel.sendMessage(this.mainInfoMessage).queue();
	}
}