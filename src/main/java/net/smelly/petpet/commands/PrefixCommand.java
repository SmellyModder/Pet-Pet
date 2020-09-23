package net.smelly.petpet.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.java.StringArgument;
import net.dv8tion.jda.api.entities.Guild;
import net.smelly.petpet.ServerDataManager;

/**
 * @author Luke Tonon
 */
public final class PrefixCommand extends Command {

	public PrefixCommand() {
		super("prefix", StringArgument.create(6));
	}

	@Override
	public void processCommand(CommandContext context) throws Exception {
		Guild guild = context.getEvent().getGuild();
		String newPrefix = context.getParsedResult(0);
		String oldPrefix = ServerDataManager.INSTANCE.getPrefix(guild);
		ServerDataManager.INSTANCE.setPrefix(guild, newPrefix);
		ServerDataManager.INSTANCE.updateBotNickname(guild, oldPrefix);
		context.getFeedbackHandler().sendSuccess(String.format("Successfully changed prefix to `%s`", newPrefix));
	}

}
