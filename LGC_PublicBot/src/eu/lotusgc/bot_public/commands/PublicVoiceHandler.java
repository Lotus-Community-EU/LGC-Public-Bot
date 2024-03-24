//Created by Christopher at 23.03.2024
package eu.lotusgc.bot_public.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.lotusgc.bot_public.misc.MySQL;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PublicVoiceHandler extends ListenerAdapter{
	
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		if(event.getName().equals("adminvoice-category")) {
			if(event.getOption("category") != null) {
				if(event.getOption("category").getAsChannel().getType() == ChannelType.CATEGORY) {
					Category category = event.getOption("category").getAsChannel().asCategory();
					String result = checkIfGuildHasVoiceSetup(guild);
					if(result.equals("Errored")) {
						event.deferReply(true).addContent("Seems like something errored!").queue();
					}else if(result.equals("none")) {
						event.deferReply(true).addContent("Guild has not set a channel yet!").queue();
						insertVoiceSetup(guild, category);
						event.getHook().sendMessage("The voice category has been set to " + category.getName()).queue();
					}
				}else {
					event.deferReply(true).addContent("Only a category can be set!").queue();
				}
			}
		}else if(event.getName().equals("voice-create")) {
			boolean bool = false;
			int slots = 8;
			if(event.getOption("private") != null) {
				bool = event.getOption("private").getAsBoolean();
			}
			if(event.getOption("slots") != null) {
				slots = event.getOption("slots").getAsInt();
			}else {
				slots = getDefaultSlots(guild);
			}
			final Integer slots2 = slots;
			final boolean bool2 = bool;
			if(memberHasVoiceAlready(guild, member)) {
				event.deferReply(true).addContent("You already have an open voice channel!").queue();
			}else {
				Category category = getCategory(guild);
				if(category == null) {
					event.deferReply(true).addContent("Couldn't create voice channel! No category has been set yet!").queue();
				}else {
					guild.createVoiceChannel(member.getEffectiveName() + "'s Channel", category).queue(rA -> {
						event.deferReply(true).addContent("Created the channel successfully: " + rA.getAsMention() + " with " + slots2 + " Slots.").queue();
						rA.getManager().setUserLimit(slots2).queue();
						rA.getManager().setBitrate(128000).queue();
						if(bool2) {
							rA.upsertPermissionOverride(getDefaultRole(guild)).deny(Permission.VOICE_CONNECT).queue();
						}else {
							rA.upsertPermissionOverride(getDefaultRole(guild)).setAllowed(Permission.VOICE_CONNECT).queue();
						}
						rA.upsertPermissionOverride(member).setAllowed(Permission.VIEW_CHANNEL ,Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_START_ACTIVITIES, Permission.VOICE_USE_SOUNDBOARD, Permission.VOICE_USE_EXTERNAL_SOUNDS).queue();
						insertVoiceChannel(guild, member, category, rA.getIdLong(), bool2);
					});
				}
			}
		}else if(event.getName().equals("voice-delete")) {
			if(memberHasVoiceAlready(guild, member)) {
				event.deferReply(true).addContent("The channel has been deleted.").queue();
				deleteVoiceChannel(guild, member);
			}else {
				event.deferReply(true).addContent("You don't have a channel!").queue();
			}
		}else if(event.getName().equals("voice-permit")) {
			if(event.getOption("user") != null) {
				User user = event.getOption("user").getAsUser();
				if(guild.isMember(user)) {
					Member target = guild.getMember(user);
					if(target.getIdLong() == member.getIdLong()) {
						event.deferReply(true).addContent("You are already added to your own channel!").queue();
					}else {
						if(memberHasVoiceAlready(guild, member)) {
							VoiceChannel channel = getVoiceChannel(guild, member);
							channel.upsertPermissionOverride(target).setAllowed(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_START_ACTIVITIES, Permission.VOICE_USE_SOUNDBOARD, Permission.VOICE_USE_EXTERNAL_SOUNDS).queue();
							event.deferReply(true).addContent(target.getAsMention() + " has been added to your voice channel permissions!").queue();
						}else {
							event.deferReply(true).addContent("You don't have a channel!").queue();
						}
					}
				}else {
					event.deferReply(true).addContent("Please add only people who are in this guild.").queue();
				}
			}
		}else if(event.getName().equals("voice-revoke")) {
			Member target = null;
			boolean forceRemove = false;
			if(event.getOption("user") != null) {
				User user = event.getOption("user").getAsUser();
				if(guild.isMember(user)) {
					target = guild.getMember(user);
				}
			}
			if(event.getOption("remove") != null) {
				forceRemove = event.getOption("remove").getAsBoolean();
			}
			if(target.getIdLong() == member.getIdLong()) {
				event.deferReply(true).addContent("You can't remove yourself from your own channel!").queue();
			}else {
				if(memberHasVoiceAlready(guild, member)) {
					VoiceChannel channel = getVoiceChannel(guild, member);
					channel.upsertPermissionOverride(target).setDenied(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_START_ACTIVITIES, Permission.VOICE_USE_SOUNDBOARD, Permission.VOICE_USE_EXTERNAL_SOUNDS).queue();
					if(forceRemove) {
						guild.kickVoiceMember(target).queue();
						event.deferReply(true).addContent(target.getAsMention() + " has been removed from your channel permission and has been kicked out of the channel.").queue();
					}else {
						event.deferReply(true).addContent(target.getAsMention() + " has been removed from your channel permission.").queue();
					}
				}else {
					event.deferReply(true).addContent("You don't have a channel!").queue();
				}
			}
		}
	}
	
	boolean memberHasVoiceAlready(Guild guild, Member member) {
		boolean bool = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_p_privateVoices WHERE guildId = ? AND channelOwner = ?");
			ps.setLong(1, guild.getIdLong());
			ps.setLong(2, member.getIdLong());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				bool = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bool;
	}
	
	void insertVoiceChannel(Guild guild, Member member, Category category, long channelId, boolean publicityStatus) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO bot_p_privateVoices(guildId, categoryId, channelId, channelOwner, isPublic) VALUES (?,?,?,?,?)");
			ps.setLong(1, guild.getIdLong());
			ps.setLong(2, category.getIdLong());
			ps.setLong(3, channelId);
			ps.setLong(4, member.getIdLong());
			ps.setBoolean(5, publicityStatus);;
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	Role getDefaultRole(Guild guild) {
		Role role = null;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT defaultRole FROM bot_p_privateVoiceSettings WHERE guildId = ?");
			ps.setLong(1, guild.getIdLong());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				role = guild.getRoleById(rs.getLong("defaultRole"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return role;
	}
	
	void deleteVoiceChannel(Guild guild, Member member) {
		getVoiceChannel(guild, member).delete().queue();
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM bot_p_privateVoices WHERE guildId = ? AND channelOwner = ?");
			ps.setLong(1, guild.getIdLong());
			ps.setLong(2, member.getIdLong());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	VoiceChannel getVoiceChannel(Guild guild, Member member) {
		long channelId = 0;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT channelId FROM bot_p_privateVoices WHERE guildId = ? AND channelOwner = ?");
			ps.setLong(1, guild.getIdLong());
			ps.setLong(2, member.getIdLong());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				channelId = rs.getLong("channelId");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return guild.getVoiceChannelById(channelId);
	}
	
	String checkIfGuildHasVoiceSetup(Guild guild) {
		String name = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT categoryId FROM bot_p_privateVoiceSettings WHERE guildId = ?");
			ps.setLong(1, guild.getIdLong());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				long id = rs.getLong("categoryId");
				name = guild.getCategoryById(id).getName();
			}else {
				name = "none";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			name = "Errored.";
		}
		return name;
	}
	
	Category getCategory(Guild guild) {
		Category category = null;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT categoryId FROM bot_p_privateVoiceSettings WHERE guildId = ?");
			ps.setLong(1, guild.getIdLong());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				category = guild.getCategoryById(rs.getLong("categoryId"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return category;
	}
	
	void insertVoiceSetup(Guild guild, Category category) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO bot_p_privateVoiceSettings(guildId, categoryId) VALUES (?,?)");
			ps.setLong(1, guild.getIdLong());
			ps.setLong(2, category.getIdLong());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	int getDefaultSlots(Guild guild) {
		int slots = 8;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT defaultMaxSlots FROM bot_p_privateVoiceSettings WHERE guildId = ?");
			ps.setLong(1, guild.getIdLong());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				slots = rs.getInt("defaultMaxSlots");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return slots;
	}
	
}