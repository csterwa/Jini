/**
 * 
 */
package org.jini.maven.plugin;

/**
 * @author csterling
 * @goal outrigger
 * @requiresProject false
 */
public class OutriggerServiceLauncher extends AbstractServiceLauncherMojo {

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getServiceName()
	 */
	public String getServiceName() {
		return "outrigger";
	}

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getInterfaceName()
	 */
	public String getInterfaceName() {
		return "OutriggerServer";
	}

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getImplName()
	 */
	public String getImplName() {
		if (TRANSIENT_MODE.equals(mode)) {
			return "TransientOutriggerImpl";
		} else if (ACTIVATABLE_MODE.equals(mode) || NON_ACTIVATABLE_MODE.equals(mode)) {
			return "PersistentOutriggerImpl";
		}
		
		return null;
	}

}
