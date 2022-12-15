package net.smelly.petpet;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.smelly.petpet.commands.PetPetCommand;

/**
 * @author Luke Tonon
 */
public final class PetPet {
	private static String gifDirectory;

	/**
	 * @param args:
	 *            0 - bot token
	 *            1 - gif directory
	 */
	public static void main(String[] args) {
		JDABuilder botBuilder = JDABuilder.create(args[0], GatewayIntent.getIntents(GatewayIntent.DEFAULT));
		botBuilder.setStatus(OnlineStatus.ONLINE);
		botBuilder.setActivity(Activity.of(Activity.ActivityType.LISTENING, "/petpet"));
		botBuilder.addEventListeners(new PetPetCommandHandler());
		botBuilder.build()
				.updateCommands().addCommands(
						Commands.slash("petpet", "Generates a GIF of a hand petting an image or user.")
								.addSubcommands(
										addBasePetPetOptions(
												new SubcommandData("user", "Generates a GIF of a hand petting a user.")
														.addOption(OptionType.USER, "user", "The user to pet")
										),
										addBasePetPetOptions(
												new SubcommandData("image", "Generates a GIF of a hand petting an image.")
														.addOption(OptionType.ATTACHMENT, "image", "The image to pet", true)
										),
										addBasePetPetOptions(
												new SubcommandData("url", "Generates a GIF of a hand petting an image from a url.")
														.addOption(OptionType.STRING, "url", "The URL of the image to pet", true)
										)
								)
				).queue();
		gifDirectory = args[1];
	}

	public static String getGifDirectory() {
		return gifDirectory;
	}

	private static SubcommandData addBasePetPetOptions(SubcommandData slashCommandData) {
		return slashCommandData.addOption(OptionType.INTEGER, "fps", "The frames per second for the gif")
				.addOption(OptionType.NUMBER, "scale", "The scale of the image getting pet for the gif");
	}

	static class PetPetCommandHandler extends ListenerAdapter {

		private PetPetCommandHandler() {}

		@Override
		public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
			if (event.getName().equals("petpet")) {
				OptionMapping fpsOption = event.getOption("fps");
				int fps = fpsOption != null ? fpsOption.getAsInt() : 15;
				OptionMapping scaleOption = event.getOption("scale");
				float scale = scaleOption != null ? (float) scaleOption.getAsDouble() : 1.0F;
				String subcommand = event.getSubcommandName();
				if ("image".equals(subcommand)) {
					OptionMapping imageOption = event.getOption("image");
					if (imageOption != null) {
						PetPetCommand.processCommand(event, null, imageOption.getAsAttachment().getUrl(), fps, scale);
					}
				} else if ("url".equals(subcommand)) {
					OptionMapping urlOption = event.getOption("url");
					if (urlOption != null) {
						PetPetCommand.processCommand(event, null, urlOption.getAsString(), fps, scale);
					}
				} else {
					OptionMapping userOption = event.getOption("user");
					PetPetCommand.processCommand(event, userOption != null ? userOption.getAsUser() : event.getUser(), null, fps, scale);
				}
			}
		}

	}
}
