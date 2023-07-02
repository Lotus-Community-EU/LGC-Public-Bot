package eu.lotusgc.bot_public.main;

import java.io.File;
import java.io.IOException;

import org.simpleyaml.configuration.file.YamlFile;

import net.dv8tion.jda.api.JDABuilder;

public class Main {
	
	public static String configFolderName = "LotusPublicBot";
	public static File configurationFolder = new File(configFolderName);
	public static String mainConfigName = "botconfig.yml";
	public static File mainConfig = new File(configFolderName + "/" + mainConfigName);
	
	public static void main(String[] args) {
		try {
			fileHandler();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void startBot(YamlFile cfg) {
		JDABuilder builder = JDABuilder.createDefault(cfg.getString("Bot.token"));
	}
	
	private static void fileHandler() throws IOException {
		if(!configurationFolder.exists()) {
			configurationFolder.mkdir();
		}
		if(!mainConfig.exists()) {
			mainConfig.createNewFile();
		}
		
		YamlFile cfg = YamlFile.loadConfiguration(mainConfig);
		//general bot configuration
		cfg.set("Bot.onlineTime", System.currentTimeMillis());
		cfg.addDefault("Bot.token", "YourBotTokenGoesThere");
		cfg.addDefault("Bot.Activity.Onlinestatus", "ONLINE");
		cfg.addDefault("Bot.Activity.StatusKey", "WATCHING");
		cfg.addDefault("Bot.Activity.StatusValue", "over the users.");
		cfg.addDefault("Bot.Activity.StreamURL", "https://twitch.tv/");
		//mysql logon data
		cfg.addDefault("MySQL.Database", "databaseName");
		cfg.addDefault("MySQL.Username", "username");
		cfg.addDefault("MySQL.Password", "pass123word456");
		cfg.addDefault("MySQL.Port", "3306");
		cfg.addDefault("MySQL.UseSQL", false);
		cfg.options().copyDefaults(true);
		cfg.save();
	}

}