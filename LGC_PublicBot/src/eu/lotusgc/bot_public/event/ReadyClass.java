package eu.lotusgc.bot_public.event;

import eu.lotusgc.bot_public.commands.PublicGuildCommands;
import eu.lotusgc.bot_public.main.Main;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyClass extends ListenerAdapter{
	
	public void onReady(ReadyEvent event) {
		Main.logger.info("Bot fired ReadyEvent.");
		PublicGuildCommands.registerCommands(event.getJDA());
	}

}
