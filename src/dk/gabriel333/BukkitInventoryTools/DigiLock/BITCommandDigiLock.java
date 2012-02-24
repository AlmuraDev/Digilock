package dk.gabriel333.BukkitInventoryTools.DigiLock;

import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.BITMessages;
import dk.gabriel333.Library.BITPermissions;

public class BITCommandDigiLock implements CommandExecutor {

	public BITCommandDigiLock(BIT plugin) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		SpoutBlock block = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
		if (BITDigiLock.isLockable(block)) {
			block = BITDigiLock.getDigiLockBlock(block);
			String pincode = "";
			String coowners = "";
			String users="";
			String connectedto = "";
			String owner = sPlayer.getName();
			Integer closetimer = 0; // never closes automatically
			int usecost = 0;
			if (!BIT.isPlayer(sPlayer)) {
				BITMessages
						.showError("You cant use this command in the console.");
				return false;
			} else if (BITPermissions.hasPerm(sPlayer, "digilock.create",
					BITPermissions.NOT_QUIET)
					|| BITPermissions.hasPerm(sPlayer, "digilock.use",
							BITPermissions.NOT_QUIET)
					|| BITPermissions.hasPerm(sPlayer, "digilock.admin",
							BITPermissions.NOT_QUIET)
					|| BITPermissions.hasPerm(sPlayer, "digilock.*",
							BITPermissions.NOT_QUIET)
					|| BITPermissions.hasPerm(sPlayer, "*",
							BITPermissions.NOT_QUIET)) {
				if (!BITDigiLock.isLocked(block)) {
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
								if (n + 1 <= args.length)
									closetimer = Integer
											.getInteger(args[n + 1],0);
								n++;
							} else if (action.equalsIgnoreCase("usecost")) {
								if (n + 1 <= args.length)
									usecost = Integer.getInteger(args[n + 1],0);
								n++;
							} else if (action.equalsIgnoreCase("coowners")) {
								if (n + 1 <= args.length)
									coowners = args[n + 1];
								n++;
							} else if (action.equalsIgnoreCase("connectedto")) {
								if (n + 1 <= args.length)
									connectedto = args[n + 1];
								n++;
							} else if (action.equalsIgnoreCase("remove")) {
								sPlayer.sendMessage("No Digilock is created on this bookshelf");
								return false;
							} else if (action.equalsIgnoreCase("info")) {
								sPlayer.sendMessage("No Digilock is created on this bookshelf");
								return false;
							}
						}
						if (BITPermissions.hasPerm(sPlayer, "digilock.create",
								BITPermissions.NOT_QUIET)
								&& args[0].equalsIgnoreCase("lock")) {
							BITDigiLock.SaveDigiLock(sPlayer, block, pincode,
									owner, closetimer, coowners, users,
									block.getTypeId(), connectedto, usecost);
							return true;
						} else {
							return false;
						}

					}
				} else { // digilock is locked
					BITDigiLock digilock = BITDigiLock.loadDigiLock(block);
					String action = args[0];
					// UNLOCK *************************************************
					if (action.equalsIgnoreCase("unlock") && args.length == 2){
						if (digilock.getPincode().equalsIgnoreCase(args[1])) {
							if (BITDigiLock.isChest(digilock.getBlock())) {
								SpoutChest sChest = (SpoutChest) block
										.getState();
								Inventory inv = sChest.getLargestInventory();
								sPlayer.openInventoryWindow(inv);
							} else if (BITDigiLock.isDoubleDoor(block)) {
								BITDigiLock.openDoubleDoor(sPlayer, block,
										digilock.getUseCost());
							} else if (BITDigiLock.isDoor(digilock.getBlock())) {
								BITDigiLock.openDoor(sPlayer, block,
										digilock.getUseCost());
							} else if (digilock.getBlock().getType() == Material.FURNACE) {
								Furnace furnace = (Furnace) block.getState();
								Inventory inv = furnace.getInventory();
								sPlayer.openInventoryWindow(inv);
							} else if (BITDigiLock.isDispenser(block)) {
								Dispenser dispenser = (Dispenser) block
										.getState();
								Inventory inv = dispenser.getInventory();
								sPlayer.openInventoryWindow(inv);
							} else if (BITDigiLock.isTrapdoor(block)) {
								BITDigiLock.openTrapdoor(sPlayer, block,
										digilock.getUseCost());
							} else if (BITDigiLock.isLever(block)) {
								if (BITDigiLock.isLeverOn(block)){
									BITDigiLock.leverOff(sPlayer, block);
								} else {
									if (BITDigiLock.isLeverOn(block)){
										BITDigiLock.leverOff(sPlayer, block);
									} else {
									BITDigiLock.leverOn(sPlayer, block, digilock.getUseCost());
									}
								}
							} else if (digilock.getBlock().getType() == Material.BOOKSHELF) {
								BITInventory bitInventory = BITInventory
										.loadBitInventory(sPlayer, block);
								bitInventory.openBitInventory(sPlayer,bitInventory);
							}
							
						} else {
							sPlayer.sendMessage("wrong pincode!");
							sPlayer.damage(5);
						}
						// REMOVE ***************************************
					} else if (action.equalsIgnoreCase("remove")
							&& (digilock.getOwner().equalsIgnoreCase(
									sPlayer.getName()) || BITPermissions
									.hasPerm(sPlayer, "digilock.admin",
											BITPermissions.NOT_QUIET))
							&& args.length == 1) {
						digilock.RemoveDigiLock(sPlayer);
						// closetimer ***************************************
					} else if (action.equalsIgnoreCase("closetimer")
							&& (digilock.getOwner().equalsIgnoreCase(
									sPlayer.getName()) || BITPermissions
									.hasPerm(sPlayer, "digilock.admin",
											BITPermissions.NOT_QUIET))
							&& args.length == 2) {
						digilock.setClosetimer(Integer.getInteger(args[1],0));
						BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
								digilock.getPincode(), digilock.getOwner(),
								digilock.getClosetimer(),
								digilock.getCoOwners(), digilock.getUsers(),
								digilock.getBlock().getTypeId(),
								digilock.getConnectedTo(),
								digilock.getUseCost());
						// addcoowner ***************************************
					} else if (action.equalsIgnoreCase("addcoowner")
							&& (digilock.getOwner().equalsIgnoreCase(
									sPlayer.getName()) || BITPermissions
									.hasPerm(sPlayer, "digilock.admin",
											BITPermissions.NOT_QUIET))
							&& args.length == 2) {
						digilock.addCoowner(args[1]);
						BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
								digilock.getPincode(), digilock.getOwner(),
								digilock.getClosetimer(),
								digilock.getCoOwners(), digilock.getUsers(),
								digilock.getBlock().getTypeId(),
								digilock.getConnectedTo(),
								digilock.getUseCost());
						// remcoowner ***************************************
					} else if (action.equalsIgnoreCase("remcoowner")
							&& (digilock.getOwner().equalsIgnoreCase(
									sPlayer.getName()) || BITPermissions
									.hasPerm(sPlayer, "digilock.admin",
											BITPermissions.NOT_QUIET))
							&& args.length == 2) {
						digilock.removeCoowner(args[1]);
						BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
								digilock.getPincode(), digilock.getOwner(),
								digilock.getClosetimer(),
								digilock.getCoOwners(), digilock.getUsers(),
								digilock.getBlock().getTypeId(),
								digilock.getConnectedTo(),
								digilock.getUseCost());
						// adduser ***************************************
					} else if (action.equalsIgnoreCase("adduser")
							&& (digilock.getOwner().equalsIgnoreCase(
									sPlayer.getName()) || BITPermissions
									.hasPerm(sPlayer, "digilock.admin",
											BITPermissions.NOT_QUIET))
							&& args.length == 2) {
						digilock.addUser(args[1]);
						BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
								digilock.getPincode(), digilock.getOwner(),
								digilock.getClosetimer(),
								digilock.getCoOwners(), digilock.getUsers(),
								digilock.getBlock().getTypeId(),
								digilock.getConnectedTo(),
								digilock.getUseCost());
						// remusers ***************************************
					} else if (action.equalsIgnoreCase("remuser")
							&& (digilock.getOwner().equalsIgnoreCase(
									sPlayer.getName()) || BITPermissions
									.hasPerm(sPlayer, "digilock.admin",
											BITPermissions.NOT_QUIET))
							&& args.length == 2) {
						digilock.removeUser(args[1]);
						BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
								digilock.getPincode(), digilock.getOwner(),
								digilock.getClosetimer(),
								digilock.getCoOwners(), digilock.getUsers(),
								digilock.getBlock().getTypeId(),
								digilock.getConnectedTo(),
								digilock.getUseCost());
						// usecost ***************************************
					} else if (action.equalsIgnoreCase("usecost")
							&& (digilock.getOwner().equalsIgnoreCase(
									sPlayer.getName()) || BITPermissions
									.hasPerm(sPlayer, "digilock.admin",
											BITPermissions.NOT_QUIET))
							&& args.length == 2) {
						digilock.setUseCost(Integer.getInteger(args[1],0));
						BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
								digilock.getPincode(), digilock.getOwner(),
								digilock.getClosetimer(),
								digilock.getCoOwners(), digilock.getUsers(),
								digilock.getBlock().getTypeId(),
								digilock.getConnectedTo(),
								digilock.getUseCost());
						// connectedto ***************************************
					} else if (action.equalsIgnoreCase("connectedto")
							&& (digilock.getOwner().equalsIgnoreCase(
									sPlayer.getName()) || BITPermissions
									.hasPerm(sPlayer, "digilock.admin",
											BITPermissions.NOT_QUIET))
							&& args.length == 2) {
						digilock.setConnectedTo(args[1]);
						BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
								digilock.getPincode(), digilock.getOwner(),
								digilock.getClosetimer(),
								digilock.getCoOwners(), digilock.getUsers(),
								digilock.getBlock().getTypeId(),
								digilock.getConnectedTo(),
								digilock.getUseCost());
						// INFO ***************************************
					} else if (action.equalsIgnoreCase("info")) {
						sPlayer.sendMessage("The owner of this lock is: "
								+ digilock.getOwner());
						sPlayer.sendMessage("The coOwner of this lock is: "
								+ digilock.getCoOwners());
						sPlayer.sendMessage("The users of this lock is: "
								+ digilock.getUsers());
						sPlayer.sendMessage("The cost is: "
								+ digilock.getUseCost() + " and closetimer is: "
								+ digilock.getClosetimer());
					} else if (digilock.getPincode().equalsIgnoreCase(args[0])
							&& args.length == 1) {
						if (BITDigiLock.isChest(digilock.getBlock())) {
							SpoutChest sChest = (SpoutChest) block.getState();
							Inventory inv = sChest.getLargestInventory();
							sPlayer.openInventoryWindow(inv);
						} else if (BITDigiLock
								.isDoubleDoor(digilock.getBlock())) {
							BITDigiLock.openDoubleDoor(sPlayer, block,
									digilock.getUseCost());

						} else if (BITDigiLock.isDoor(digilock.getBlock())) {
							BITDigiLock.openDoor(sPlayer, block,
									digilock.getUseCost());
						}  else if (digilock.getBlock().getType() == Material.FURNACE) {
							Furnace furnace = (Furnace) block.getState();
							Inventory inv = furnace.getInventory();
							sPlayer.openInventoryWindow(inv);
						} else if (BITDigiLock.isDispenser(block)) {
							Dispenser dispenser = (Dispenser) block
									.getState();
							Inventory inv = dispenser.getInventory();
							sPlayer.openInventoryWindow(inv);
						} else if (BITDigiLock.isTrapdoor(block)) {
							BITDigiLock.openTrapdoor(sPlayer, block,
									digilock.getUseCost());
						} else if (BITDigiLock.isLever(block)) {
							if (BITDigiLock.isLeverOn(block)){
								BITDigiLock.leverOff(sPlayer, block);
							} else {
							BITDigiLock.leverOn(sPlayer, block, digilock.getUseCost());
							}
						}
					} else {
						if (args.length == 1)
							sPlayer.damage(5);
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
			BITMessages.sendNotification(sPlayer,
					"You can't lock a " + block.getType());
			return false;
		}
	}
}
