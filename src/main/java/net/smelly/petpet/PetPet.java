package net.smelly.petpet;

import net.smelly.disparser.CommandHandler;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.smelly.petpet.commands.PPCommands;

import javax.security.auth.login.LoginException;

/**
 * @author Luke Tonon
 */
public final class PetPet {
	/**
	 * @param args:
	 *            0 - bot token
	 *            1 - prefix storage directory
	 *            2 - gif directory
	 * @throws LoginException
	 */
	public static void main(String[] args) throws LoginException {
		JDABuilder botBuilder = JDABuilder.create(args[0], GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS & ~GatewayIntent.getRaw(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_TYPING)));
		botBuilder.setStatus(OnlineStatus.ONLINE);
		botBuilder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "Petting Images!"));
		botBuilder.addEventListeners(new PetPetCommandHandler());
		botBuilder.build();
		ServerDataManager.INSTANCE.init(args[1], args[2]);
	}

	static class PetPetCommandHandler extends CommandHandler {

		private PetPetCommandHandler() {
			super((guild) -> ServerDataManager.INSTANCE.getPrefix(guild));
			this.registerCommands(PPCommands.class);
		}

		@Override
		public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
			this.registerCommand(PPCommands.createInfoCommand(event.getGuild()));
			super.onGuildMessageReceived(event);
		}

		@Override
		public void onGuildJoin(GuildJoinEvent event) {
			ServerDataManager.INSTANCE.updateBotNickname(event.getGuild(), null);
		}

	}
}
