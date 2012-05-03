package com.almuramc.digilock;

import java.util.HashMap;
import java.util.Map;

import com.almuramc.digilock.command.LockCommand;
import com.almuramc.digilock.gui.KeyHandler;
import com.almuramc.digilock.listener.BlockListener;
import com.almuramc.digilock.listener.PlayerListener;
import com.almuramc.digilock.listener.KeyboardListener;
import com.almuramc.digilock.util.Dependency;
import com.almuramc.digilock.util.LockConfig;
import com.almuramc.digilock.util.Messages;
import com.almuramc.digilock.util.SqlHandler;
import com.almuramc.digilock.util.Permissions;

import org.bukkit.plugin.Plugin;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.plugin.SpoutPlugin;

public class Digilock extends SpoutPlugin {
	private static Digilock instance;
	private static Dependency hooks;
	private static LockConfig config;
	private static SqlHandler sql;
	

	@Override
	public void onDisable() {
		if (Digilock.getConf().getSQLType().equals("SQLITE")) {
		sql.getSqliteHandler().close();
		Messages.showInfo("SQLite Database Closed.");
		}
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

		//Setup permissions
		 Permissions.setupPermissions(this);
		
		 //Register command
		new LockCommand(this);

		registerEvents(new BlockListener());
		registerEvents(new PlayerListener());
		registerEvents(new KeyboardListener());

		SpoutManager.getKeyBindingManager().registerBinding("Digilock.Lock", Keyboard.KEY_L, "The key to lock chests", new KeyHandler(this, Keyboard.KEY_L), this);

		log("v" + this.getVersion() + " enabled.");
	}

	public static Digilock getInstance() {
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

	public static Map<Integer, String> holdingKey = new HashMap<Integer, String>();
	public static Map<Integer, Integer> userno = new HashMap<Integer, Integer>();

	public static void removeUserData(int id) {
		if (userno.containsKey(id)) {
			// DigiLock
			holdingKey.remove(id);
			userno.remove(id);
		}
	}

	public static void addUserData(int id) {
		if (!userno.containsKey(id)) {
			// DigiLock
			userno.put(id, new Integer(id));
			holdingKey.put(id, "KEY_LCONTROL");
		}

	}
}