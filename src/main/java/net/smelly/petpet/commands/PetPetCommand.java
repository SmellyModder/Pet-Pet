package net.smelly.petpet.commands;

import com.squareup.gifencoder.*;
import com.twelvemonkeys.image.ResampleOp;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.FileUpload;
import net.smelly.petpet.PetPet;
import net.smelly.petpet.ThreadedImageBrightener;
import org.jetbrains.annotations.Nullable;

import javax.imageio.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Luke Tonon
 */
public final class PetPetCommand {
	private static final int FPS_CAP = 60;
	private static final int HAND_SIZE = 112;
	private static final int DEFAULT_X = 12;
	private static final int DEFAULT_Y = 18;
	private static final float PET_SIZE = 100.0F;
	private static final String BOT_USER_AGENT = "DiscordBot (https://github.com/DV8FromTheWorld/JDA, 4.2.0_204)";

	public static void processCommand(IReplyCallback replyCallback, @Nullable User user, @Nullable String url, int fps, float scale) {
		fps = fps > FPS_CAP ? FPS_CAP : fps <= 0 ? 1 : fps;
		if (scale < 0.0F) scale = 0.0F;
		String name;
		String contentURL;
		if (user != null) {
			name = user.getName();
			String avatar = user.getAvatarUrl();
			contentURL = avatar != null ? avatar : user.getDefaultAvatarUrl();
		} else if (url != null) {
			String[] split = url.split("/");
			name = split[split.length - 1];
			contentURL = url;
		} else return;
		try {
			URLConnection connection = new URL(contentURL).openConnection();
			connection.setRequestProperty("User-Agent", BOT_USER_AGENT);
			InputStream inputStream = connection.getInputStream();
			File outputGif = new File(String.format("%1$s/petpet_%2$s.gif", PetPet.getGifDirectory(), name));
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
				BufferedImage bufferedImage = ImageIO.read(new File(PetPet.getGifDirectory() + "/petpet.png"));
				BufferedImage image = ImageIO.read(inputStream);
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
				inputStream.close();
				replyCallback.replyFiles(FileUpload.fromData(outputGif, outputGif.getName())).queue();
				outputGif.deleteOnExit();
			} catch (Exception e) {
				replyCallback.reply(":x: Command failed, an error occurred trying to read and process the image").queue();
			}
		} catch (Throwable e) {
			if (e instanceof OutOfMemoryError) {
				replyCallback.reply(":x: Command failed, image was too large!").queue();
			} else {
				replyCallback.reply(":x: Command failed, could not retrieve image from url (`" + contentURL + "`)").queue();
			}
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
