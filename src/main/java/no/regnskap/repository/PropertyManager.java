package no.regnskap.repository;

import java.io.IOException;
import java.util.Properties;


public class PropertyManager {
	
	private static PropertyManager instance = null;
	private Properties properties = null;
	
	
	private PropertyManager() throws IOException {
		properties = new Properties();
		properties.load(PropertyManager.class.getResourceAsStream("/regnskapsapi.properties"));
	}
	
	public static PropertyManager getInstance() throws IOException {
		if (instance == null) {
			instance = new PropertyManager();
		}
		return instance;
	}

	public String getProperty(final String key) {
		return properties==null ? null : properties.getProperty(key);
	}

}
