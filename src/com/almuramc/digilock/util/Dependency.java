package com.almuramc.digilock.util;

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
	//public Residence area = null;
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
			//TODO Implement Residence
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

	public Residence getResidenceHook() {
		return null; //TODO Implement Residence
	}
}
