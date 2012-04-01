package com.almuramc.digilock.util;

import java.io.File;

import com.almuramc.digilock.Digilock;

public class Config {
	public static String LIBRARY_MENUKEY;
	public static String LIBRARY_LOCKKEY;
	public static Boolean LIBRARY_USESIGNEDITGUI;
	public static int DIGILOCK_COST;
	public static int DIGILOCK_USEMAXCOST;
	public static int DIGILOCK_DESTROYCOST;
	public static String DIGILOCK_SOUND;
	public static Boolean DIGILOCK_SOUND_ON;
	public static String DIGILOCK_DefCloseTimer;
	private final static String CONFIG_FILE = "config.yml";
	final static File configfile = new File(Digilock.plugin.getDataFolder(), CONFIG_FILE);

	public static void bitSetupConfig() {
		if (!Digilock.plugin.getDataFolder().exists()) {
			Digilock.plugin.getDataFolder().mkdirs();
		}
		if (!configfile.exists()) {
			//TODO read in config
		}

		//TODO implement Dinnerbone's config.

		//General
		//LIBRARY_MENUKEY = getStringParm("Library.MenuKey", "KEY_M");
		//LIBRARY_LOCKKEY = getStringParm("Library.LockKey", "KEY_L");
		//LIBRARY_USESIGNEDITGUI = getBooleanParm("Library.UseSignEditGUI", true);

		//Digilock
		//DIGILOCK_COST = getIntParm("DigiLock.Cost", 50);
		//DIGILOCK_USEMAXCOST = getIntParm("DigiLock.UseMaxCost", 100);
		//DIGILOCK_DESTROYCOST = getIntParm("DigiLock.DestroyCost", -10);
		//DIGILOCK_SOUND = getStringParm("DigiLock.Sound", "http://dl.dropbox.com/u/36067670/lock/Sounds/Digilock.wav");
		//DIGILOCK_SOUND_ON = getBooleanParm("DigiLock.SoundOn", false);
		//DIGILOCK_ADVANCED_GUI = getBooleanParm("DigiLock.AdvancedGUIOn", false);
		//DIGILOCK_DefCloseTimer = getStringParm("DigiLock.DefCloseTimer", "2");
	}
}
