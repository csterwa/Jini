/**
 * 
 */
package org.jini.maven.plugin;

import java.util.List;


/**
 * @author csterling
 *
 */
public abstract class AbstractServiceLauncherMojo extends AbstractLauncherMojo {

	/**
	 * @param argsList
	 */
	public void configureServiceParams(List argsList) {
		argsList.add("-DserviceName=" + getServiceName());
		argsList.add("-DinterfaceName=" + getInterfaceName());
		argsList.add("-DimplName=" + getImplName());
	}
	
	public abstract String getInterfaceName();
	
	public abstract String getImplName();
	
}
