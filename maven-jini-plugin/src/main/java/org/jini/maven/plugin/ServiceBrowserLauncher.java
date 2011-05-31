/**
 * 
 */
package org.jini.maven.plugin;

import java.io.File;
import java.util.List;

/**
 * @author csterling
 * @goal browser
 * @requiresProject false
 */
public class ServiceBrowserLauncher extends AbstractLauncherMojo {

	/**
	 * 
	 */
	public String getServiceName() {
		return "browser";
	}

	/**
	 * 
	 */
	public void configureServiceParams(List argsList) {
		// nothing for now
	}

	/**
	 * 
	 */
	protected String configureServiceConfig() {
		return jiniHome + File.separator + "example" + File.separator + "common" + File.separator + protocol + File.separator + "config" + File.separator + "start-browser.config";
	}

}
