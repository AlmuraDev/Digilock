package com.almuramc.digilock;

import com.almuramc.digilock.command.LockCommand;
import com.almuramc.digilock.gui.KeyHandler;
import com.almuramc.digilock.listener.BlockListener;
import com.almuramc.digilock.listener.PlayerListener;
import com.almuramc.digilock.util.Dependency;
import com.almuramc.digilock.util.LockConfig;
import com.almuramc.digilock.util.SqlHandler;

import org.bukkit.plugin.Plugin;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.plugin.SpoutPlugin;

public class Digilock extends SpoutPlugin {
	private static Plugin instance;
	private static Dependency hooks;
	private static LockConfig config;
	private static SqlHandler sql;

	@Override
	public void onDisable() {
		log("disabled.");
	}

	@Override
	public void onEnable() {
		instance = this;

		//Setup config
		config = new LockConfig(this);

		//Handles all dependencies
		hooks = new Dependency(this);

		sql = new SqlHandler(this);

		//Register command
		new LockCommand(this);

		registerEvents(new BlockListener());
		registerEvents(new PlayerListener());

		SpoutManager.getKeyBindingManager().registerBinding("Digilock.Lock", Keyboard.KEY_L, "The key to lock chests", new KeyHandler(this, Keyboard.KEY_L), this);

		log("v" + this.getVersion() + " enabled.");
	}

	public static Plugin getInstance() {
		return instance;
	}

	public static Dependency getHooks() {
		return hooks;
	}

	public static LockConfig getConf() {
		return config;
	}

	public static SqlHandler getHandler() {
		return sql;
	}
}