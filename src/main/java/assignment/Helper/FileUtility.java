package assignment.Helper;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to read the SQL file and convert it into String
 * 
 * @author ARNAV
 *
 */
public class FileUtility {
	private static final Logger logger = LoggerFactory.getLogger(FileUtility.class);

	/**
	 * This method is used to raed from a SQL file and convert it into String
	 * 
	 * @param fname
	 * @return SQL converted to string
	 */
	public static String parseSqlToString(String fname) {
		logger.info("Inside the parseSqlToString method from class", FileUtility.class.getSimpleName());
		File file = new File(fname);
		logger.info("Inside the parseSqlToString method trying to look for File in path " + fname);
		String reqString = "";
		if (file.exists()) {
			logger.info("Inside the parseSqlToString method file found " + fname);
			try {
				reqString = FileUtils.readFileToString(file, "utf-8");
			} catch (IOException ex) {

				logger.error("Inside the parseSqlToString unable to Read File");
				logger.error(ex.getMessage());
			}
			logger.info("Inside the parseSqlToString method file sucessfully converted to string " + fname);
			logger.debug("Inside the parseSqlToString method String generated from sql is " + reqString);
		} else {
			logger.error("Inside the parseSqlToString method file not found " + fname);
		}
		return reqString;
	}
}
