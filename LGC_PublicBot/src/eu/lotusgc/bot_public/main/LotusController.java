package eu.lotusgc.bot_public.main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.lotusgc.bot_public.misc.MySQL;

public class LotusController {
	
	static HashMap<String, String> nameMap = new HashMap<>();
	static HashMap<Long, String> idMap = new HashMap<>();
	static List<String> uuidList = new ArrayList<>();
	public static List<String> serverList = new ArrayList<>();
	
	public static void loadData() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT servername FROM mc_serverstats");
			ResultSet rs = ps.executeQuery();
			int data = 0;
			while(rs.next()) {
				data++;
				serverList.add(rs.getString("servername"));
			}
			StringBuilder sb = new StringBuilder();
			serverList.forEach(l -> {
				sb.append(l);
				sb.append(", ");
			});
			Main.logger.info("Loaded data from ``mc_serverstats`` - got " + data + " entries: " + sb.toString().substring(0, (sb.toString().length() - 2)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT mcuuid,lgcid,name FROM mc_users");
			ResultSet rs = ps.executeQuery();
			int data = 0;
			while(rs.next()) {
				data++;
				uuidList.add(rs.getString("mcuuid"));
				idMap.put((long) rs.getInt("lgcid"), rs.getString("mcuuid"));
				nameMap.put(rs.getString("name"), rs.getString("mcuuid"));
			}
			Main.logger.info("Loaded data from ``mc_users`` - got " + data + " entries.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
