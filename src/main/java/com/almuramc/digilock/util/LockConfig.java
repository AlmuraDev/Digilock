package com.almuramc.digilock.util;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Utility class to supplement the config file.
 */
public class LockConfig {
	private final Plugin parent;
	private static FileConfiguration config;

	public LockConfig(Plugin instance) {
		parent = instance;
		if (!new File(parent.getDataFolder(), "config.yml").exists()) {
			parent.saveDefaultConfig();
		}
		config = parent.getConfig();
	}

	public boolean useEconomy() {
		return config.getBoolean("features.economy");
	}

	public boolean useWar() {
		return config.getBoolean("features.war");
	}

	public boolean useArena() {
		return config.getBoolean("features.arena");
	}

	public boolean useJail() {
		return config.getBoolean("features.jail");
	}

	public boolean useResidence() {
		return config.getBoolean("features.residence");
	}

	public String getLockKey() {
		return config.getString("gui.lockkey").toUpperCase();
	}

	public String getMenuKey() {
		return config.getString("gui.menukey").toUpperCase();
	}

	public boolean useSignGUI() {
		return config.getBoolean("gui.usesigngui");
	}

	public String getSQLType() {
		return config.getString("storage.type").toUpperCase();
	}

	public String getSQLHost() {
		return config.getString("storage.host");
	}

	public String getSQLPort() {
		return config.getString("storage.port");
	}

	public String getSQLUsername() {
		return config.getString("storage.username");
	}

	public String getSQLPassword() {
		return config.getString("storage.password");
	}

	public String getSQLDatabase() {
		return config.getString("storage.database");
	}

	public double getLockCost() {
		return config.getDouble("lock.cost");
	}

	public double getLockMaxCost() {
		return config.getDouble("lock.maxcost");
	}

	public double getDestroyCost() {
		return config.getDouble("lock.destroycost");
	}
	
	public int getDefaultCloseTimer() {
		return config.getInt("lock.defaultclosetimer");
	}

	public boolean playLockSound() {
		return config.getBoolean("lock.playsound");
	}

	public String getSoundURL() {
		return config.getString("lock.sound");
	}
}
