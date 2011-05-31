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
public class JiniStartExamplesExtracter {
	
	public static synchronized String extractStartExamples(String jiniHome, ArtifactRepository localRepos) {
		// check if example directory exists in jiniHome, if so return
		if (new File(jiniHome + File.separator + "example").exists()) {
			return null;
		}
		
		// extract start examples into jiniHome
		Artifact jiniStartExamplesArtifact = new DefaultArtifact("com.sun.jini", "jini-start-examples", VersionRange.createFromVersion("2.1"), Artifact.SCOPE_SYSTEM, "zip", null, new DefaultArtifactHandler());
		String jiniStartExamplesLoc = localRepos.getBasedir() + File.separator + localRepos.pathOf(jiniStartExamplesArtifact) + ".zip";
		
		try {
			ZipFileUtil.unzipFileIntoDirectory(new ZipFile(jiniStartExamplesLoc), new File(jiniHome));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return jiniStartExamplesLoc;
	}

}
