/**
 * 
 */
package org.jini.maven.plugin;

/**
 * @author csterling
 * @goal fiddler
 * @requiresProject false
 */
public class FiddlerServiceLauncher extends AbstractServiceLauncherMojo {

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getServiceName()
	 */
	public String getServiceName() {
		return "fiddler";
	}

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getInterfaceName()
	 */
	public String getInterfaceName() {
		return "Fiddler";
	}

	/**
	 * @see org.jini.maven.plugin.AbstractServiceLauncherMojo#getImplName()
	 */
	public String getImplName() {
		if (TRANSIENT_MODE.equals(mode)) {
			return "TransientFiddlerImpl";
		} else if (ACTIVATABLE_MODE.equals(mode)) {
			return "ActivatableFiddlerImpl";
		} else if (NON_ACTIVATABLE_MODE.equals(mode)) {
			return "NonActivatableFiddlerImpl";
		}
		
		return null;
	}

}
