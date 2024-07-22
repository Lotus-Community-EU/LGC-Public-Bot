package eu.lotusgc.bot_public.event;

import java.util.Timer;

import eu.lotusgc.bot_public.commands.RegisterCommands;
import eu.lotusgc.bot_public.main.Main;
import eu.lotusgc.bot_public.misc.InfoUpdater;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyClass extends ListenerAdapter{
	
	public void onReady(ReadyEvent event) {
		Main.logger.info("Bot fired ReadyEvent.");
		RegisterCommands.registerCommands(event.getJDA());
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new InfoUpdater(event.getJDA()), 1000, 1000*60);
		InfoUpdater.setOnlineStatus(true);
	}

}
