package net.smelly.disparser.feedback;

/**
 * This class contains many built-in {@link CommandExceptionCreator}s useful for creating exceptions.
 * All of these fields are used internally in Disparser.
 * @author Luke Tonon
 */
public final class DisparserExceptions {
	public static final DynamicCommandExceptionCreator<String> INVALID_INTEGER_EXCEPTION = DynamicCommandExceptionCreator.createInstance(integer -> {
		return String.format("`%s` is not a valid integer!", integer);
	});
	public static final DynamicCommandExceptionCreator<String> INVALID_LONG_EXCEPTION = DynamicCommandExceptionCreator.createInstance(along -> {
		return String.format("`%s` is not a valid long", along);
	});
	public static final DynamicCommandExceptionCreator<String> INVALID_CHAR_EXCEPTION = DynamicCommandExceptionCreator.createInstance(character -> {
		return String.format("`%s` is not a valid char!", character);
	});
	public static final DynamicCommandExceptionCreator<String> INVALID_SHORT_EXCEPTION = DynamicCommandExceptionCreator.createInstance(ashort -> {
		return String.format("`%s` is not a valid short!", ashort);
	});
	public static final DynamicCommandExceptionCreator<String> INVALID_BYTE_EXCEPTION = DynamicCommandExceptionCreator.createInstance(abyte -> {
		return String.format("`%s` is not a valid byte!", abyte);
	});
	public static final DynamicCommandExceptionCreator<String> INVALID_FLOAT_EXCEPTION = DynamicCommandExceptionCreator.createInstance(afloat -> {
		return String.format("`%s` is not a valid float!", afloat);
	});
	public static final DynamicCommandExceptionCreator<String> INVALID_DOUBLE_EXCEPTION = DynamicCommandExceptionCreator.createInstance(adouble -> {
		return String.format("`%s` is not a valid double!", adouble);
	});
	public static final DynamicCommandExceptionCreator<String> INVALID_ENUM_EXCEPTION = DynamicCommandExceptionCreator.createInstance(type -> {
		return String.format("`%s` is not a valid type!", type);
	});
	public static final DynamicCommandExceptionCreator<String> INVALID_NUMBER_EXCEPTION = DynamicCommandExceptionCreator.createInstance(number -> {
		return String.format("`%s` is not a valid number!", number);
	});
	public static final DynamicCommandExceptionCreator<String> INVALID_COLOR_EXCEPTION = DynamicCommandExceptionCreator.createInstance(number -> {
		return String.format("`%s` is not a valid color value!", number);
	});
	public static final BiDynamicCommandExceptionCreator<String, Integer> LENGTH_EXCEPTION = BiDynamicCommandExceptionCreator.createInstance((string, length) -> {
		return String.format("`%s` exceeds the length of %o", string, length);
	});
	public static final BiDynamicCommandExceptionCreator<Number, Number> VALUE_TOO_HIGH = BiDynamicCommandExceptionCreator.createInstance((value, max) -> {
		return String.format("Value (`%1$s`) cannot be greater than %2$s", value, max);
	});
	public static final BiDynamicCommandExceptionCreator<Number, Number> VALUE_TOO_LOW = BiDynamicCommandExceptionCreator.createInstance((value, min) -> {
		return String.format("Value (`%1$s`) cannot be lower than %2$s", value, min);
	});
}
