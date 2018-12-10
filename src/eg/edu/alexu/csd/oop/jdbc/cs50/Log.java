package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
	private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private FileHandler fh;
	private static Log SingleLog = new Log();

	private Log() {
		try {
			File file = new File("log.txt");
			if (file.exists()) {
				file.delete();
			}
			logger.setUseParentHandlers(false);
			file.createNewFile();
			fh = new FileHandler("log.txt", true);
			logger.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Logger getLoggeer() {
		return SingleLog.logger;
	}
}
