package ru.simpleweb.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

@ApplicationPath("rest/*")
public class Application extends javax.ws.rs.core.Application {
	
	static final Logger LOG = Logger.getLogger(Application.class);

	public static Map<String, String> loadMap(String resourceName) {
		Properties props = loadProps(resourceName);
		return toMap(props);
	}

	public static Map<String, String> toMap(Properties props) {
		Map<String,String > result = new LinkedHashMap<>();
		for (Entry<Object, Object> e : props.entrySet()) {
			result.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
		}
		return result;
	}

	public static Properties loadProps(String resourceName) {
		Properties props = new Properties(); 
		try {
			InputStream rs = Application.class.getResourceAsStream(resourceName);
			props.load(rs);
		} catch (IOException e) {
			LOG.error( resourceName + Throwables.getStackTraceAsString(e));
		}		
		return props;
	}

	public static class PROPS {

		static Map<String, String> props = Application.loadMap("app.properties");
		static String appURL;

		public static void initWithHttpRequest(HttpServletRequest request) {
			if (appURL == null) {
				appURL = new StringBuffer().append(request.getScheme()).append("://").append(request.getServerName())
						.append(":").append(request.getServerPort()).append(request.getContextPath()).toString();
			}
		}

		public static String getAppBaseUrl() {
			return appURL;
		}

		public static Map<String, String> getAll() {
			return props;
		}

		public static String get(String key) {
			return props.get(key);
		}
	}

}
