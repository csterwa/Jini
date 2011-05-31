/**
 * 
 */
package org.jini.maven.plugin;

import java.io.File;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jini.maven.plugin.util.JiniHomeUtil;

import com.sun.jini.tool.ClassServer;

/**
 * 
 * 
 * @author Chris Sterling
 * @goal httpd
 * @requiresProject false
 */
public class HttpdLauncher extends AbstractMojo {
	
	/**
	 * The port to run the HTTP daemon on.
	 * @parameter expression="${codebasePort}" default-value="5464"
	 */
	private int port;
	/**
	 * The base directory for delivering artifacts from.
	 * @parameter expression="${codebaseDir}"
	 */
	private String baseDirectory;
	/**
	 * @parameter expression="${jiniHome}"
	 */
	private String jiniHome;
	/**
	 * The local Maven artifact repository.
	 * 
	 * @parameter expression="${localRepository}"
	 */
	protected ArtifactRepository localRepository;

	/**
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Starting HTTP daemon on port " + port);
		if (jiniHome == null) {
			jiniHome = JiniHomeUtil.buildJiniHomePath(localRepository);
		}
		
		if (baseDirectory == null) {
			baseDirectory = jiniHome + File.separator + "lib-dl";
		}
		
		getLog().info("HTTP daemon base directory: " + baseDirectory);
		String[] args = new String[] { "-port", String.valueOf(port), "-dir", baseDirectory, "-verbose" };
		ClassServer.main(args);
		
		// wait indefinitely until the program is killed from outside forces
		getLog().info("HTTP daemon has started.");
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			// ignore
		}
	}

}
