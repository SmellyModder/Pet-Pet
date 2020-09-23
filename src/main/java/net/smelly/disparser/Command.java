package net.smelly.disparser;

import net.smelly.disparser.annotations.Optional;
import net.smelly.disparser.util.MessageUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Abstract class for a command.
 * 
 * @author Luke Tonon
 */
public abstract class Command {
	private Set<String> aliases;
	private Set<Permission> requiredPermissions;
	private final List<Argument<?>> arguments;
	
	public Command(String name) {
		this(name, new Argument[0]);
	}
	
	public Command(String name, Argument<?>... args) {
		this(new HashSet<>(Collections.singletonList(name)), new HashSet<>(Arrays.asList(Permission.EMPTY_PERMISSIONS)), args);
	}
	
	public Command(Set<String> aliases, Set<Permission> permissions, Argument<?>... args) {
		this.aliases = aliases;
		this.requiredPermissions = permissions;
		List<Argument<?>> setupArguments = new ArrayList<>();
		for (Argument<?> argument : args) {
			setupArguments.add(argument.getClass().isAnnotationPresent(Optional.class) ? argument.asOptional() : argument);
		}
		this.arguments = setupArguments;
	}
	
	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}
	
	/**
	 * @return This command's aliases.
	 */
	public Set<String> getAliases() {
		return this.aliases;
	}
	
	public void setRequiredPermissions(Set<Permission> requiredPermissions) {
		this.requiredPermissions = requiredPermissions;
	}
	
	/**
	 * @return This command's required permissions.
	 */
	public Set<Permission> getRequiredPermissions() {
		return this.requiredPermissions;
	}
	
	/**
	 * @return This command's arguments.
	 */
	@Nullable
	public List<Argument<?>> getArguments() {
		return this.arguments;
	}
	
	/**
	 * Used for processing this command.
	 * 
	 * @param context - The {@link CommandContext} for this command, use this to get the parsed arguments and make use of the {@link GuildMessageReceivedEvent} event
	 */
	public abstract void processCommand(CommandContext context) throws Exception;
	
	public boolean hasPermissions(Member member) {
		return member.hasPermission(this.getRequiredPermissions());
	}

	/**
	 * Tests for an array of permisssions on a message.
	 * @param message - The message to test.
	 * @param permissions - The permissions to test.
	 * @return True if the sender of the message has the permissions.
	 */
	public boolean testForPermissions(Message message, Permission... permissions) {
		Member member = message.getMember();
		if (member != null && member.hasPermission(permissions)) {
			return true;
		}
		this.sendMessage(message.getTextChannel(), MessageUtil.createErrorMessage("You do not have permission to run this command"));
		return false;
	}

	/**
	 * Queues a message to be sent made up of a {@link CharSequence} to a {@link TextChannel}.
	 * @param channel - The {@link TextChannel} to send the message to.
	 * @param message - The {@link CharSequence} for the message.
	 */
	protected void sendMessage(TextChannel channel, CharSequence message) {
		channel.sendTyping().queue();
		channel.sendMessage(message).queue();
	}

	/**
	 * Queues a embedded message to be sent to a {@link TextChannel}.
	 * @param channel - The {@link TextChannel} to send the message to.
	 * @param message - The {@link MessageEmbed} for the message.
	 */
	protected void sendMessage(TextChannel channel, MessageEmbed message) {
		channel.sendTyping().queue();
		channel.sendMessage(message).queue();
	}
}