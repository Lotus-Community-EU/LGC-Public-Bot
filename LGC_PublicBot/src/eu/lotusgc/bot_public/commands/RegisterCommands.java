package eu.lotusgc.bot_public.commands;

import eu.lotusgc.bot_public.main.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
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
				Commands.slash("ticket", "Need help? Create a ticket!")
		).queue();
			Main.logger.info("Registered commands on 'PublicGuildCommands' for " + guild.getName() + " guilds.");
		}
	}
}