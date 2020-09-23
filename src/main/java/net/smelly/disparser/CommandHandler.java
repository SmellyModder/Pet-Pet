package net.smelly.disparser;

import net.smelly.disparser.annotations.Aliases;
import net.smelly.disparser.annotations.Permissions;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles all the command execution.
 * <p> This is a {@link ListenerAdapter} so it can be used as a JDA event listener. <p>
 * 
 * @author Luke Tonon
 */
public class CommandHandler extends ListenerAdapter {
	public final Map<String, Command> aliasMap = Collections.synchronizedMap(new HashMap<>());
	private Function<Guild, String> prefixFunction = (guild) -> "!";
	private FeedbackHandlerBuilder feedbackHandlerBuilder = FeedbackHandlerBuilder.SIMPLE_BUILDER;
	
	private CommandHandler() {}

	protected CommandHandler(String prefix) {
		this((guild) -> prefix);
	}

	protected CommandHandler(String prefix, Command... commands) {
		this((guild) -> prefix, commands);
	}

	protected CommandHandler(Function<Guild, String> prefixFunction, Command... commands) {
		this.prefixFunction = prefixFunction;
		this.registerCommands(Arrays.asList(commands));
	}
	
	protected void registerCommands(List<Command> commands) {
		synchronized (this.aliasMap) {
			commands.forEach(this::registerCommand);
		}
	}

	/**
	 * Registers all command fields from a class. All fields <b> MUST </b> be static to be registered.
	 * @param commandsClazz - The class to lookup command fields to register.
	 */
	protected void registerCommands(Class<?> commandsClazz) {
		synchronized (this.aliasMap) {
			Field[] fields = commandsClazz.getFields();
			for (Field field : fields) {
				try {
					if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
						Aliases aliases = field.getAnnotation(Aliases.class);
						Permissions permissions = field.getAnnotation(Permissions.class);
						field.setAccessible(true);
						Object object = field.get(null);
						if (object instanceof Command) {
							Command command = (Command) object;
							if (aliases != null) {
								this.applyAliases(command, aliases);
							}
							if (permissions != null) {
								this.applyPermissions(command, permissions);
							}
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Registers a command for an alias.
	 * @param alias - The alias for this command.
	 * @param command - The command to register.
	 */
	protected void registerCommand(String alias, Command command) {
		synchronized (this.aliasMap) {
			this.applyAliases(command, command.getClass().getAnnotation(Aliases.class));
			this.applyPermissions(command, command.getClass().getAnnotation(Permissions.class));
			this.aliasMap.put(alias, command);
		}
	}

	/**
	 * Registers a command by all its aliases.
	 * @param command - The command to register.
	 */
	protected void registerCommand(Command command) {
		synchronized (this.aliasMap) {
			command.getAliases().forEach(alias -> this.aliasMap.put(alias, command));
		}
	}

	/**
	 * Applies {@link Aliases} and {@link Permissions}s to {@link Command} fields in a class.
	 * @param clazz - The class to have its fields be applied.
	 * @return This {@link CommandHandler}.
	 */
	public CommandHandler applyAnnotations(Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Aliases aliases = field.getAnnotation(Aliases.class);
			Permissions permissions = field.getAnnotation(Permissions.class);
			field.setAccessible(true);
			try {
				Object object = field.get(clazz.newInstance());
				if (!(object instanceof Command)) return this;
				Command command = (Command) object;
				if (aliases != null) {
					this.applyAliases(command, aliases);
				}
				if (permissions != null) {
					this.applyPermissions(command, permissions);
				}
			} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	/**
	 * Applies an {@link Aliases} to a {@link Command}.
	 * @param command - The command to have the {@link Aliases} applied to.
	 */
	private void applyAliases(Command command, @Nullable Aliases aliases) {
		this.aliasMap.entrySet().stream().filter(entry -> entry.getValue() == command).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).forEach((alias, value) -> this.aliasMap.remove(alias));
		if (aliases != null) {
			Set<String> newAliases = aliases.mergeAliases() ? command.getAliases() : new HashSet<>();
			newAliases.addAll(Arrays.asList(aliases.value()));
			command.setAliases(newAliases);
		}
		this.registerCommand(command);
	}

	/**
	 * Applies an {@link Permissions} to a {@link Command}.
	 * @param command - The command to have the {@link Permissions} applied to.
	 */
	private void applyPermissions(Command command, @Nullable Permissions permissions) {
		if (permissions != null) {
			Set<Permission> newPermissions = permissions.mergePermissions() ? command.getRequiredPermissions() : new HashSet<>();
			newPermissions.addAll(Arrays.asList(permissions.value()));
			command.setRequiredPermissions(newPermissions);
		}
		this.registerCommand(command);
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		CommandContext.createAndDisparse(this, event);
	}
	
	/**
	 * Override this in your own {@link CommandHandler} if you wish to have the prefix be more dynamic and/or configurable.
	 * @param guild - The guild belonging to the sent command.
	 * @return The prefix for the commands.
	 */
	public String getPrefix(Guild guild) {
		return this.prefixFunction.apply(guild);
	}

	/**
	 * Gets this handler's {@link FeedbackHandlerBuilder}.
	 * This is used for creating a {@link FeedbackHandler} to be used for sending feedback when processing commands.
	 * <p> This returns {@link FeedbackHandlerBuilder#SIMPLE_BUILDER} by default. </p>
	 * @return This {@link FeedbackHandlerBuilder} for this {@link CommandHandler}.
	 */
	public FeedbackHandlerBuilder getFeedbackHandlerBuilder() {
		return this.feedbackHandlerBuilder;
	}

	public static class CommandHandlerBuilder {
		private final CommandHandler handler;

		public CommandHandlerBuilder() {
			this.handler = new CommandHandler();
		}

		/**
		 * Sets a prefix for the {@link CommandHandler}.
		 * @param prefix - The prefix to set.
		 * @return This builder.
		 */
		public CommandHandlerBuilder setPrefix(String prefix) {
			this.handler.prefixFunction = (guild) -> prefix;
			return this;
		}

		/**
		 * Sets a prefix function for the {@link CommandHandler}.
		 * @param prefixFunction - The prefix function to set.
		 * @return This builder.
		 */
		public CommandHandlerBuilder setPrefix(Function<Guild, String> prefixFunction) {
			this.handler.prefixFunction = prefixFunction;
			return this;
		}

		/**
		 * Registers a command for an alias.
		 * @param alias - The alias for this command.
		 * @param command - The command to register.
		 * @return This builder.
		 */
		public CommandHandlerBuilder registerCommand(String alias, Command command) {
			this.handler.registerCommand(alias, command);
			return this;
		}

		/**
		 * Registers a command by all its aliases.
		 * @param command - The command to register.
		 * @return This builder.
		 */
		public CommandHandlerBuilder registerCommand(Command command) {
			this.handler.registerCommand( command);
			return this;
		}

		/**
		 * Registers multiple commands by their aliases.
		 * @param commands - The commands to register.
		 * @return This builder.
		 */
		public CommandHandlerBuilder registerCommands(Command... commands) {
			this.handler.registerCommands(Arrays.asList(commands));
			return this;
		}

		/**
		 * Registers all command fields from a class. All fields MUST be static.
		 * @param commandsClazz - The class to lookup command fields to register.
		 * @return This builder.
		 */
		public CommandHandlerBuilder registerCommands(Class<?> commandsClazz) {
			this.handler.applyAnnotations(commandsClazz);
			return this;
		}

		/**
		 * Applies annotated {@link Aliases} and {@link Permissions} annotations to {@link Command} fields in a class.
		 * @param clazz - The class to have its fields be applied.
		 * @return This builder.
		 */
		public CommandHandlerBuilder applyAnnotations(Class<?> clazz) {
			this.handler.applyAnnotations(clazz);
			return this;
		}

		/**
		 * Applies an {@link Aliases} to a {@link Command}.
		 * @param command - The command to have the {@link Aliases} applied to.
		 * @param aliases - The {@link Aliases} annotation to apply.
		 * @return This builder.
		 */
		public CommandHandlerBuilder applyAliases(Command command, @Nonnull Aliases aliases) {
			this.handler.applyAliases(command, aliases);
			return this;
		}

		/**
		 * Applies an {@link Permissions} to a {@link Command}.
		 * @param command - The command to have the {@link Permissions} applied to.
		 * @param permissions - The {@link Permissions} annotation to apply.
		 * @return This builder.
		 */
		public CommandHandlerBuilder applyPermissions(Command command, @Nonnull Permissions permissions) {
			this.handler.applyPermissions(command, permissions);
			return this;
		}

		/**
		 * Sets a {@link FeedbackHandlerBuilder} for the {@link CommandHandler}.
		 * @return This builder.
		 */
		public CommandHandlerBuilder setFeedbackBuilder(FeedbackHandlerBuilder feedbackBuilder) {
			this.handler.feedbackHandlerBuilder = feedbackBuilder;
			return this;
		}

		/**
		 * @return Returns the built {@link CommandHandler}.
		 */
		public CommandHandler build() {
			return this.handler;
		}
	}
}