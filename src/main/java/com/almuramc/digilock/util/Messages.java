package com.almuramc.digilock.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.almuramc.digilock.Digilock;

import org.getspout.spoutapi.player.SpoutPlayer;

import org.bukkit.Material;

public class Messages {
	public static final Logger l = Logger.getLogger("Minecraft");
	private static String PLUGIN_NAME = Digilock.getInstance().getName();

	public static void showInfo(String message) {
		l.log(Level.INFO, "[" + PLUGIN_NAME + "] " + message);
	}

	public static void showError(String message) {
		l.log(Level.SEVERE, "[" + PLUGIN_NAME + "] " + message);
	}

	public static void sendNotification(SpoutPlayer sPlayer, String string) {
		if (sPlayer.isSpoutCraftEnabled() && (sPlayer instanceof SpoutPlayer)) {
			if (string.length() < 25) {
				sPlayer.sendNotification(sPlayer.getName(), string, Material.LOCKED_CHEST);
			} else {
				sPlayer.sendNotification(sPlayer.getName(), string.substring(0, 25), Material.LOCKED_CHEST);
			}
		} else {
			sPlayer.sendMessage(string);
		}
	}
}
