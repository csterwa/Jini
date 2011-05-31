/**
 * 
 */
package org.jini.maven.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.jini.maven.plugin.util.FilePathUtil;
import org.jini.maven.plugin.util.JiniHomeUtil;
import org.jini.maven.plugin.util.JiniStartExamplesExtracter;
import org.jini.maven.plugin.util.JiniStarterKitExtracter;
import org.jini.maven.plugin.util.PrintLineThread;

/**
 * @author csterling
 */
public abstract class AbstractLauncherMojo extends AbstractMojo {

	protected static final String TRANSIENT_MODE = "transient";

	protected static final String ACTIVATABLE_MODE = "activatable";

	protected static final String NON_ACTIVATABLE_MODE = "nonactivatable";
	
	protected static final String JERI = "jeri";
	
	protected static final String JRMP = "jrmp";
	
	protected static final String JERI_SSL = "jsse";

	/**
	 * The security manager for the service.
	 * 
	 * @parameter expression="${jiniSecurityManager}"
	 */
	protected String securityManager;
	/**
	 * The security policy for the running service.
	 * 
	 * @parameter expression="${jiniSecurityPolicy}"
	 */
	protected String securityPolicy;
	/**
	 * The package which contains the protocol handlers for this service.
	 * 
	 * @parameter expression="${jiniProtocolHandlerPkgs}" default-value="net.jini.url"
	 */
	protected String protocolHandlerPackages;
	/**
	 * The security access control properties for each service.
	 * 
	 * @parameter expression="${jiniSecurityProps}"
	 */
	protected String securityProperties;
	/**
	 * Authentication configuration file for secure login.
	 * 
	 * @parameter expression="${jiniAuthLoginConfig}"
	 */
	protected String authLoginConfig;
	/**
	 * The trust store to derive security properties from.
	 * 
	 * @parameter expression="${jiniSSLTruststore}"
	 */
	protected String sslTrustStore;
	/**
	 * The log manager absolute implementation class for the service.
	 * 
	 * @parameter expression="${jiniLoggingManager}" default-value="com.sun.jini.logging.LogManager"
	 */
	protected String loggingManager;
	/**
	 * The logging configuration file for the log manager.
	 * 
	 * @parameter expression="${jiniLoggingConfig}"
	 */
	protected String loggingConfig;
	/**
	 * The service runtime configuration based on the net.jini.config package interfaces.
	 * 
	 * @parameter expression="${jiniServiceConfig}"
	 */
	private String serviceConfig;
	/**
	 * The log of the shared virtual machine in activatable mode.
	 * 
	 * @parameter expression="${jiniSharedVMLog}"
	 */
	protected String sharedVMLog;
	/**
	 * The host which this service will be launched on.
	 * 
	 * @parameter expression="${jiniServerHost}" default-value="localhost"
	 */
	protected String serverHost;
	/**
	 * The host to download codebase artifacts from.
	 * 
	 * @parameter expression="${jiniCodebaseHost}" default-value="localhost"
	 */
	protected String codebaseHost;
	/**
	 * The port on the codebase host to connect onto.  The port number '5464' spells out 'jini' on a traditional telephone keypad.
	 * 
	 * @parameter expression="${jiniCodebasePort}" default-value="5464"
	 */
	protected int codebasePort;
	/**
	 * The local Maven artifact repository.
	 * 
	 * @parameter expression="${localRepository}"
	 */
	protected ArtifactRepository localRepository;
	/**
	 * The Jini installation directory.  This is dynamically created when not set to:
	 * 
	 * <i><maven-local-repo>/org/jini/maven-jini-plugin/resources/jini2_1</i>
	 * 
	 * except on Windows in which it is <i>C:\Java\jini2_1</i> by default.  This is due to
	 * problems with spaces in the default Maven local repository path and executing the Java
	 * runtime using java.lang.Runtime.exec().
	 *  
	 * @parameter expression="${jiniHome}"
	 */
	protected String jiniHome;
	/**
	 * The directory to persist in process service data.
	 * 
	 * @parameter expression="${jiniPersistDir}"
	 */
	protected String persistenceDir;
	/**
	 * The service's group configuration file.
	 * 
	 * @parameter expression="${jiniGroupConfig}"
	 */
	private String groupConfig;
	/**
	 * Whether Java security should be in debug mode or not.
	 * 
	 * @parameter expression="${jiniSecurityDebug}"
	 */
	protected boolean securityDebug;
	/**
	 * Whether the Jini service should be in debug mode or not.
	 * 
	 * @parameter expression="${jiniServiceDebug}"
	 */
	protected boolean serviceDebug;
	/**
	 * Whether logging should be turned on.
	 * 
	 * @parameter expression="${jiniLoggingOn}"
	 */
	protected boolean loggingOn;
	/**
	 * This indicates the stateful mode for this service's runtime configuration.
	 * The following modes are supported: "transient", "activatable", "nonactivatable".
	 * 
	 * @parameter expression="${jiniMode}" default-value="transient"
	 */
	protected String mode;
	/**
	 * The protocol to use when marshalling objects to and from the service proxies.
	 * Currently, the supported protocols are "jrmp", "jeri", and "jsse" with "jeri"
	 * being the default.
	 * 
	 * @parameter expression="${jiniProtocol}" default-value="jeri"
	 */
	protected String protocol;
	
	/**
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Configuring the " + getServiceName() + " service");
		if (jiniHome == null) {
			jiniHome = JiniHomeUtil.buildJiniHomePath(localRepository);
		}
		
		getLog().info("Jini Home Directory: " + jiniHome);
		configureService();
		checkJiniStarterKitInstallation();

		Process javaProcess = null;
		BufferedReader out = null;
		BufferedReader err = null;
		Thread outThread = null;
		Thread errThread = null;
		
		try {
			javaProcess = createCommandLine().execute();

			out = new BufferedReader(new InputStreamReader(javaProcess.getInputStream()));
			err = new BufferedReader(new InputStreamReader(javaProcess.getErrorStream()));
			
			outThread = new Thread(new PrintLineThread(out, getLog()));
			outThread.start();
			errThread = new Thread(new PrintLineThread(err, getLog()));
			errThread.start();
		} catch (CommandLineException e) {
			e.printStackTrace();
			return;
		}
		
		waitForProcessShutdown(javaProcess, out, err, outThread, errThread);
	}

	/**
	 * @param javaProcess
	 * @param out
	 * @param err
	 * @param outThread
	 * @param errThread
	 */
	private void waitForProcessShutdown(Process javaProcess, BufferedReader out, BufferedReader err, Thread outThread, Thread errThread) {
		try {
			// wait until the process in interrupted by an exit call
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			getLog().info("Shutting down " + getServiceName() + "...");
			
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			if (err != null) {
				try {
					err.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			outThread.interrupt();
			errThread.interrupt();
			javaProcess.destroy();
			getLog().info(getServiceName() + " shutdown finished.");
		}
	}
	
	/**
	 *
	 */
	private void checkJiniStarterKitInstallation() {
		String jiniStarterKitLoc = JiniStarterKitExtracter.extractStarterKit(jiniHome, localRepository);
		String jiniStartExamplesLoc = JiniStartExamplesExtracter.extractStartExamples(jiniHome, localRepository);

		if (jiniStarterKitLoc != null) {
			getLog().info("Extracted contents of " + jiniStarterKitLoc + " into " + jiniHome);

			if (jiniStartExamplesLoc != null) {
				getLog().info("Extracted contents of " + jiniStartExamplesLoc + " into " + jiniHome);
			}
		} else {
			getLog().info("Jini starter kit ready for use at " + jiniHome);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private Commandline createCommandLine() {
		Commandline javaCmdLine = new Commandline();

		javaCmdLine.addArguments(createExecutableCommand());
		javaCmdLine.setWorkingDirectory(".");
		getLog().info("Starting the " + getServiceName() + " service in " + mode + " mode using the " + protocol + " protocol...");

		getLog().info(getServiceName() + " service started.");
		getLog().info("Command: " + javaCmdLine.toString());
		
		return javaCmdLine;
	}
	
	/**
	 * 
	 * @return
	 */
	private String[] createExecutableCommand() {
		List argsList = new ArrayList();
		
		argsList.add(JavaEnvUtils.getJreExecutable("java"));
		
		if (securityManager == null) {
			securityManager = "";
		}
		argsList.add("-Djava.security.manager=" + securityManager);
		argsList.add("-Djava.security.policy=" + FilePathUtil.escapeFilePath(securityPolicy));
		argsList.add("-Dconfig=" + FilePathUtil.escapeFilePath(configureServiceConfig()));
		argsList.add("-DserverHost=" + serverHost);
		argsList.add("-DcodebaseHost=" + codebaseHost);
		argsList.add("-DcodebasePort=" + codebasePort);
		argsList.add("-DjiniHome=" + FilePathUtil.escapeFilePath(jiniHome));
		argsList.add("-DappHome=" + FilePathUtil.escapeFilePath(jiniHome));
		argsList.add("-DresourceHome=" + FilePathUtil.escapeFilePath(jiniHome + File.separator + "lib"));
		argsList.add("-DgroupConfig=" + FilePathUtil.escapeFilePath(groupConfig));
		
		if (isPersistent()) {
			configurePersistence(argsList);
		}
		
		if (isSecure()) {
			configureSecurity(argsList);
		}
		
		if (loggingOn) {
			configureLogging(argsList);
		}
		
		configureServiceParams(argsList);
		configureDebug(argsList);
		
		argsList.add("-jar");
		argsList.add(FilePathUtil.escapeFilePath(jiniHome + File.separator + "lib" + File.separator + "start.jar"));
		argsList.add(FilePathUtil.escapeFilePath(configureServiceConfig()));
		
		String[] args = new String[argsList.size()];
		args = (String[]) argsList.toArray(args);
		
		return args;
	}

	/**
	 *
	 */
	private void configureService() {
		if (!TRANSIENT_MODE.equals(mode) && !ACTIVATABLE_MODE.equals(mode) && !NON_ACTIVATABLE_MODE.equals(mode)) {
			throw new IllegalArgumentException("Service mode is not supported: " + mode + ".  Please update mode to be either " + TRANSIENT_MODE + ", " + ACTIVATABLE_MODE + ", or " + NON_ACTIVATABLE_MODE + ".");
		}
		
		if (!JERI.equals(protocol) && !JRMP.equals(protocol) && !JERI_SSL.equals(protocol)) {
			throw new IllegalArgumentException("Service protocol is not supported: " + protocol + ".  Please update mode to be either " + JERI + ", " + JRMP + ", or " + JERI_SSL + ".");
		}
		
		if (securityPolicy == null) {
			securityPolicy = jiniHome + File.separator + "policy.all";
		}
		
		if (groupConfig == null) {
			groupConfig = jiniHome + File.separator + "example" + File.separator + "phoenix" + File.separator + protocol + File.separator + "config" + File.separator + "phoenix-group.config";
		}
	}

	/**
	 * 
	 */
	protected String configureServiceConfig() {
		if (serviceConfig == null) {
			serviceConfig = jiniHome + File.separator + "example" + File.separator + "common" + File.separator + protocol + File.separator + "config" + File.separator + "start-one-" + mode + ".config";
		}
		
		return serviceConfig;
	}

	/**
	 * 
	 * @param argsList
	 */
	private void configurePersistence(List argsList) {
		if (persistenceDir == null) {
			persistenceDir = jiniHome + File.separator + "example" + File.separator + "phoenix";
		}
		argsList.add("-DpersistDir=" + FilePathUtil.escapeFilePath(persistenceDir));
		
		if (ACTIVATABLE_MODE.equals(mode)) {
			if (sharedVMLog == null) {
				sharedVMLog = System.getProperty("java.io.tmpdir") + File.separator + getServiceName() + "-sharedvm";
			}
			argsList.add("-DsharedVMLog=" + FilePathUtil.escapeFilePath(sharedVMLog));
		}
	}
	
	/**
	 * 
	 * @param argsList
	 */
	private void configureSecurity(List argsList) {
		if (protocolHandlerPackages == null) {
			protocolHandlerPackages = "net.jini.url";
		}
		
		if (securityProperties == null) {
			securityProperties = jiniHome + File.separator + "example" + File.separator + "common" + File.separator + "jsse" + File.separator + "props" + File.separator + "dynamic-policy.properties";
		}
		
		if (authLoginConfig == null) {
			authLoginConfig = jiniHome + File.separator + "example" + File.separator + "common" + File.separator + "jsse" + File.separator + "login" + File.separator + "setup.login";
		}
		
		if (sslTrustStore == null) {
			sslTrustStore = jiniHome + File.separator + "example" + File.separator + "common" + File.separator + "jsse" + File.separator + "truststore" + File.separator + "jini-ca.truststore";
		}
		
		argsList.add("-Djava.protocol.handler.pkgs=" + protocolHandlerPackages);
		argsList.add("-Djava.security.auth.login.config=" + FilePathUtil.escapeFilePath(authLoginConfig));
		argsList.add("-Djava.security.properties=" + FilePathUtil.escapeFilePath(securityProperties));
		argsList.add("-Djavax.net.ssl.trustStore=" + FilePathUtil.escapeFilePath(sslTrustStore));
	}
	
	/**
	 * @param argsList
	 */
	private void configureDebug(List argsList) {
		if (securityDebug) {
			argsList.add("-Djava.security.debug=true");
		}

		if (serviceDebug) {
			argsList.add("-Ddebug=true");
		}
	}

	/**
	 * @param argsList
	 */
	private void configureLogging(List argsList) {
		argsList.add("-Djava.util.logging.manager=" + loggingManager);
		
		if (loggingConfig == null) {
			loggingConfig = jiniHome + File.separator + "example" + File.separator + "common" + File.separator + "logging" + File.separator + "jini.logging";
		}
		argsList.add("-Djava.util.logging.config.file=" + FilePathUtil.escapeFilePath(loggingConfig));
	}

	/**
	 * 
	 * @return
	 */
	public abstract String getServiceName();
	
	/**
	 * 
	 * @param argsList
	 */
	public abstract void configureServiceParams(List argsList);
	
	/**
	 * 
	 * @return
	 */
	public boolean isPersistent() {
		return (!TRANSIENT_MODE.equals(mode));
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isSecure() {
		return (JERI_SSL.equals(protocol));
	}
	
}
