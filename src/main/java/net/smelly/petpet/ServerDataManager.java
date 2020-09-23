package net.smelly.petpet;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages and stores data for server prefixes for the bot.
 * @author Luke Tonon
 */
public enum ServerDataManager {
	INSTANCE;

	private final Gson gson = new Gson();
	private final Map<String, String> prefixMap = Collections.synchronizedMap(new HashMap<>());
	private String directory, gifDirectory;

	/**
	 * Sets up the {@link ServerDataManager} for the bot.
	 * @param directory The storage directory for the prefixes.
	 * @param gifDirectory The gif directory to create the petpet gifs to, these are scheduled to delete on their own.
	 */
	public void init(String directory, String gifDirectory) {
		this.directory = directory;
		this.gifDirectory = gifDirectory;
		try {
			File[] files = new File(directory).listFiles();
			for (File file : files) {
				try {
					this.prefixMap.put(file.getName(), this.gson.fromJson(new String(Files.readAllBytes(Paths.get(file.getPath()))), String.class));
				} catch (JsonSyntaxException | IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("PetPet Info:" + "\n" + "Loaded " + files.length + " Server Data Files");
		} catch (NullPointerException e) {
			System.out.println(String.format("Could not locate file path: %s", directory));
		}
	}

	public synchronized void setPrefix(Guild guild, String prefix) {
		try {
			String guildId = guild.getId();
			this.prefixMap.put(guildId, prefix);
			Writer writer = Files.newBufferedWriter(Paths.get(this.directory + "/" + guildId));
			this.gson.toJson(prefix, writer);
			writer.close();
		} catch (IOException | JsonIOException e) {
			e.printStackTrace();
		}
	}

	public synchronized String getPrefix(Guild guild) {
		return this.prefixMap.getOrDefault(guild.getId(), "p!");
	}

	public String getGifDirectory() {
		return this.gifDirectory;
	}

	public void updateBotNickname(Guild guild, @Nullable String oldPrefix) {
		Member bot = guild.getMemberByTag("PetPet", "6620");
		String nickname = bot.getNickname();
		String prefix = this.getPrefix(guild);
		if (nickname == null || oldPrefix == null) {
			nickname = "PetPet" + " [" + prefix + "]";
		} else {
			StringBuilder builder = new StringBuilder();
			String[] splitNickname = nickname.split(" ");
			int length = splitNickname.length;
			int charAfterOldPrefix = oldPrefix.length() + 1;
			boolean foundPrefix = false;
			for (int i = 0; i < length; i++) {
				String currentString = splitNickname[i];
				if (i == length - 1 && currentString.length() == charAfterOldPrefix + 1 && currentString.charAt(0) == '[' && currentString.charAt(charAfterOldPrefix) == ']') {
					splitNickname[i] = "[" + prefix + "]";
					foundPrefix = true;
				}
				builder.append(" " + splitNickname[i]);
			}

			if (!foundPrefix) {
				builder.append(String.format(" [%s]", prefix));
			}

			nickname = builder.toString();
		}
		guild.modifyNickname(bot, nickname).queue();
	}
}
