package logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Logger{
	private static final DateTimeFormatter FORMATTER=DataTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static void log(String level,String message){
		String timestamp=localDateTime.now().format(FORMATTER);
		System.out.printf("[%s] [%s] %s%n",timestamp,level,message);
	}
	public static void info(String message){
		log("INFO",message);
	}
	public static void warn(String message){
		log("WARN",message);
	}
	public static void error(String message){
		log("ERROR",message);
	}
}
