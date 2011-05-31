/**
 * 
 */
package org.jini.maven.plugin;

/**
 * @author csterling
 * @goal mercury
 * @requiresProject false
 */
public class MercuryServiceLauncher extends AbstractServiceLauncherMojo {

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getServiceName()
	 */
	public String getServiceName() {
		return "mercury";
	}

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getInterfaceName()
	 */
	public String getInterfaceName() {
		return "MailboxBackEnd";
	}

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getImplName()
	 */
	public String getImplName() {
		if (TRANSIENT_MODE.equals(mode)) {
			return "TransientMercuryImpl";
		} else if (ACTIVATABLE_MODE.equals(mode)) {
			return "ActivatableMercuryImpl";
		} else if (NON_ACTIVATABLE_MODE.equals(mode)) {
			return "NonActivatableMercuryImpl";
		}
		
		return null;
	}

}
