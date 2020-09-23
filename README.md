![Logo](https://media.giphy.com/media/hprAf7DbntntXN3SDr/giphy.gif)

# What is Pet-Pet?
PetPet is a fairly simple Discord Bot that can generate pet-pet GIFs for images and user profiles.
To generate a pet-pet GIF use the `pet` command.
The prefix for this bot is customizable with the `prefix` command.

Invite Link: https://discord.com/api/oauth2/authorize?client_id=727309426549063732&permissions=671386624&scope=bot

## Commands
* `info/help`
   * Shows info on the bot or a command. `command_name` is optional, meaning it doesn't have to be present in the command.
   * Arguments:
      * `<command_name:optional_string>` - The command to get info on
* `prefix`
   * This command sets a new command prefix for this server. This command requires a one to six character prefix and admin permission.
   * Arguments:
      * `<prefix:one_to_six_string>` - The new one to six character prefix to set as the command prefix.
* `pet` 
   * This command pets any user's profile picture or image. The exported gif to be pet will be an overlaid hand petting the image. There are two extra optional variables for the image export; FPS and scale. When the user argument is not used it will pet any images uploaded with a message.
   * Arguments:
     * `<user:optional_user>, <fps:optional_integer> <scale:optional_number>`
     * `<user:optional_user>` - The user to pet their profile. This takes in a mention or the user's id. The user must be in this server!
     * `<fps:optional_integer>` - The frame rate to use for the exported gif, defaults to 15. This must be an integer from 0 - 60.
     * `<scale:optional_number>` - The scale to apply for the input image to use for the exported gif, defaults to 1.0. This must be a positive number.
