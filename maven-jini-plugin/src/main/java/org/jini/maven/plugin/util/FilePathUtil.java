/**
 * 
 */
package org.jini.maven.plugin.util;


/**
 * @author csterling
 *
 */
public class FilePathUtil {
	
	public static synchronized String escapeFilePath(String str) {
//		str = str.replace(" ", "%20");
		str = str.replace("\\", "/");
		
		return "\"" + str + "\"";
	}
	
}
