package net.smelly.petpet.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.annotations.Aliases;
import net.smelly.disparser.annotations.Permissions;
import net.smelly.disparser.util.InfoCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.smelly.petpet.ServerDataManager;

import java.util.*;

/**
 * @author Luke Tonon
 */
public final class PPCommands {
	private static final String DESCRIPTION =
			"PetPet is a simple bot for generating pet-pet gifs for images and user profile pictures.\n" +
			"-------------------------------------------------------------------------------------------------\n" +
			"Use the `pet` command to pet an image or user.\n\nUse the `prefix` command to set a new command prefix for the bot for this server (Must be an Admin!).\n\nFor more info on a command use the `info/help` command for a command, for example, `info pet`";
	private static final String AUTHOR = "Created by Luke Tonon, @Smelly²#3450";
	private static final String ICON_URL = "https://cdn.discordapp.com/avatars/347143578096500736/a_33c085f65b80b06586dc680d4252409f.gif?size=128";
	private static final Set<String> INFO_ALIASES = createAliases("info", "help");

	private static final Map<String, MessageEmbed> COMMAND_INFOS = new HashMap<>();

	@Aliases(value = "petpet", mergeAliases = true)
	public static final PetPetCommand PET_PET_COMMAND = registerCommand(new PetPetCommand(), new EmbedBuilder()
			.setTitle("Pet Commmand Help & Info")
			.addField("Description", "This command pets any user's profile picture or image. The exported gif to be pet will be an overlaid hand petting the image. There are two extra optional variables for the image export; FPS and scale.\n" +
					"When the `source` argument is not used it will pet any images uploaded with the message.", true)
			.addField("Arguments", "`<source:optional_either_user_or_link>, <fps:optional_integer> <scale:optional_number>`\n" +
							"\n`<source:optional_either_user_or_link>` - The user or source-link to be pet. For users, this takes in a mention or the user's id and the user must be in this server!\n" +
							"\n`<fps:optional_integer>` - The frame rate to use for the exported gif, defaults to 15. This must be an integer from 0 - 60.\n" +
							"\n`<scale:optional_number>` - The scale to apply for the input image to use for the exported gif, defaults to 1.0. This must be a positive number.",
					false)
			.addField("Examples", "`pet @User#0000 20 1.15`\n`pet 347143578096500736 60`\n`pet 1.25 (With image attached)`\n`pet https://rb.gy/bbskhd`", true)
			.setFooter("Permissions: None\nAliases: pet, petpet")
			.setColor(7506394)
			.build()
	);

	@Permissions(Permission.ADMINISTRATOR)
	public static final PrefixCommand PREFIX_COMMAND = registerCommand(new PrefixCommand(), new EmbedBuilder()
			.setTitle("Prefix Command Help & Info")
			.addField("Description", "This command sets a new command prefix for this server. This command requires a one to six character prefix and admin permission.", true)
			.addField("Arguments", "`<prefix:one_to_six_string>` - The new one to six character prefix to set as the command prefix.\n", false)
			.addField("Examples", "`prefix !`\n`prefix p~`\n`prefix pet`", true)
			.setFooter("Permissions: Administrator\nAliases: prefix")
			.setColor(7506394)
			.build()
	);

	private static <C extends Command> C registerCommand(C command, MessageEmbed messageEmbed) {
		command.getAliases().forEach(alias -> COMMAND_INFOS.put(alias, messageEmbed));
		return command;
	}

	public static InfoCommand createInfoCommand(Guild guild) {
		InfoCommand infoCommand = new InfoCommand(createMainInfoMessage(guild)).setCommandInfoMessages(COMMAND_INFOS);
		infoCommand.setAliases(INFO_ALIASES);
		return infoCommand;
	}

	private static MessageEmbed createMainInfoMessage(Guild guild) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("PetPet Help & Info!");
		builder.appendDescription(String.format("**Prefix: **`%s`\n", ServerDataManager.INSTANCE.getPrefix(guild)));
		builder.appendDescription(DESCRIPTION);
		builder.setColor(7506394);
		builder.setFooter(AUTHOR, ICON_URL);
		return builder.build();
	}

	private static Set<String> createAliases(String... strings) {
		Set<String> aliases = new HashSet<>();
		Collections.addAll(aliases, strings);
		return aliases;
	}
}
