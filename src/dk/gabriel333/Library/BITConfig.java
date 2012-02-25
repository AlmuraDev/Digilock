package dk.gabriel333.Library;

import dk.gabriel333.BukkitInventoryTools.BIT;
import java.io.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class BITConfig {
	
	final static int LATEST_VERSION=1;
	
	public static int LIBRARY_VERSION;
	public static String LIBRARY_LANGUAGE;
	public static String LIBRARY_SORTKEY;
	public static String LIBRARY_MENUKEY;
	public static String LIBRARY_LOCKKEY;
	public static String LIBRARY_READKEY;
	public static String LIBRARY_BACKPACK;
	public static String LIBRARY_WORKBENCH;
	public static Boolean LIBRARY_USESIGNEDITGUI;
	
	public static String STORAGE_TYPE;
	public static String STORAGE_HOST;
	public static String STORAGE_USERNAME;
	public static String STORAGE_PASSWORD;
	public static String STORAGE_DATABASE;
	
	public static Boolean SORT_DISPLAYSORTARCHIEVEMENT;
	private static String SORT_SORTSEQ;
	public static String[] SORTSEQ;
	private static String SORT_TOOLS;
	public static String[] tools;
	private static String SORT_WEAPONS;
	public static String[] weapons;
	private static String SORT_ARMORS;
	public static String[] armors;
	private static String SORT_FOODS;
	public static String[] foods;
	private static String SORT_VEHICLES;
	public static String[] vehicles;
	private static String SORT_BUCKETS;
	public static String[] buckets;
	
	public static int DIGILOCK_COST;
	public static int DIGILOCK_USEMAXCOST;
	public static int DIGILOCK_DESTROYCOST;
	public static String DIGILOCK_SOUND;
	public static Boolean DIGILOCK_SOUND_ON;
	public static Boolean DIGILOCK_ADVANCED_GUI;
	public static String DIGILOCK_DefCloseTimer;
	
	public static Boolean BOOKSHELF_ENABLE;
	public static int BOOKSHELF_COST;
	public static int BOOKSHELF_USEMAXCOST;
	public static int BOOKSHELF_SIZE;
	public static int BOOKSHELF_DESTROYCOST;
	public static Boolean BOOKSHELF_RECOVER_ON_BREAK;
	
	public static int BOOK_COST;
	public static int BOOK_USEMAXCOST;
	public static int BOOK_DESTROYCOST;
	
	public static String SBP_language;
	public static Boolean SBP_InventoriesShareDefault;
	private static String noBackpackRegions;
	public static String[] SBP_noBackpackRegions;
	public static Boolean SBP_EnableEconomy;
	public static Boolean SBP_DisableSBPCreative;
	public static double SBP_price9;
	public static double SBP_price18;
	public static double SBP_price27;
	public static double SBP_price36;
	public static double SBP_price45;
	public static double SBP_price54;
	public static int SBP_blackOrWhiteList;
	public static String whitelist;
	public static String SBP_whitelist[];
	public static String blacklist;
	public static String SBP_blacklist[];
	public static boolean SBP_workbenchEnabled;
	public static boolean SBP_workbenchInventory;
	public static boolean SBP_workbenchBuyable;
	public static boolean SBP_useWidget;
	public static int SBP_widgetX;
	public static int SBP_widgetY;
	public static int SBP_saveTime;
	public static boolean SBP_logSaves;


	
	public static Boolean DEBUG_PERMISSIONS;
	public static Boolean DEBUG_SORTINVENTORY;
	public static Boolean DEBUG_ONENABLE;
	public static Boolean DEBUG_KEYBOARD;
	public static Boolean DEBUG_SQL;
	public static Boolean DEBUG_GUI;
	public static Boolean DEBUG_DOOR;
	public static Boolean DEBUG_EVENTS;
	public static Boolean DEBUG_ADVANCEDGUI;
	
	private final static String CONFIG_FILE = "config.yml";
	public static YamlConfiguration config;
	private static Boolean dosave = false;
	final static File configfile = new File(BIT.plugin.getDataFolder(),
			CONFIG_FILE);
	
	public static void bitSetupConfig() {
		if (!BIT.plugin.getDataFolder().exists())
			BIT.plugin.getDataFolder().mkdirs();
		if (!configfile.exists()) {
			BITMessages.showInfo("Loading config.yml from BukkitInventoryTools.jar");
			loadFileFromJar(CONFIG_FILE);
		}
		config = new YamlConfiguration();
		try {
			config.load(configfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		//General
		LIBRARY_VERSION = getIntParm("Library.Version", 0);
		LIBRARY_LANGUAGE = getStringParm("Library.Language", "EN");
		LIBRARY_SORTKEY = getStringParm("Library.SortKey", "KEY_S");
		LIBRARY_MENUKEY = getStringParm("Library.MenuKey", "KEY_M");
		LIBRARY_LOCKKEY = getStringParm("Library.LockKey", "KEY_L");
		LIBRARY_READKEY = getStringParm("Library.ReadKey", "KEY_R");
		LIBRARY_BACKPACK = getStringParm("Library.Backpack", "KEY_B");
		LIBRARY_WORKBENCH = getStringParm("Library.Workbench", "KEY_N");
		LIBRARY_USESIGNEDITGUI = getBooleanParm("Library.UseSignEditGUI", true);

		//SQL
		STORAGE_TYPE=getStringParm("Storage.Type", "SQLite");
		STORAGE_HOST=getStringParm("Storage.Host", "SQLite");
		STORAGE_USERNAME=getStringParm("Storage.Username", "Admin");
		STORAGE_PASSWORD=getStringParm("Storage.Password", "Changethis");
		STORAGE_DATABASE=getStringParm("Storage.Database", "SortInventory");
		//Sort                                              
		SORT_DISPLAYSORTARCHIEVEMENT = getBooleanParm("Sort.DisplaySortArchievement", true);
        SORT_SORTSEQ = getStringParm("Sort.SortSEQ", "STONE,COBBLESTONE,DIRT,WOOD");
		SORTSEQ = SORT_SORTSEQ.split(",");
		
		SORT_TOOLS = getStringParm("Sort.Tools", "256,257,258,269,270,271,273,274,275,277,278,279,284,285,286,290,291,292,293,294,346");
		tools = SORT_TOOLS.split(",");
		SORT_WEAPONS = getStringParm("Sort.Weapons", "267,268,272,276,283,261");
		weapons = SORT_WEAPONS.split(",");
		SORT_ARMORS = getStringParm("Sort.Armors", "298,299,300,301,302,303,304,305,306,307,308,309,310,311,312,313,314,315,316,317");
		armors = SORT_ARMORS.split(",");
		SORT_FOODS = getStringParm("Sort.Foods", "260,282,297,319,320,322,349,350,354,357");
		foods = SORT_FOODS.split(",");
		SORT_VEHICLES = getStringParm("Sort.Vehicles", "328,333,342,343");
		vehicles = SORT_VEHICLES.split(",");
		SORT_BUCKETS = getStringParm("Sort.Buckets","326,327,335");
		buckets = SORT_BUCKETS.split(",");
		
		//Digilock
		DIGILOCK_COST = getIntParm("DigiLock.Cost", 50);
		DIGILOCK_USEMAXCOST = getIntParm("DigiLock.UseMaxCost", 100);
		DIGILOCK_DESTROYCOST = getIntParm("DigiLock.DestroyCost", -10);
		DIGILOCK_SOUND = getStringParm("DigiLock.Sound","http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Sounds/Digilock.wav");
		DIGILOCK_SOUND_ON = getBooleanParm("DigiLock.SoundOn", false);
		DIGILOCK_ADVANCED_GUI = getBooleanParm("DigiLock.AdvancedGUIOn", false);
		DIGILOCK_DefCloseTimer = getStringParm("DigiLock.DefCloseTimer", "2");
		
		//Bookshelf
		BOOKSHELF_ENABLE = getBooleanParm("Bookshelf.Enabled", false);
		BOOKSHELF_COST = getIntParm("Bookshelf.Cost", 50);
		BOOKSHELF_USEMAXCOST = getIntParm("Bookshelf.UseMaxCost", 100);
		BOOKSHELF_SIZE = getIntParm("Bookshelf.Size", 9);
		BOOKSHELF_DESTROYCOST = getIntParm("Bookshelf.DestroyCost", 10);
		BOOKSHELF_RECOVER_ON_BREAK=getBooleanParm("Bookshelf.RecoverOnBreak", false);
		
		//Book
		BOOK_COST = getIntParm("Book.Cost", 10);
		BOOK_USEMAXCOST = getIntParm("Book.UseMaxCost", 50);
		BOOK_DESTROYCOST = getIntParm("Book.DestroyCost", 0);
		
		//SpoutBackpack
		SBP_language = getStringParm("SBP.Language","EN");
		SBP_InventoriesShareDefault = getBooleanParm("SBP.InventoriesShareDefault",true);
		noBackpackRegions = getStringParm("SBP.RegionWhereBackpacksAreDisabled", "region1,region2");
		SBP_noBackpackRegions = noBackpackRegions.split(",");
		SBP_EnableEconomy = getBooleanParm("SBP.EnableEconomy",true);
		SBP_DisableSBPCreative = getBooleanParm("SBP.DisableSBPCreative",true);
		SBP_price9 = getDoubleParm("SBP.Price9", 100.00);
		SBP_price18 = getDoubleParm("SBP.Price18", 10.00);
		SBP_price27 = getDoubleParm("SBP.Price27", 20.00);
		SBP_price36 = getDoubleParm("SBP.Price36", 30.00);
		SBP_price45 = getDoubleParm("SBP.Price45", 40.00);
		SBP_price54 = getDoubleParm("SBP.Price54", 50.00);
		SBP_blackOrWhiteList = getIntParm("SBP.NoneBlackOrWhiteList",0);
		if (SBP_blackOrWhiteList != 1 && SBP_blackOrWhiteList != 2) {
			SBP_blackOrWhiteList = 0;
		}

			
		whitelist = getStringParm("SBP.Whitelist","262");
		SBP_whitelist = whitelist.split(",");
		blacklist = getStringParm("SBP.Blacklist","264");
		SBP_blacklist = blacklist.split(",");
		SBP_workbenchEnabled = getBooleanParm("SBP.WorkbenchEnabled",true);
		SBP_workbenchBuyable = getBooleanParm("SBP.WorkbenchBuyable",true);
		SBP_workbenchInventory = getBooleanParm("SBP.WorkbenchNeededInInventory",false);
		SBP_useWidget = getBooleanParm("SBP.WidgetEnabled",false);
		SBP_widgetX = getIntParm("SBP.WidgetPositionX",3);
		SBP_widgetY = getIntParm("SBP.WidgetPositionY",5);
		SBP_logSaves = getBooleanParm("SBP.SavesLog",false);
		SBP_saveTime = getIntParm("SBP.SavesInterval(InMinutes)",5);
		
		//Debug
		DEBUG_PERMISSIONS = getBooleanParm("Debug.Permissions", false);
		DEBUG_SORTINVENTORY = getBooleanParm("Debug.Inventory", false);
		DEBUG_ONENABLE = getBooleanParm("Debug.OnEnable", false);
		DEBUG_KEYBOARD = getBooleanParm("Debug.Keyboard", false);
		DEBUG_SQL = getBooleanParm("Debug.SQL", false);
		DEBUG_GUI = getBooleanParm("Debug.GUI", false);
		DEBUG_DOOR = getBooleanParm("Debug.Door", false);
		DEBUG_EVENTS = getBooleanParm("Debug.Events", false);
		DEBUG_ADVANCEDGUI = getBooleanParm("Debug.AdvancedGUI", false);
		
		if (dosave || LATEST_VERSION>LIBRARY_VERSION) {
			config.options().header("##########################################################\n"+
					" This is an autogenerated config.yml, because you had an #\n"+
					" old version of the config.yml. I recommended that you   #\n"+
					" backup your current config.yml and then delete it from  #\n"+
					" from the plugin directory and reload the server, to     #\n"+
					" get a fresh config.yml                                  #\n"+
					"                                                         #\n"+
					"                                                         #\n"+
					"##########################################################");
			BITMessages.showWarning("YOUR CONFIG.YML IS NOT UP TO DATE!");
			try {
				config.save(configfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}

	private static int getIntParm(String path, int def) {
		if (!config.contains(path)) {
			config.set(path, def);
			dosave=true;
			BITMessages.showWarning("Missing parameter:" +path+ " , Let me add that for you and save it.");
		} 
		return config.getInt(path, def);
	}
	
	private static double getDoubleParm(String path, double def) {
		if (!config.contains(path)) {
			config.set(path, def);
			dosave=true;
			BITMessages.showWarning("Missing parameter:" +path+ " , Let me add that for you and save it.");
		} 
		return config.getDouble(path, def);
	}

	private static String getStringParm(String path, String def) {
		if (!config.contains(path)) {
			config.set(path, def);
			dosave=true;
			BITMessages.showWarning("Missing parameter:" +path+ " , Let me add that for you and save it.");
		} 
		return config.getString(path, def);
	}

	public static Boolean getBooleanParm(String path, Boolean def) {
		if (!config.contains(path)) {
			config.set(path, def);
			dosave=true;
			BITMessages.showWarning("Missing parameter:" +path+ " , Let me add that for you and save it.");
		} 
		return config.getBoolean(path, def);
	}
	
	public static void addBooleanParmToConfig(String path, Boolean def ){
		try {
			config.load(configfile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		config.set(path, def);
		try {
			config.options().header("##########################################################\n"+
					" This is an autogenerated config.yml, because you had an #\n"+
					" old version of the config.yml. I recommended that you   #\n"+
					" backup your current config.yml and then delete it from  #\n"+
					" from the plugin directory and reload the server, to     #\n"+
					" get a fresh config.yml                                  #\n"+
					"                                                         #\n"+
					" All worlds must be added manually, before first run, if #\n" +
					" you dont want to see this message. Example              #\n" +
					" SBP.InventoriesShare.world: true                        #\n"+
					" SBP.InventoriesShare.world_nether: true                 #\n"+
					" SBP.InventoriesShare.creative: false                    #\n"+
					"##########################################################");
			config.save(configfile);
			BITMessages.showInfo("saving parm:"+path+" into Config.yml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static File loadFileFromJar(String filename) {
		File actual = new File(BITPlugin.PLUGIN_FOLDER, filename);
		if (!actual.exists()) {
			InputStream input = BITConfig.class.getResourceAsStream("/"
					+ filename);
			if (input != null) {
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length;

					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
					BITMessages.showInfo("The file: " + filename
							+ " has been created.");
				} catch (Exception e) {
					BITMessages.showStackTrace(e);
				} finally {
					try {
						input.close();
					} catch (Exception e) {
					}
					try {
						output.flush();
						output.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return actual;
	}

}
