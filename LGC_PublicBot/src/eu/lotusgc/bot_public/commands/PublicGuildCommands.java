package eu.lotusgc.bot_public.commands;

import java.util.ArrayList;
import java.util.List;

import eu.lotusgc.bot_public.main.Main;
import eu.lotusgc.bot_public.misc.SimpleMethods;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class PublicGuildCommands extends ListenerAdapter{
	
	public static void registerCommands(JDA jda) {
		int count = 0;
		for(Guild guild : jda.getGuilds()) {
			count++;
			guild.updateCommands().addCommands(
					Commands.slash("ping", "See the ping between bot and gateway.")
						.addOption(OptionType.BOOLEAN, "ephemeral", "Whether or not the message should be sent as an ephemeral message."),
					Commands.slash("help", "Sends you via DM the help page of the bot."),
					Commands.slash("whois", "View account infos like online status, join datum, etc.")
					    .addOption(OptionType.USER, "user", "The User you want the info about."),
					Commands.slash("guildinfo", "View guild related infos like roles, users and such.")
					).queue();
		}
		Main.logger.info("Registered commands on 'PublicGuildCommands' for " + count + " guilds.");
	}
	
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		User user = event.getUser();
		Guild guild = event.getGuild();
		if(event.getName().equals("ping")) {
			long timeOld = System.currentTimeMillis();
			event.deferReply().queue();
			event.getHook().sendMessage("Pong...").queue(rA -> {
				rA.editMessageFormat("Pong! ``%d ms``\nGatewayping: ``%d ms", System.currentTimeMillis() - timeOld, event.getJDA().getGatewayPing()).queue();
			});
		}else if(event.getName().equalsIgnoreCase("help")) {
			event.deferReply().queue();
			user.openPrivateChannel().queue(rA -> {
				rA.sendMessage("Hiya! There is no help page yet!").queue();
			});
		}else if(event.getName().equals("whois")) {
			event.deferReply().queue();
			Member member = event.getOption("user").getAsMember();
			List<String> list = new ArrayList<>();
			if(member != null) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(member.getColor());
				if(member.getUser().getDiscriminator().equalsIgnoreCase("0000")) {
					eb.setAuthor(member.getUser().getName(), member.getEffectiveAvatarUrl());
				}else {
					eb.setAuthor(member.getUser().getName() + "#" + member.getUser().getDiscriminator(), member.getEffectiveAvatarUrl());
				}
				eb.addField("Dates", "Joined Discord: " + SimpleMethods.retDate(member.getUser().getTimeCreated(), "dd.MM.yy - HH:mm") + "\nJoined " + guild.getName() + ": " + SimpleMethods.retDate(member.getTimeJoined(), "dd.mm.yy - HH:mm"), false);
				
				eb.setThumbnail(member.getEffectiveAvatarUrl());
				if(member.isOwner()) {
					list.add("Guildowner: yes");
				}else {
					list.add("Guildowner: no");
				}
				if(member.isTimedOut()) {
					list.add("Timed out: yes");
					list.add("Timed out until: " + SimpleMethods.retDate(member.getTimeOutEnd(), "dd.MM.yy - HH:mm:ss"));
				}else {
					list.add("Timed out: no");
				}
				if(member.isBoosting()) {
					list.add("Boosts the server: yes, since " + SimpleMethods.retDate(member.getTimeBoosted(), "dd.MM.yy"));
				}else {
					list.add("Boosts the server: no");
				}
				if(member.getOnlineStatus() == OnlineStatus.ONLINE) {
					list.add("Online status: ``online``");
				}else if(member.getOnlineStatus() == OnlineStatus.IDLE) {
					list.add("Online status: ``idle``");
				}else if(member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB) {
					list.add("Online status: ``do not disturb``");
				}else if(member.getOnlineStatus() == OnlineStatus.OFFLINE) {
					list.add("Online status: ``offline``");
				}
				StringBuilder sbI = new StringBuilder();
				for(String s : list) {
					sbI.append(s);
					sbI.append("\n");
				}
				eb.setDescription(sbI.toString());
				List<Role> roles = member.getRoles();
				StringBuilder sb = new StringBuilder();
				for(Role rs : roles) {
					sb.append(rs.getAsMention());
					sb.append(" ");
				}
				if(roles.size() <= 17) {
					eb.addField("Roles (" + roles.size() + ")", sb.toString(), true);
				}else {
					eb.addField("Roles (" + roles.size() + ")", "Too many roles to list.", true);
				}
				event.getHook().sendMessageEmbeds(eb.build()).queue();
			}
		}
	}
}