package com.almuramc.digilock.util;

import com.almuramc.digilock.Digilock;

import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;

import org.bukkit.plugin.Plugin;

public class SqlHandler {
	private Plugin plugin;
	private MySQL mysql;
	private SQLite sqlite;
	private String digilockTable = "Digilock";

	public SqlHandler(Plugin instance) {
		plugin = instance;
		setupSQL();
	}

	private void setupSQL() {
		if (Digilock.getConf().getSQLType().equals("MYSQL")) {
			mysql = new MySQL(plugin.getLogger(), "[" + plugin.getName() + "]", Digilock.getConf().getSQLHost(), Digilock.getConf().getSQLPort(), Digilock.getConf().getSQLDatabase(), Digilock.getConf().getSQLUsername(), Digilock.getConf().getSQLPassword());
			mysql.open();
			if (mysql.checkConnection()) {
				//Check if the Connection was successful
				String query;
				Messages.showInfo("MySQL Initialization Successful.");
				if (!mysql.checkTable(digilockTable)) {
					query = "CREATE TABLE "
							+ digilockTable
							+ " (x INT, y INT, z INT, world VARCHAR(255), owner VARCHAR(255), "
							+ "pincode VARCHAR(255), coowners VARCHAR(255), users VARCHAR(255), closetimer INT, "
							+ "typeid INT, connectedto VARCHAR(255), usecost INT);";
					mysql.createTable(query);
					Messages.showInfo("MySQL Table Created Successfully.");
				} else {
					Messages.showInfo("MySQL Table Loaded.");
				}
			} else {
				Messages.showInfo("MySQL Initialization failed!");
			}
		} else {
			try {
				sqlite = new SQLite(plugin.getLogger(), "[" + plugin.getName() + "]", plugin.getName(), plugin.getDataFolder().getCanonicalPath());
			} catch (Exception e) {

			}
			sqlite.open();
			if (sqlite.open() != null) {
				Messages.showInfo("SQLite Initialization Successful.");
				String query;
				if (!sqlite.checkTable(digilockTable)) {
					query = "CREATE TABLE "
							+ digilockTable
							+ " (x INTEGER, y INTEGER, z INTEGER, world TEXT, owner TEXT,"
							+ " pincode TEXT,"
							+ " coowners TEXT, users TEXT, closetimer INTEGER,"
							+ " typeid INTEGER, connectedto TEXT, usecost INTEGER);";
					sqlite.createTable(query);
					Messages.showInfo("SQLite Table Created Successfully.");
				} else {
					Messages.showInfo("SQLite Table Loaded.");
				}
			} else {
				Messages.showInfo("SQLite Initialization failed!");
			}
		}
	}

	public MySQL getMySQLHandler() {
		return mysql;
	}

	public SQLite getSqliteHandler() {
		return sqlite;
	}

	public String getTableName() {
		return digilockTable;
	}
}
