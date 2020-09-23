package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.CommandSyntaxException;
import net.smelly.disparser.feedback.DisparserExceptions;

import java.awt.*;

/**
 * An argument that parses a {@link Color}.
 * This argument also makes use of {@link EnumArgument} to parse a {@link Color} by its name, such as red.
 *
 * @author Luke Tonon
 */
public final class ColorArgument implements Argument<Color> {
	private final EnumArgument<ColorType> colorTypeEnumArgument;

	private ColorArgument() {
		this.colorTypeEnumArgument = EnumArgument.get(ColorType.class);
	}

	/**
	 * @return The default instance.
	 */
	public static ColorArgument get() {
		return new ColorArgument();
	}

	@Override
	public ParsedArgument<Color> parse(ArgumentReader reader) throws Exception {
		ColorType colorType = reader.tryToParseArgument(this.colorTypeEnumArgument).getResult();
		if (colorType != null) {
			return ParsedArgument.parse(colorType.color);
		}
		try {
			return ParsedArgument.parse(new Color(reader.nextInt()));
		} catch (CommandSyntaxException e) {
			throw DisparserExceptions.INVALID_COLOR_EXCEPTION.create(reader.getCurrentMessageComponent());
		}
	}

	public enum ColorType {
		WHITE(Color.WHITE),
		BLACK(Color.BLACK),
		BROWN(new Color(102, 51, 0)),
		LIGHT_GRAY(Color.LIGHT_GRAY),
		GRAY(Color.GRAY),
		DARK_GRAY(Color.DARK_GRAY),
		RED(Color.RED),
		PINK(Color.PINK),
		ORANGE(Color.GREEN),
		YELLOW(Color.YELLOW),
		GREEN(Color.GREEN),
		CYAN(Color.CYAN),
		BLUE(Color.BLUE),
		PURPLE(new Color(102, 0, 153)),
		MAGENTA(Color.MAGENTA);

		private Color color;

		ColorType(Color color) {
			this.color = color;
		}
	}
}
