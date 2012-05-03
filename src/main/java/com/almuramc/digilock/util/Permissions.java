package com.almuramc.digilock.util;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import org.getspout.spoutapi.player.SpoutPlayer;

import com.almuramc.digilock.Digilock;

public class Permissions {
	public static String PERMISSION_NODE;
	public final static Boolean QUIET = false;
	public final static Boolean NOT_QUIET = true;
	public static Permission permission = null;

	public static void setupPermissions(Plugin plugin) {
		PERMISSION_NODE = plugin.getDescription().getName() + ".";
		if (Digilock.getHooks().getEconHook() != null) {
			RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
			if (permissionProvider != null) {
				permission = permissionProvider.getProvider();
				Messages.showInfo("Vault is detected, using it for permissions.");
			}
		} else {
			Messages.showInfo("Vault not found, Defaulting to built-in permissions.");
		}
	}

	// Test if the player has permissions to do the action
	public static boolean hasPerm(CommandSender sender, String label, Boolean not_quiet) {

		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		Boolean hasPermission;

		if (permission != null) {
			hasPermission = permission.has(sender, PERMISSION_NODE + label);
		} else {
			// fallback builtin Permission system
			hasPermission = sPlayer.hasPermission((PERMISSION_NODE + label).toLowerCase());
		}

		if (hasPermission) {
			return true;
		} else if (not_quiet) {
			sPlayer.sendMessage(ChatColor.RED + "You to dont have permission to do this.");
		}

		return false;
	}
}
