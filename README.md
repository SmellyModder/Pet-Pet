![Logo](https://cdn.discordapp.com/avatars/727309426549063732/158e4ff08e5ebf32b6d8468f82356712.png?size=128)

# What is Cross-Poster?
Cross-Poster Bot is a bot that can crosspost messages in text channels to other channels.
The bot has some ways of configuring the crossposting and was originally designed to be used for showcase channels on discord servers.
Showcase channels are channels where images, videos, etc are showcased for people to discuss in another channel.
Cross-Poster makes showcase channels a lot easier since users can use the `show` command to have a message sent in a discussion channel be cross-posted to a showcase channel.
By default the bot requires a message must contain at least one attachment (i.e. images, videos, etc) for it to crosspost it, but this can be configured by using the `require_attachment` command.

Invite Link: https://discord.com/api/oauth2/authorize?client_id=727309426549063732&permissions=671386624&scope=bot

## Commands
* `info`
   * Shows info on the bot or a command. `command` is optional, meaning it doesn't have to be present in the command.
   * Arguments:
      * `command` (Optional; String) - The command to get info on
* `prefix`
   * Assigns a new command prefix to the bot.
   * Arguments:
      * `prefix` (String) - The new prefix to set
* `show` 
   * Crossposts a message if the current channel crossposts to a channel.
   * Arguments: None
* `enable_crosspost`
   * Enables crossposting on a channel to another channel.
   * Arguments: 
      * `channel` (Text Channel ID) - The ID of the Text Channel to make this channel crosspost to
* `disable_crosspost`
   * Disables crossposting on a channel to another channel.
   * Arguments:
      * `channel` (Text Channel ID) - The ID of the Text Channel to make this channel no longer crosspost to
* `require_attachment`
   * Sets whether a crossposting channel should need its messages to contain at least one attachment (i.e. images, videos, etc) to crosspost.
   * Arguments:
      * `require` (Boolean) - If the channel should require at least one attachment, represented by true or false
