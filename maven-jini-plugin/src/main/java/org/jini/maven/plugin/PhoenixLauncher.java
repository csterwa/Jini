/**
 * 
 */
package org.jini.maven.plugin;

import java.io.File;
import java.util.List;

import org.jini.maven.plugin.util.FilePathUtil;

/**
 * @author csterling
 * @goal phoenix
 * @requiresProject false
 */
public class PhoenixLauncher extends AbstractLauncherMojo {

	/**
	 * 
	 */
	public String getServiceName() {
		return "phoenix";
	}

	/**
	 * 
	 */
	public void configureServiceParams(List argsList) {
		argsList.add("-DpersistDir=" + jiniHome + File.separator + "example" + File.separator + "phoenix");
		argsList.add("-Djava.security.properties=" + FilePathUtil.escapeFilePath(jiniHome + File.separator + "example" + File.separator + "common" + File.separator + "jsse" + File.separator + "props" + File.separator + "dynamic-policy.properties"));
	}

	/**
	 * 
	 */
	protected String configureServiceConfig() {
		return jiniHome + File.separator + "example" + File.separator + "common" + File.separator + protocol + File.separator + "config" + File.separator + "start-phoenix.config";
	}

}
