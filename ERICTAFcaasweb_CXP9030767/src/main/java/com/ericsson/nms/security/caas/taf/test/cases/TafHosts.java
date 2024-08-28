/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.caas.taf.test.cases;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;

/**
 * Class providing common interface to access the hosts in case of a TOR deployment.
 * 
 * Reads the hosts from the host.properties (using TAF DataHandler)
 * 
 * TOR deployment contains the following hosts:
 * SC1
 * SC2
 * SC1 JBOSS instance
 * SC2 JBOSS instance
 * 
 */
public class TafHosts {

	static Host parent;
	static Host sc1;
	static Host secServ1;
	static Host sc2;
	static Host secServ2;

	static String secServ1Ip;
	static String secServ2Ip;
	
	/**
	 * @return SC-1 Host
	 */
	public static Host getSC1() {
		return sc1;
	}
	/**
	 * @return SC-2 Host
	 */
	public static Host getSC2() {
		return sc2;
	}
	/**
	 * @return SC-1 JBOSS instance Host
	 */
	public static Host getsecServ1() {
		return secServ1;
	}
	/**
	 * @return SC-2 JBOSS instance Host
	 */
	public static Host getsecServ2() {
		return secServ2;
	}


	public static String getsecServ1RealIp() {
		return getRealIp(secServ1);
	}
	public static String getsecServ2RealIp() {
		return getRealIp(secServ2);
	}

	public static String getRealIp(Host host) {
		String origIp = host.getOriginalIp();
		String ip = (null == origIp) ? host.getIp() : origIp;
		return ip;
	}
	
	static {
		String deploymentType = (String) DataHandler.getAttribute("deployment.type");

		if ("multinode".equals(deploymentType))  {
			System.out.println("Found multinode deplyoment in host.properties.");
		}
		else if ("cloud".equals(deploymentType)) {
			System.out.println("Found cloud environment in host.properties.");
		}
		else {
			System.out.println("Deployment type is unkown: " + deploymentType);
		}
		//secServ1
		TafHosts.secServ1 = DataHandler.getHostByName("secServ1");
		//printHost(TafHosts.secServ1);

		//secServ2
		TafHosts.secServ2 = DataHandler.getHostByName("secServ2");
		//printHost(TafHosts.secServ2);

		//SC1
		TafHosts.sc1 = DataHandler.getHostByName("sc1");
		//printHost(TafHosts.sc1);

		//SC2			
		TafHosts.sc2 = DataHandler.getHostByName("sc2");
		//printHost(TafHosts.sc2);		
	}

	public static void printHost(Host host)  {
		if (null != host) {
			String parent = null;
			try {
				parent = host.getParentName();
			}
			catch (Exception e) {
				//getParentName throws exception in case parent is null
			}

			try {
				System.out.println(
						"Host: " + host.getHostname() + "\n" + 
								"Type: " + host.getType() + "\n" +
								"Ip: " + host.getIp() + "\n" + 
								"OrigIp: " + host.getOriginalIp() + "\n" +
								"RealIp: " + getRealIp(host) + "\n" +  
								"User: " + host.getUser() + "\n" +  
								"Pass: " + host.getPass() + "\n" + 
								"TunnelPortOffset: " + host.getTunnelPortOffset()+ "\n" +  
								"Parent: " + parent + "\n" + 
								"Nodes: " + host.getNodes()+ "\n");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Host: null" + "\n");
		}
	}
}
