package com.almuramc.digilock.util;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Utility class to supplement the config file.
 */
public class LockConfig {
	private final Plugin parent;
	private FileConfiguration config;

	public LockConfig(Plugin instance) {
		parent = instance;
		if (!new File(parent.getDataFolder(), "config.yml").exists()) {
			parent.saveDefaultConfig();
		}
		config = parent.getConfig();
	}

	public boolean useEconomy() {
		return parent.getConfig().getBoolean("features.economy");
	}

	public boolean useWar() {
		return parent.getConfig().getBoolean("features.war");
	}

	public boolean useArena() {
		return parent.getConfig().getBoolean("features.arena");
	}

	public boolean useJail() {
		return parent.getConfig().getBoolean("features.jail");
	}

	public boolean useResidence() {
		return parent.getConfig().getBoolean("features.residence");
	}
}
