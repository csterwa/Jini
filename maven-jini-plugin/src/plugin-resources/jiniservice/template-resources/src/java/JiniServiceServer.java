package @PACKAGE@;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceID ;
import net.jini.lease.LeaseListener;             
import net.jini.lease.LeaseRenewalEvent;         
import net.jini.lease.LeaseRenewalManager;       
import java.io.*;

/**
 * NOTE: Originally taken from code examples in Jan Newmarch's tutorial at 
 * <a href="http://jan.netcomp.monash.edu.au/java/jini/tutorial/SimpleExample.xml">here</a>
 * My Jini Service!
 *
 * @author <a href="csterwa@jini.org">Chris Sterling</a>
 * @author Jan Newmarch
 */
public class JiniServiceServer implements DiscoveryListener, LeaseListener {
    
    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
    protected ServiceID serviceID = null;
    protected JiniServiceImpl impl = null;

    public static void main(String argv[]) {
    	new JiniServiceServer();
		
        // keep server running forever to 
		// - allow time for locator discovery and
		// - keep re-registering the lease
		Object keepAlive = new Object();
		synchronized(keepAlive) {
		    try {
		    	keepAlive.wait();
		    } catch(java.lang.InterruptedException e) {
		    	// do nothing
		    }
		}
    }

    public JiniServiceServer() {
		// Create the service
		impl = new JiniServiceImpl();
	
		// Try to load the service ID from file.
		// It isn't an error if we can't load it, because
		// maybe this is the first time this service has run
		DataInputStream din = null;
		
		try {
		    din = new DataInputStream(new FileInputStream(this.getClass().getName() + ".id"));
		    serviceID = new ServiceID(din);
		} catch(Exception e) {
		    // ignore
		}

        System.setSecurityManager(new RMISecurityManager());

        LookupDiscovery discover = null;
        
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println("Discovery failed " + e.toString());
            System.exit(1);
        }

        discover.addDiscoveryListener(this);
    }
    
    public void discovered(DiscoveryEvent evt) {
        ServiceRegistrar[] registrars = evt.getRegistrars();

        for (int n = 0; n < registrars.length; n++) {
            ServiceRegistrar registrar = registrars[n];

            ServiceItem item = new ServiceItem(serviceID, impl, null);
            ServiceRegistration reg = null;

            try {
				reg = registrar.register(item, Lease.FOREVER);
		    } catch(java.rmi.RemoteException e) {
		    	System.err.println("Register exception: " + e.toString());
		    	continue;
		    }
		    
		    System.out.println("Service registered with id " + reg.getServiceID());
	
		    // set lease renewal in place
		    leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, this);
	
		    // set the serviceID if necessary
		    if (serviceID == null) {
		    	serviceID = reg.getServiceID();
	
				// try to save the service ID in a file
				DataOutputStream dout = null;
				try {
				    dout = new DataOutputStream(new FileOutputStream(this.getClass().getName() + ".id"));
				    serviceID.writeBytes(dout);
				    dout.flush();
				} catch(Exception e) {
				    // ignore
				}
		    }
		}
    }

    public void notify(LeaseRenewalEvent evt) {
    	System.out.println("Lease expired " + evt.toString());
    }   
    
	public void discarded(DiscoveryEvent arg0) {
		// do nothing
	}   

} // JiniServiceServer
