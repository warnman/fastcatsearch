package org.fastcatsearch.env;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {
	
	private static final Logger logger = LoggerFactory.getLogger(Environment.class);
	
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String PATH_SEPARATOR = System.getProperty("path.separator");
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String OS_NAME = System.getProperty("os.name");
	
	private String home = "";
	private File homeFile;
	
	private SettingManager settingManager;
	
	private boolean isMasterNode;
	
	public Environment(String homeDirPath){
		home = homeDirPath;
		homeFile= new File(homeDirPath);
		
		if (home.length() > 0 && !home.endsWith(FILE_SEPARATOR)) {
			home = home + FILE_SEPARATOR;
		}
		
		System.setProperty("fastcatsearch.home", homeFile.getAbsolutePath());
		System.setProperty("logback.configurationFile", new File(new File(homeFile, "conf"), "logback.xml").getAbsolutePath());
		logger.info("Setting Home = {}", home);
	}
	
	public Environment init(){
		settingManager = new SettingManager(this);
		
		String myNodeName = settingManager.getSettings().getString("me","me");
		String masterNodeName = settingManager.getSettings().getString("master", "master");
		if(myNodeName.equals(masterNodeName)){
			isMasterNode = true;
		}
		return this;
	}
	
	public String home() {
		return home;
	}
	public File homeFile() {
		return homeFile;
	}
	
	public SettingManager settingManager(){
		return settingManager;
	}
	
	public Path filePaths(){
		return new Path(homeFile);
	}

	public boolean isMasterNode(){
		return isMasterNode;
	}
}
