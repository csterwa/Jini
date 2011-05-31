/**
 * 
 */
package org.jini.maven.plugin.util;

import java.io.File;

import org.apache.maven.artifact.repository.ArtifactRepository;

/**
 * @author csterling
 *
 */
public class JiniHomeUtil {
	
	private static String jiniHome = null;

	public static synchronized String buildJiniHomePath(ArtifactRepository localRepository) {
		if (jiniHome == null) {
			String os = System.getProperty("os.name");
			
			if (os.toLowerCase().indexOf("windows") != -1) {
				jiniHome = "C:\\Java" + File.separator + "jini2_1";
			} else {
				jiniHome = localRepository.getBasedir() + File.separator + "org" + File.separator + "jini" + File.separator + "maven-jini-plugin" + File.separator + "resources" + File.separator + "jini2_1";
			}
		}
		
		return jiniHome;
	}
	
}
