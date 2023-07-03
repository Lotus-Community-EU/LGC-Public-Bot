package eu.lotusgc.bot_public.main;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgc.bot_public.misc.MySQL;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
	
	public static String configFolderName = "LotusPublicBot";
	public static File configurationFolder = new File(configFolderName);
	public static String mainConfigName = "botconfig.yml";
	public static File mainConfig = new File(configFolderName + "/" + mainConfigName);
	private static File logFolder = new File(configFolderName + "/logs");
	
	public static final Logger logger = Logger.getLogger("LGC Public Bot");
	
	public static void main(String[] args) {
		YamlFile cfg = null;
		try {
			fileHandler();
			cfg = YamlFile.loadConfiguration(mainConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
		configLogger();
		if(cfg != null) {
			startBot(cfg);
			connectSQL(cfg);
		}else {
			System.out.println("Error! Shutting down bot.");
			System.exit(0);
		}
		enableShutdownHook();
	}
	
	private static void startBot(YamlFile cfg) {
		JDABuilder builder = JDABuilder.createDefault(cfg.getString("Bot.token"));
		builder.enableIntents(GatewayIntent.GUILD_MODERATION, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES);
		builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.FORUM_TAGS, CacheFlag.ONLINE_STATUS, CacheFlag.SCHEDULED_EVENTS, CacheFlag.VOICE_STATE);
		builder.build();
	}
	
	private static void fileHandler() throws IOException {
		if(!configurationFolder.exists()) {
			configurationFolder.mkdir();
		}
		if(!logFolder.exists()) {
			logFolder.mkdir();
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
		cfg.addDefault("MySQL.Host", "hostname");
		cfg.addDefault("MySQL.Database", "databaseName");
		cfg.addDefault("MySQL.Username", "username");
		cfg.addDefault("MySQL.Password", "pass123word456");
		cfg.addDefault("MySQL.Port", "3306");
		cfg.addDefault("MySQL.UseSQL", false);
		cfg.options().copyDefaults(true);
		cfg.save();
	}
	
	private static void connectSQL(YamlFile cfg) {
		if(cfg.getBoolean("MySQL.UseSQL")) {
			logger.info("Bot is using SQL!");
			try {
				MySQL.connect(cfg.getString("MySQL.Host"), cfg.getString("MySQL.Port"), cfg.getString("MySQL.Database"), cfg.getString("MySQL.Username"), cfg.getString("MySQL.Password"));
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}else {
			logger.info("Bot is not using SQL!");
		}
	}
	
	private static void configLogger() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		String date = sdf.format(new Date());
		try {
			FileHandler fileHandler = new FileHandler("log-" + date + ".log.txt");
			fileHandler.setFormatter(new SimpleFormatter());
			
			ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setLevel(Level.ALL);
			
			logger.addHandler(fileHandler);
			logger.addHandler(consoleHandler);
			
			logger.setLevel(Level.ALL);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void closeLogger() {
		for(Handler handler : logger.getHandlers()) {
			handler.close();
		}
	}
	
	private static void enableShutdownHook() {
		Thread printingHook = new Thread(() -> {
			MySQL.disconnect();
			logger.info("Bot is in shutdownprogress, byebye...");
			closeLogger();
		});
		Runtime.getRuntime().addShutdownHook(printingHook);
	}

}