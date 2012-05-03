package com.almuramc.digilock.util;

import com.almuramc.digilock.Digilock;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.garbagemule.MobArena.MobArenaHandler;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailAPI;
import com.tommytony.war.War;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Dependency {
	//Plugin hooks
	private Economy econ = null;
	private War war = null;
	private MobArenaHandler arena = null;
	private JailAPI jail = null;
	private boolean hasResidence = false;
	private Plugin plugin;

	public Dependency(Plugin instance) {
		plugin = instance;
		PluginManager pm = plugin.getServer().getPluginManager();
		if (pm.isPluginEnabled("Vault")) {
			RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				econ = economyProvider.getProvider();
			}
		}

		if (pm.isPluginEnabled("War")) {
			war = new War();
		}

		if (pm.isPluginEnabled("MobArena")) {
			arena = new MobArenaHandler();
		}

		if (pm.isPluginEnabled("Jail")) {
			jail = ((Jail) plugin.getServer().getPluginManager().getPlugin("Jail")).API;
		}

		if (pm.isPluginEnabled("Residence")) {
			Messages.showInfo("Residence Property Protection Detected");
			if (Digilock.getConf().useResidence()) {
				hasResidence = true;
				FlagPermissions.addFlag("lockable");
				FlagPermissions.addResidenceOnlyFlag("lockable");
				Messages.showInfo("Custom Residence Flag of [lockable] added successfully");
			} else {
				Messages.showInfo("Residence was detected but the custom flags are currently disabled in the config.yml");
			}
		}
	}

	public Economy getEconHook() {
		return econ;
	}

	public War getWarHook() {
		return war;
	}

	public MobArenaHandler getArenaHook() {
		return arena;
	}

	public boolean isResidencyAvailable() {
		return hasResidence;
	}
}
