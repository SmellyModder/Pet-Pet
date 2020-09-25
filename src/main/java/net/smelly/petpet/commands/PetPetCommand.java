package net.smelly.petpet.commands;

import com.squareup.gifencoder.*;
import com.twelvemonkeys.image.ResampleOp;
import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.EitherArgument;
import net.smelly.disparser.arguments.java.FloatArgument;
import net.smelly.disparser.arguments.java.IntegerArgument;
import net.smelly.disparser.arguments.java.URLArgument;
import net.smelly.disparser.arguments.jda.UserArgument;
import net.smelly.disparser.feedback.BiDynamicCommandExceptionCreator;
import net.smelly.disparser.feedback.CommandSyntaxException;
import net.smelly.disparser.feedback.SimpleCommandExceptionCreator;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.smelly.petpet.ServerDataManager;
import net.smelly.petpet.ThreadedImageBrightener;

import javax.imageio.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
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

	private static final BiDynamicCommandExceptionCreator<String, Boolean> UNABLE_TO_FETCH_EXCEPTION = BiDynamicCommandExceptionCreator.createInstance((string, bool) -> {
		return bool ? String.format("Could not retrieve and process image for user %s", string) : String.format("Could not retrieve and process image from url (`%s`)", string);
	});
	private static final SimpleCommandExceptionCreator IMAGE_SIZE_TOO_LARGE_EXCEPTION = new SimpleCommandExceptionCreator("Processed image size was too large!");
	private static final SimpleCommandExceptionCreator IMAGE_READ_EXCEPTION = new SimpleCommandExceptionCreator("An error occurred trying to read the image");

	public PetPetCommand() {
		super("pet", EitherArgument.of(UserArgument.get(), URLArgument.get()).asOptional(), IntegerArgument.get().asOptional(), FloatArgument.getMin(0.0F).asOptional());
	}

	@Override
	public void processCommand(CommandContext context) throws Exception {
		EitherArgument.Either<User, URL> userURLEither = context.getParsedResult(0);
		int fps = this.getClampedFPS(context.getParsedResultOrElse(1, 15));
		float scale = context.getParsedResultOrElse(2, 1.0F);
		if (userURLEither != null) {
			User user = userURLEither.first;
			if (user != null) {
				this.processURL(context, fps, scale, user.getName(), this.getAvatarURL(user), true);
			} else {
				URL url = userURLEither.second;
				String[] split = url.toString().split("/");
				this.processURL(context, fps, scale, split[split.length - 1], url, false);
			}
		} else {
			List<Message.Attachment> attachments = context.getEvent().getMessage().getAttachments();
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

	private int getClampedFPS(int fps) {
		return fps > FPS_CAP ? FPS_CAP : fps < 0 ? 1 : fps;
	}

	private void processURL(CommandContext context, int fps, float scale, String name, URL url, boolean user) throws CommandSyntaxException {
		try {
			URLConnection connection = url.openConnection();
			connection.setRequestProperty(USER_AGENT, BOT_USER_AGENT);
			this.processPet(context, fps, scale, name, connection.getInputStream());
		} catch (Throwable e) {
			if (e instanceof OutOfMemoryError) {
				throw IMAGE_SIZE_TOO_LARGE_EXCEPTION.create();
			} else {
				throw UNABLE_TO_FETCH_EXCEPTION.create(user ? name : url.toString(), user);
			}
		}
	}

	private URL getAvatarURL(User user) throws MalformedURLException {
		String avatar = user.getAvatarUrl();
		return new URL(avatar != null ? avatar : user.getDefaultAvatarUrl());
	}

	private void processPet(CommandContext context, int fps, float scale, String name, InputStream stream) throws Exception {
		String gifDirectory = ServerDataManager.INSTANCE.getGifDirectory();
		File outputGif = new File(String.format("%1$s/petpet_%2$s.gif", gifDirectory, name));
		FileOutputStream outputStream = new FileOutputStream(outputGif);
		GifEncoder gifEncoder = new GifEncoder(outputStream, 112, 112, 0);
		ImageOptions options = new ImageOptions()
				.setTransparencyColor(Color.BLACK.getRGB())
				.setColorQuantizer(MedianCutQuantizer.INSTANCE)
				.setDitherer(FloydSteinbergDitherer.INSTANCE)
				.setDisposalMethod(DisposalMethod.DO_NOT_DISPOSE);
		Field delayCentiSeconds = options.getClass().getDeclaredField("delayCentiSeconds");
		delayCentiSeconds.setAccessible(true);
		delayCentiSeconds.set(options, Math.round(100.0F / fps));
		try {
			BufferedImage bufferedImage = ImageIO.read(new File(gifDirectory + "/petpet.png"));
			BufferedImage image = ImageIO.read(stream);
			ThreadedImageBrightener imageDarkener = new ThreadedImageBrightener(image, 0.04F);
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
				int[] rgb = combined.getRGB(0, 0, HAND_SIZE, HAND_SIZE, new int[HAND_SIZE * HAND_SIZE], 0, HAND_SIZE);
				gifEncoder.addImage(rgb, HAND_SIZE, options);
			}

			gifEncoder.finishEncoding();
			stream.close();
			context.getEvent().getChannel().sendFile(outputGif).queue();
			outputGif.deleteOnExit();
		} catch (Exception e) {
			throw IMAGE_READ_EXCEPTION.create();
		}
	}

	//TODO: Change to work mutatively, as in it scales off the previous scaled image to improve performance.
	private enum FrameDrawerType {
		FIRST(((graphics, bufferedImage, width, height) -> {
			graphics.drawImage(new ResampleOp(width, height, ResampleOp.FILTER_LANCZOS).filter(bufferedImage, null), DEFAULT_X + 4, DEFAULT_Y, null);
		})),
		SECOND(((graphics, bufferedImage, width, height) -> {
			graphics.drawImage(new ResampleOp(width, (int) ((float) height * 0.82F), ResampleOp.FILTER_LANCZOS).filter(bufferedImage, null), DEFAULT_X + 4, DEFAULT_Y + 14, null);
		})),
		THIRD(((graphics, bufferedImage, width, height) -> {
			graphics.drawImage(new ResampleOp((int) ((float) width * 1.1F), (int) ((float) height * 0.81F), ResampleOp.FILTER_LANCZOS).filter(bufferedImage, null), DEFAULT_X - 4, DEFAULT_Y + 17, null);
		})),
		FOURTH(((graphics, bufferedImage, width, height) -> {
			graphics.drawImage(new ResampleOp((int) ((float) width * 1.025F), (int) ((float) height * 0.86F), ResampleOp.FILTER_LANCZOS).filter(bufferedImage, null), DEFAULT_X - 4, DEFAULT_Y + 12, null);
		})),
		LAST(((graphics, bufferedImage, width, height) -> {
			graphics.drawImage(new ResampleOp(width, height, ResampleOp.FILTER_LANCZOS).filter(bufferedImage, null), DEFAULT_X, DEFAULT_Y, null);
		}));

		private final Drawer drawer;

		FrameDrawerType(Drawer drawer) {
			this.drawer = drawer;
		}

		@FunctionalInterface
		interface Drawer {
			void draw(Graphics graphics, BufferedImage image, int width, int height);
		}
	}
}
