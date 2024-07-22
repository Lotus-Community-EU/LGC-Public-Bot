package eu.lotusgc.bot_public.commands;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.lotusgc.bot_public.main.LotusController;
import eu.lotusgc.bot_public.misc.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class Serverfinder extends ListenerAdapter{
	
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getName().equals("serverinfo")) {
			StringSelectMenu.Builder builder = StringSelectMenu.create("servers");
			for(String string : LotusController.serverList) {
				builder.addOption(string, string);
			}
			StringSelectMenu menu = builder.build();
			event.reply("Choose the server")
			.addActionRow(menu).queue();
		}
	}
	
	@Override
	public void onStringSelectInteraction(StringSelectInteractionEvent event) {
		if(event.getComponentId().equals("servers")) {
			//event.editMessage("You have chosen " + event.getValues().get(0)).setReplace(true).queue();
			event.editMessageEmbeds(getServerInfo(event.getValues().get(0), event.getMember()).build()).setReplace(true).queue();
		}
	}
	
	EmbedBuilder getServerInfo(String input, Member member) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor("Requested by: " + member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_serverstats WHERE servername = ?");
			ps.setString(1, input);
			ResultSet rs = ps.executeQuery();
			rs.next();
			eb.setTitle("Selected Server: " + input + " / " + rs.getString("serverid"));
			eb.addField("Playerinfo", rs.getInt("currentPlayers") + " / " + rs.getInt("maxPlayers") + "\nOnline Staffs: " + rs.getInt("currentStaffs") + "\nCapacity: " + rs.getDouble("playerCapacity") + "%", true);
			eb.addField("Configuration", "Is Online: " + translateBoolean(rs.getBoolean("isOnline")) + "\nIs Monitored: " + translateBoolean(rs.getBoolean("isMonitored")) + "\nIs Locked: " + translateBoolean(rs.getBoolean("isLocked")) + "\nIs Modded: " + translateBoolean(rs.getBoolean("isHybrid")) + "\nHas Onlinemap: " + translateBoolean(rs.getBoolean("hasDynmap")) + "\nHas Jobs: " + translateBoolean(rs.getBoolean("hasJobs")) + "\nMax. Homes: " + rs.getInt("maxHomes") + "\nVersion: " + rs.getString("version"), false);
			eb.setColor(capacityCalc(rs.getDouble("playerCapacity")));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return eb;
	}
	
	ResultSet getRawData(String input) {
		ResultSet rs = null;
		
		return rs;
	}
	
	String translateBoolean(boolean input) {
		if(input) {
			return "yes";
		}else {
			return "no";
		}
	}
	
	Color capacityCalc(double input) {
		if(input >= 0.0 && input <= 24.9) {
			return Color.GREEN;
		}else if(input >= 25.0 && input <= 49.9) {
			return Color.YELLOW;
		}else if(input >= 50.0 && input <= 74.9) {
			return Color.ORANGE;
		}else if(input >= 75.0 && input <= 89.9) {
			return Color.RED;
		}else {
			return Color.BLACK;
		}
	}
}