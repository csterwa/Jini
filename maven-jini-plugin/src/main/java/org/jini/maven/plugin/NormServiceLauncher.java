/**
 * 
 */
package org.jini.maven.plugin;

/**
 * @author csterling
 * @goal norm
 * @requiresProject false
 */
public class NormServiceLauncher extends AbstractServiceLauncherMojo {

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getServiceName()
	 */
	public String getServiceName() {
		return "norm";
	}

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getInterfaceName()
	 */
	public String getInterfaceName() {
		return "NormServer";
	}

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getImplName()
	 */
	public String getImplName() {
		if (TRANSIENT_MODE.equals(mode)) {
			return "TransientNormServerImpl";
		} else if (ACTIVATABLE_MODE.equals(mode)) {
			return "ActivatableNormServerImpl";
		} else if (NON_ACTIVATABLE_MODE.equals(mode)) {
			return "PersistentNormServerImpl";
		}
		
		return null;
	}

}
