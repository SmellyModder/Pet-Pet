package net.smelly.petpet.commands;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.java.FloatArgument;
import net.smelly.disparser.arguments.java.IntegerArgument;
import net.smelly.disparser.arguments.jda.UserArgument;
import net.smelly.disparser.feedback.DynamicCommandExceptionCreator;
import net.smelly.disparser.feedback.SimpleCommandExceptionCreator;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.smelly.petpet.ServerDataManager;
import net.smelly.petpet.ThreadedImageDarkener;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * @author Luke Tonon
 */
public final class PetPetCommand extends Command {
	private static final int FPS_CAP = 60;
	private static final int HAND_SIZE = 112;
	private static final int DEFAULT_X = 12;
	private static final int DEFAULT_Y = 18;
	private static final float PET_SIZE = 100.0F;
	private static final String USER_AGENT = "User-Agent";
	private static final String BOT_USER_AGENT = "DiscordBot (https://github.com/DV8FromTheWorld/JDA, 4.2.0_204)";

	private static final DynamicCommandExceptionCreator<String> UNABLE_TO_FETCH_PFP_EXCEPTION = DynamicCommandExceptionCreator.createInstance((string) -> {
		return String.format("Could not retrieve and process profile picture for user `%s`", string);
	});
	private static final SimpleCommandExceptionCreator IMAGE_SIZE_TOO_LARGE_EXCEPTION = new SimpleCommandExceptionCreator("Processed image size was too large!");
	private static final SimpleCommandExceptionCreator IMAGE_READ_EXCEPTION = new SimpleCommandExceptionCreator("An error occurred trying to read the image");

	public PetPetCommand() {
		super("pet", UserArgument.get().asOptional(), IntegerArgument.get().asOptional(), FloatArgument.getMin(0.0F).asOptional());
	}

	@Override
	public void processCommand(CommandContext context) throws Exception {
		User user = context.getParsedResult(0);
		int fps = this.getClampedFPS(context.getParsedResultOrElse(1, 15));
		float scale = context.getParsedResultOrElse(2, 1.0F);
		if (user != null) {
			try {
				URL url = new URL(user.getAvatarUrl());
				URLConnection connection = url.openConnection();
				connection.setRequestProperty(USER_AGENT, BOT_USER_AGENT);
				this.processPet(context, fps, scale, user.getName(), connection.getInputStream());
			} catch (Throwable e) {
				e.printStackTrace();
				if (e instanceof OutOfMemoryError) {
					throw IMAGE_SIZE_TOO_LARGE_EXCEPTION.create();
				} else {
					throw UNABLE_TO_FETCH_PFP_EXCEPTION.create(user.getName());
				}
			}
		} else {
			List<Message.Attachment> attachments = context.getEvent().getMessage().getAttachments();
			if (!attachments.isEmpty()) {
				for (Message.Attachment attachment : attachments) {
					if (attachment.isImage()) {
						try {
							this.processPet(context, fps, scale, attachment.getFileName(), attachment.retrieveInputStream().get());
						} catch (OutOfMemoryError error) {
							throw IMAGE_SIZE_TOO_LARGE_EXCEPTION.create();
						}
					}
				}
			}
		}
	}

	private int getClampedFPS(int fps) {
		return fps > FPS_CAP ? FPS_CAP : fps < 0 ? 1 : fps;
	}

	private void processPet(CommandContext context, int fps, float scale, String name, InputStream stream) throws Exception {
		String gifDirectory = ServerDataManager.INSTANCE.getGifDirectory();
		File outputGif = new File(String.format("%1$s/petpet_%2$s.gif", gifDirectory, name));
		FileOutputStream outputStream = new FileOutputStream(outputGif);
		AnimatedGifEncoder encoder = new AnimatedGifEncoder();
		encoder.start(outputStream);
		encoder.setQuality(1);
		encoder.setTransparent(Color.WHITE, true);
		encoder.setFrameRate(fps);
		encoder.setRepeat(0);

		try {
			BufferedImage bufferedImage = ImageIO.read(new File(gifDirectory + "/petpet.png"));
			BufferedImage image = ImageIO.read(stream);
			ThreadedImageDarkener imageDarkener = new ThreadedImageDarkener(image);
			imageDarkener.run();

			int inputWidth = image.getWidth(null);
			int inputHeight = image.getHeight(null);

			scale = Math.min(PET_SIZE / (float) inputWidth, PET_SIZE / (float) inputHeight) * scale;
			int width = (int) ((float) inputWidth * scale);
			int height = (int) ((float) inputHeight * scale);

			FrameDrawerType[] drawerTypes = FrameDrawerType.values();
			for (int i = 0; i < drawerTypes.length; i++) {
				BufferedImage combined = new BufferedImage(HAND_SIZE, HAND_SIZE, BufferedImage.TYPE_INT_ARGB);
				Graphics combinedGraphics = combined.getGraphics();
				drawerTypes[i].drawer.draw(combinedGraphics, image, width, height);
				combinedGraphics.drawImage(bufferedImage.getSubimage(i * 112, 0, 112, 112), 0, 0, null);
				combinedGraphics.dispose();
				encoder.addFrame(combined);
			}

			encoder.finish();
			stream.close();
			context.getEvent().getChannel().sendFile(outputGif).queue();
			outputGif.deleteOnExit();
		} catch (ArrayIndexOutOfBoundsException e) {
			throw IMAGE_READ_EXCEPTION.create();
		}
	}

	private enum FrameDrawerType {
		FIRST(((graphics, bufferedImage, width, height) -> {
			graphics.drawImage(bufferedImage.getScaledInstance(width, height, Image.SCALE_DEFAULT), DEFAULT_X + 4, DEFAULT_Y, null);
		})),
		SECOND(((graphics, bufferedImage, width, height) -> {
			graphics.drawImage(bufferedImage.getScaledInstance((int) ((float) width * 1.04F), (int) ((float) height * 0.87F), Image.SCALE_DEFAULT), DEFAULT_X + 4, DEFAULT_Y + 8, null);
		})),
		THIRD(((graphics, bufferedImage, width, height) -> {
			graphics.drawImage(bufferedImage.getScaledInstance((int) ((float) width * 1.08F), (int) ((float) height * 0.74F), Image.SCALE_DEFAULT), DEFAULT_X + 4, DEFAULT_Y + 24, null);
		})),
		FOURTH(((graphics, bufferedImage, width, height) -> {
			graphics.drawImage(bufferedImage.getScaledInstance((int) ((float) width * 1.04F), (int) ((float) height * 0.87F), Image.SCALE_DEFAULT), DEFAULT_X - 4, DEFAULT_Y + 8, null);
		})),
		LAST(((graphics, bufferedImage, width, height) -> {
			graphics.drawImage(bufferedImage.getScaledInstance(width, height, Image.SCALE_DEFAULT), DEFAULT_X, DEFAULT_Y, null);
		}));

		private final Drawer drawer;

		FrameDrawerType(Drawer drawer) {
			this.drawer = drawer;
		}

		@FunctionalInterface
		interface Drawer {
			void draw(Graphics graphics, Image image, int width, int height);
		}
	}
}