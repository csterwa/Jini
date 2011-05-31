/**
 * 
 */
package org.jini.maven.plugin.util;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * @author csterling
 *
 */
public class JiniStarterKitExtracter {
	
	public static synchronized String extractStarterKit(String jiniHome, ArtifactRepository localRepos) {
		// check for starter kit already extracted by start.jar existing
		if (new File(jiniHome + File.separator + "lib" + File.separator + "start.jar").exists()) {
			// Jini starter kit has already been extracted in this location
			return null;
		}
		
		// unzip archive into jiniHome location
		File jiniHomeDir = new File(jiniHome);
		if (!jiniHomeDir.getParentFile().exists()) {
			jiniHomeDir.getParentFile().mkdirs();
		}
		
		Artifact jiniStarterKitArtifact = new DefaultArtifact("com.sun.jini", "jini-starterkit", VersionRange.createFromVersion("2.1"), Artifact.SCOPE_SYSTEM, "zip", null, new DefaultArtifactHandler());
		String jiniStarterKitLoc = localRepos.getBasedir() + File.separator + localRepos.pathOf(jiniStarterKitArtifact) + ".zip";

		unzipArchive(jiniHome, jiniStarterKitLoc);
		
		return jiniStarterKitLoc;
	}

	/**
	 * @param jiniHome
	 * @param jiniStarterKitLoc
	 */
	private static void unzipArchive(String jiniHome, String jiniStarterKitLoc) {
		ZipFile zipFile = null;
		
		try {
			zipFile = new ZipFile(new File(jiniStarterKitLoc));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not open file: " + jiniStarterKitLoc);
			return;
		}
		
		File jiniHomeParentDir = new File(jiniHome).getParentFile();

		ZipFileUtil.unzipFileIntoDirectory(zipFile, jiniHomeParentDir);
	}
	
}
