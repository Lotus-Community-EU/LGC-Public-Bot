package eu.lotusgc.bot_public.commands;

import eu.lotusgc.bot_public.main.LotusController;
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
			event.editMessage("You have chosen " + event.getValues().get(0)).setReplace(true).queue();
		}
	}
}