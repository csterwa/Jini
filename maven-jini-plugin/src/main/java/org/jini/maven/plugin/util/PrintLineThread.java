/**
 * 
 */
package org.jini.maven.plugin.util;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.maven.plugin.logging.Log;

/**
 * @author csterling
 */
public class PrintLineThread implements Runnable {

	private BufferedReader reader = null;
	
	private Log logger = null;
	
	/**
	 * 
	 * @param br
	 * @param logger
	 */
	public PrintLineThread(BufferedReader br, Log logger) {
		this.reader = br;
		this.logger = logger;
	}

	/**
	 * 
	 */
	public void run() {
		String line = null;
		
		try {
			while ((line = this.reader.readLine()) != null) {
				this.logger.info(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}