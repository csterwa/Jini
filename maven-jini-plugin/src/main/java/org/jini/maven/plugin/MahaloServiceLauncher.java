/**
 * 
 */
package org.jini.maven.plugin;

/**
 * @author csterling
 * @goal mahalo
 * @requiresProject false
 */
public class MahaloServiceLauncher extends AbstractServiceLauncherMojo {

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getServiceName()
	 */
	public String getServiceName() {
		return "mahalo";
	}

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getInterfaceName()
	 */
	public String getInterfaceName() {
		return "TxnManager";
	}

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getImplName()
	 */
	public String getImplName() {
		if (TRANSIENT_MODE.equals(mode)) {
			return "TransientMahaloImpl";
		} else if (ACTIVATABLE_MODE.equals(mode)) {
			return "ActivatableMahaloImpl"; 
		} else if (NON_ACTIVATABLE_MODE.equals(mode)) {
			return "NonActivatableMahaloImpl";
		}
		
		return null;
	}

}
