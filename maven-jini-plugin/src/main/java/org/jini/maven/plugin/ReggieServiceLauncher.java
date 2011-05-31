/**
 * 
 */
package org.jini.maven.plugin;


/**
 * @author csterling
 * @goal reggie
 * @requiresProject false
 */
public class ReggieServiceLauncher extends AbstractServiceLauncherMojo {

	/**
	 * 
	 */
	public String getServiceName() {
		return "reggie";
	}

	/**
	 * 
	 */
	public String getInterfaceName() {
		return "Registrar";
	}

	/**
	 * 
	 */
	public String getImplName() {
		if (TRANSIENT_MODE.equals(mode)) {
			return "TransientRegistrarImpl";
		} else if (ACTIVATABLE_MODE.equals(mode) || NON_ACTIVATABLE_MODE.equals(mode)) {
			return "PersistentRegistrarImpl";
		}
		
		return null;
	}
	
}
