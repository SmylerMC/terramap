package fr.thesmyler.smylibgui;

import org.apache.logging.log4j.Logger;

public abstract class SmyLibGui {

	public static Logger logger;
	public static boolean debug;
	
	public static void init(Logger logger, boolean debug) {
		SmyLibGui.logger = logger;
		SmyLibGui.debug = debug;
	}
}
