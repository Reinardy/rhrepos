package com.sc.retail.csl.notification;

import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OTPProperties {
	private String location;
	private BundleContext bundleContext;
	private static final Logger logger = LoggerFactory.getLogger(OTPProperties.class);
	
	public Properties getProperties() {
		Properties properties = new Properties();
		
		try {
			properties.load(OTPProperties.class.getClassLoader().getResourceAsStream(location));
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		return properties;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	
}
