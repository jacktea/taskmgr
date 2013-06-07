package xg.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sunyard.task.audit.AuditTaskService;

import xg.task.thread.InnerSocketThread;
import xg.util.Loader;


public class Manager {
	
	public static int PORT = 50200;
	
	private final String DEFAULT_CONFIG = "config.properties";
	
	private String CUST_SPRING_CONFIG = "applicationContext.xml";
	
	private Properties config;
	
	private static Manager manager = new Manager();
	
	private final ApplicationContext ctx;
	
	private final InnerSocketThread innerSocketThread;
	
	private Manager(){
		
		config = parseConfig(DEFAULT_CONFIG);
		if(null!=config.getProperty("port")){
			PORT = Integer.parseInt(config.getProperty("port"));
		}
		if(null!=config.getProperty("springCfg")){
			CUST_SPRING_CONFIG = config.getProperty("springCfg");
		}
		
		System.out.println("PORT:"+PORT);
		
		ctx = new ClassPathXmlApplicationContext(CUST_SPRING_CONFIG);
		AuditTaskService ts = ctx.getBean(AuditTaskService.class);
		innerSocketThread = new InnerSocketThread();
		innerSocketThread.setTs(ts);
		innerSocketThread.setDaemon(true);
	}
	
	public static Manager instance() {
		return manager;
	}
	
	public void startupThread(){		
		innerSocketThread.start();
	}
	/**
	 * 解析配置文件
	 * @param configFile
	 * @return
	 */
	private Properties parseConfig(String configFile){
		Properties prop = new Properties();
		URL url = Loader.getResource(configFile);
		if (url != null) {
			InputStream in = null;
			try {
				URLConnection urlConnection = url.openConnection();
				urlConnection.setUseCaches(false);
				in = urlConnection.getInputStream();
				prop.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(null!=in){
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return prop;
	}

}
