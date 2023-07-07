package eu.lotusgc.bot_public.misc;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleMethods {
	
	public static String retDate(OffsetDateTime odt, String pattern) {
		return odt.format(DateTimeFormatter.ofPattern(pattern));
	}

}
