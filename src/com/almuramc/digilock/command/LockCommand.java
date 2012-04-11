package com.almuramc.digilock.command;

import com.almuramc.digilock.LockCore;
import com.almuramc.digilock.util.BlockTools;
import com.almuramc.digilock.util.Messages;
import com.almuramc.digilock.util.Permissions;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class LockCommand implements CommandExecutor {
	public LockCommand(JavaPlugin instance) {
		instance.getCommand("lock").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		SpoutBlock block = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
		if (BlockTools.isLockable(block)) {
			block = BlockTools.getDigiLockBlock(block);
			String pincode = "";
			String coowners = "";
			String users = "";
			String connectedto = "";
			String owner = sPlayer.getName();
			Integer closetimer = 0; // never closes automatically
			int usecost = 0;
			if (sender instanceof Player) {
				Messages.showError("You cant use this command in the console.");
				return false;
			} else if (Permissions.hasPerm(sPlayer, "lock.create", Permissions.NOT_QUIET) || Permissions.hasPerm(sPlayer, "lock.use", Permissions.NOT_QUIET) || Permissions.hasPerm(sPlayer, "lock.admin", Permissions.NOT_QUIET) || Permissions.hasPerm(sPlayer, "lock.*", Permissions.NOT_QUIET) || Permissions.hasPerm(sPlayer, "*", Permissions.NOT_QUIET)) {
				if (!BlockTools.isLocked(block)) {
					if (args.length == 0) {
						return false;
					} else {
						String action;
						// LOCK
						// ***********************************************************
						for (int n = 0; n < args.length; n++) {
							action = args[n];
							if (action.equalsIgnoreCase("lock")) {
								if (n + 1 <= args.length) {
									pincode = args[n + 1];
								}
								n++;
							} else if (action.equalsIgnoreCase("owner")) {
								if (n + 1 <= args.length) {
									owner = args[n + 1];
								}
								n++;
							} else if (action.equalsIgnoreCase("closetimer")) {
								if (n + 1 <= args.length) {
									closetimer = Integer
											.getInteger(args[n + 1], 0);
								}
								n++;
							} else if (action.equalsIgnoreCase("usecost")) {
								if (n + 1 <= args.length) {
									usecost = Integer.getInteger(args[n + 1], 0);
								}
								n++;
							} else if (action.equalsIgnoreCase("coowners")) {
								if (n + 1 <= args.length) {
									coowners = args[n + 1];
								}
								n++;
							} else if (action.equalsIgnoreCase("connectedto")) {
								if (n + 1 <= args.length) {
									connectedto = args[n + 1];
								}
								n++;
							}
						}
						if (Permissions.hasPerm(sPlayer, "lock.create", Permissions.NOT_QUIET) && args[0].equalsIgnoreCase("lock")) {
							LockCore.SaveDigiLock(sPlayer, block, pincode, owner, closetimer, coowners, users, block.getTypeId(), connectedto, usecost);
							return true;
						} else {
							return false;
						}
					}
				} else { // lock is locked
					LockCore lock = BlockTools.loadDigiLock(block);
					String action = args[0];
					// UNLOCK *************************************************
					if (action.equalsIgnoreCase("unlock") && args.length == 2) {
						if (lock.getPincode().equalsIgnoreCase(args[1])) {
							if (BlockTools.isChest(lock.getBlock())) {
								Chest sChest = (Chest) block.getState();
								Inventory inv = sChest.getInventory();
								sPlayer.openInventory(inv);
							} else if (BlockTools.isDoubleDoor(block)) {
								BlockTools.openDoubleDoor(sPlayer, block, lock.getUseCost());
							} else if (BlockTools.isDoor(lock.getBlock())) {
								BlockTools.openDoor(sPlayer, block, lock.getUseCost());
							} else if (lock.getBlock().getType() == Material.FURNACE) {
								Furnace furnace = (Furnace) block.getState();
								Inventory inv = furnace.getInventory();
								sPlayer.openInventory(inv);
							} else if (BlockTools.isDispenser(block)) {
								Dispenser dispenser = (Dispenser) block .getState();
								Inventory inv = dispenser.getInventory();
								sPlayer.openInventory(inv);
							} else if (BlockTools.isTrapdoor(block)) {
								BlockTools.openTrapdoor(sPlayer, block, lock.getUseCost());
							} else if (BlockTools.isLever(block)) {
								if (BlockTools.isLeverOn(block)) {
									BlockTools.leverOff(sPlayer, block);
								} else {
									if (BlockTools.isLeverOn(block)) {
										BlockTools.leverOff(sPlayer, block);
									} else {
										BlockTools.leverOn(sPlayer, block, lock.getUseCost());
									}
								}
							}
						} else {
							sPlayer.sendMessage("wrong pincode!");
							sPlayer.damage(5);
						}
						// REMOVE ***************************************
					} else if (action.equalsIgnoreCase("remove") && (lock.getOwner().equalsIgnoreCase(sPlayer.getName()) || Permissions.hasPerm(sPlayer, "lock.admin", Permissions.NOT_QUIET)) && args.length == 1) {
						lock.RemoveDigiLock(sPlayer);
						// closetimer ***************************************
					} else if (action.equalsIgnoreCase("closetimer") && (lock.getOwner().equalsIgnoreCase(sPlayer.getName()) || Permissions.hasPerm(sPlayer, "lock.admin", Permissions.NOT_QUIET)) && args.length == 2) {
						lock.setClosetimer(Integer.getInteger(args[1], 0));
						LockCore.SaveDigiLock(sPlayer, lock.getBlock(), lock.getPincode(), lock.getOwner(), lock.getClosetimer(), lock.getCoOwners(), lock.getUsers(), lock.getBlock().getTypeId(), lock.getConnectedTo(), lock.getUseCost());
						// addcoowner ***************************************
					} else if (action.equalsIgnoreCase("addcoowner") && (lock.getOwner().equalsIgnoreCase(sPlayer.getName()) || Permissions.hasPerm(sPlayer, "lock.admin", Permissions.NOT_QUIET)) && args.length == 2) {
						lock.addCoowner(args[1]);
						LockCore.SaveDigiLock(sPlayer, lock.getBlock(), lock.getPincode(), lock.getOwner(), lock.getClosetimer(), lock.getCoOwners(), lock.getUsers(), lock.getBlock().getTypeId(), lock.getConnectedTo(), lock.getUseCost());
						// remcoowner ***************************************
					} else if (action.equalsIgnoreCase("remcoowner")
							&& (lock.getOwner().equalsIgnoreCase(
							sPlayer.getName()) || Permissions
							.hasPerm(sPlayer, "lock.admin",
									Permissions.NOT_QUIET))
							&& args.length == 2) {
						lock.removeCoowner(args[1]);
						LockCore.SaveDigiLock(sPlayer, lock.getBlock(),
								lock.getPincode(), lock.getOwner(),
								lock.getClosetimer(),
								lock.getCoOwners(), lock.getUsers(),
								lock.getBlock().getTypeId(),
								lock.getConnectedTo(),
								lock.getUseCost());
						// adduser ***************************************
					} else if (action.equalsIgnoreCase("adduser")
							&& (lock.getOwner().equalsIgnoreCase(
							sPlayer.getName()) || Permissions
							.hasPerm(sPlayer, "lock.admin",
									Permissions.NOT_QUIET))
							&& args.length == 2) {
						lock.addUser(args[1]);
						LockCore.SaveDigiLock(sPlayer, lock.getBlock(),
								lock.getPincode(), lock.getOwner(),
								lock.getClosetimer(),
								lock.getCoOwners(), lock.getUsers(),
								lock.getBlock().getTypeId(),
								lock.getConnectedTo(),
								lock.getUseCost());
						// remusers ***************************************
					} else if (action.equalsIgnoreCase("remuser")
							&& (lock.getOwner().equalsIgnoreCase(
							sPlayer.getName()) || Permissions
							.hasPerm(sPlayer, "lock.admin",
									Permissions.NOT_QUIET))
							&& args.length == 2) {
						lock.removeUser(args[1]);
						LockCore.SaveDigiLock(sPlayer, lock.getBlock(),
								lock.getPincode(), lock.getOwner(),
								lock.getClosetimer(),
								lock.getCoOwners(), lock.getUsers(),
								lock.getBlock().getTypeId(),
								lock.getConnectedTo(),
								lock.getUseCost());
						// usecost ***************************************
					} else if (action.equalsIgnoreCase("usecost")
							&& (lock.getOwner().equalsIgnoreCase(
							sPlayer.getName()) || Permissions
							.hasPerm(sPlayer, "lock.admin",
									Permissions.NOT_QUIET))
							&& args.length == 2) {
						lock.setUseCost(Integer.getInteger(args[1], 0));
						LockCore.SaveDigiLock(sPlayer, lock.getBlock(),
								lock.getPincode(), lock.getOwner(),
								lock.getClosetimer(),
								lock.getCoOwners(), lock.getUsers(),
								lock.getBlock().getTypeId(),
								lock.getConnectedTo(),
								lock.getUseCost());
						// connectedto ***************************************
					} else if (action.equalsIgnoreCase("connectedto")
							&& (lock.getOwner().equalsIgnoreCase(
							sPlayer.getName()) || Permissions
							.hasPerm(sPlayer, "lock.admin",
									Permissions.NOT_QUIET))
							&& args.length == 2) {
						lock.setConnectedTo(args[1]);
						LockCore.SaveDigiLock(sPlayer, lock.getBlock(),
								lock.getPincode(), lock.getOwner(),
								lock.getClosetimer(),
								lock.getCoOwners(), lock.getUsers(),
								lock.getBlock().getTypeId(),
								lock.getConnectedTo(),
								lock.getUseCost());
						// INFO ***************************************
					} else if (action.equalsIgnoreCase("info")) {
						sPlayer.sendMessage("The owner of this lock is: "
								+ lock.getOwner());
						sPlayer.sendMessage("The coOwner of this lock is: "
								+ lock.getCoOwners());
						sPlayer.sendMessage("The users of this lock is: "
								+ lock.getUsers());
						sPlayer.sendMessage("The cost is: "
								+ lock.getUseCost() + " and closetimer is: "
								+ lock.getClosetimer());
					} else if (lock.getPincode().equalsIgnoreCase(args[0])
							&& args.length == 1) {
						if (BlockTools.isChest(lock.getBlock())) {
							Chest sChest = (Chest) block.getState();
							Inventory inv = sChest.getInventory();
							sPlayer.openInventory(inv);
						} else if (BlockTools.isDoubleDoor(lock.getBlock())) {
							BlockTools.openDoubleDoor(sPlayer, block,
									lock.getUseCost());
						} else if (BlockTools.isDoor(lock.getBlock())) {
							BlockTools.openDoor(sPlayer, block,
									lock.getUseCost());
						} else if (lock.getBlock().getType() == Material.FURNACE) {
							Furnace furnace = (Furnace) block.getState();
							Inventory inv = furnace.getInventory();
							sPlayer.openInventory(inv);
						} else if (BlockTools.isDispenser(block)) {
							Dispenser dispenser = (Dispenser) block
									.getState();
							Inventory inv = dispenser.getInventory();
							sPlayer.openInventory(inv);
						} else if (BlockTools.isTrapdoor(block)) {
							BlockTools.openTrapdoor(sPlayer, block,
									lock.getUseCost());
						} else if (BlockTools.isLever(block)) {
							if (BlockTools.isLeverOn(block)) {
								BlockTools.leverOff(sPlayer, block);
							} else {
								BlockTools.leverOn(sPlayer, block, lock.getUseCost());
							}
						}
					} else {
						if (args.length == 1) {
							sPlayer.damage(5);
						}
						sPlayer.sendMessage("Wrong pincode!");
						return false;
					}
				}
				return true;
			}
			// No permissions
			return false;
		} else {
			//Not lockable
			Messages.sendNotification(sPlayer, "You can't lock a " + block.getType());
			return false;
		}
	}
}
