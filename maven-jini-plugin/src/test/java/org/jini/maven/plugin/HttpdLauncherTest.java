package org.jini.maven.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.cli.ConsoleDownloadMonitor;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderConsoleLogger;
import org.apache.maven.embedder.PlexusLoggerAdapter;
import org.apache.maven.monitor.event.DefaultEventMonitor;
import org.apache.maven.monitor.event.EventMonitor;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusTestCase;

public class HttpdLauncherTest extends PlexusTestCase {
	
	protected MavenEmbedder maven = null;

	protected void setUp() throws Exception {
		super.setUp();
		
		this.maven = new MavenEmbedder();
        this.maven.setClassLoader( Thread.currentThread().getContextClassLoader() );
        this.maven.setLogger( new MavenEmbedderConsoleLogger() );
        this.maven.setLocalRepositoryDirectory(new File("."));
        this.maven.start();
    }

	protected void tearDown() throws Exception {
		this.maven.stop();
		
		super.tearDown();
	}
	
	public void testStartUp() throws Exception {
		File basedir = getTestFile( "src/test/projects/project1" );

        MavenProject project = this.maven.readProjectWithDependencies( new File( basedir, "pom.xml" ) );

        EventMonitor eventMonitor = new DefaultEventMonitor(new PlexusLoggerAdapter( new MavenEmbedderConsoleLogger() ));
        
        this.maven.execute(project, Arrays.asList( new String[] {"jini:maven-jini-plugin:httpd" } ), eventMonitor, new ConsoleDownloadMonitor(), new Properties(), basedir);
        this.maven.stop();
    }

}
