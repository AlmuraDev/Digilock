package com.almuramc.digilock.util;

import com.almuramc.digilock.Digilock;

import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;

import org.bukkit.plugin.Plugin;

public class SQLHandler {
	private Plugin plugin;
	private MySQL mysql;
	private SQLite sqlite;
	private String digilockTable = "Digilock";

	public SQLHandler(Plugin instance) {
		plugin = instance;
		setupSQL();
	}

	private void setupSQL() {
		if (Digilock.getConfig().getSQLType().equals("MYSQL")) {
			mysql = new MySQL(plugin.getLogger(), "[" + plugin.getName() + "]", Digilock.getConfig().getSQLHost(), Digilock.getConfig().getSQLPort(), Digilock.getConfig().getSQLDatabase(), Digilock.getConfig().getSQLUsername(), Digilock.getConfig().getSQLPassword());
			if (mysql.checkConnection()) {
				//Check if the Connection was successful
				String query;

				if (!mysql.checkTable(digilockTable)) {
					query = "CREATE TABLE "
							+ digilockTable
							+ " (x INT, y INT, z INT, world VARCHAR(255), owner VARCHAR(255), "
							+ "pincode VARCHAR(255), coowners VARCHAR(255), users VARCHAR(255), closetimer INT, "
							+ "typeid INT, connectedto VARCHAR(255), usecost INT);";
					mysql.createTable(query);
				}
			}
		} else if (Digilock.getConfig().getSQLType().equals("SQLITE")) {
			try {
				sqlite = new SQLite(plugin.getLogger(), "[" + plugin.getName() + "]", plugin.getName(), plugin.getDataFolder().getCanonicalPath());
			} catch (Exception e) {

			}
			String query;
			if (!sqlite.checkTable(digilockTable)) {
				query = "CREATE TABLE "
						+ digilockTable
						+ " (x INTEGER, y INTEGER, z INTEGER, world TEXT, owner TEXT,"
						+ " pincode TEXT,"
						+ " coowners TEXT, users TEXT, closetimer INTEGER,"
						+ " typeid INTEGER, connectedto TEXT, usecost INTEGER);";
				sqlite.createTable(query);
			}
		} else {
			return;
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
