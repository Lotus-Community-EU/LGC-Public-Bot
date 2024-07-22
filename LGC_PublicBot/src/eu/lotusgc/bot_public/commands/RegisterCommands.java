package eu.lotusgc.bot_public.commands;

import eu.lotusgc.bot_public.main.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class RegisterCommands {
	
	public static void registerCommands(JDA jda) {
		for(Guild guild : jda.getGuilds()) {
			guild.updateCommands().addCommands(
				Commands.slash("ping", "See the ping between bot and gateway.")
					.addOption(OptionType.BOOLEAN, "ephemeral", "Whether or not the message should be sent as an ephemeral message."),
				Commands.slash("help", "Sends you via DM the help page of the bot."),
				Commands.slash("whois", "View account infos like online status, join datum, etc.")
				    .addOption(OptionType.USER, "user", "The User you want the info about."),
				Commands.slash("guildinfo", "View guild related infos like roles, users and such."),
				Commands.slash("user", "See the user from our Minecraft Playerdatabase."),
				Commands.slash("serverinfo", "View detailed Informations about a game server."),
				Commands.slash("ticket", "Need help? Create a ticket!"),
				Commands.slash("status", "Sets the online status [Only useable by bot owner]"),
				Commands.slash("activity", "Sets the activity [Only useable by bot owner]"),
				
				//Private voice 
				Commands.slash("adminvoice-category", "Administration for 'voice-channel'.")
				.addOption(OptionType.CHANNEL, "category", "The category to set as default", true)
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL)),
				Commands.slash("adminvoice-settings", "Setup the private voices.")
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL)),
				Commands.slash("voice-create", "Create a voice channel")
				.addOption(OptionType.BOOLEAN, "private", "Whether the channel should be open or not. (true=private, false=public)", true)
				.addOption(OptionType.INTEGER, "slots", "How many slots should the voice channel have?"),
				Commands.slash("voice-delete", "Deletes the voice channel"),
				Commands.slash("voice-permit", "Adds a person to the channel permissions (just for private channels!)")
				.addOption(OptionType.USER, "user", "The user to add", true),
				Commands.slash("voice-revoke", "Removes a person from the channel permissions (just for private channels!)")
				.addOption(OptionType.USER, "user", "The user to remove", true)
				.addOption(OptionType.BOOLEAN, "remove", "Whether to forcefully remove the user from the voice or not.")
		).queue();
			Main.logger.info("Registered commands on 'PublicGuildCommands' for " + guild.getName() + " guilds.");
		}
	}
}