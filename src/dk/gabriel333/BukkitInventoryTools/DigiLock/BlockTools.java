/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.gabriel333.BukkitInventoryTools.DigiLock;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BlockTools {
	private BlockTools() {
	}

	private static final Material lockablematerials[] = {Material.CHEST,
			Material.LOCKED_CHEST, Material.IRON_DOOR,
			Material.IRON_DOOR_BLOCK, Material.WOODEN_DOOR, Material.WOOD_DOOR,
			Material.FURNACE, Material.DISPENSER, Material.LEVER,
			Material.STONE_BUTTON, Material.BOOKSHELF, Material.TRAP_DOOR,
			Material.SIGN, Material.SIGN_POST, Material.WALL_SIGN,
			Material.FENCE_GATE, Material.JUKEBOX,
			Material.BREWING_STAND
	};

	// check if material is a lockable block

	/**
	 * Check if the Block is made of a lockable material.
	 * @param block
	 * @return true or false
	 */
	public static boolean isLockable(Block block) {
		if (block != null) {
			for (Material i : lockablematerials) {
				if (i == block.getType()) {
					return true;
				}
			}
		}
		return false;
	}

	static boolean isLockable(Material material) {
		if (material != null) {
			for (Material i : lockablematerials) {
				if (i == material) {
					return true;
				}
			}
		}
		return false;
	}

	public static BITDigiLock loadDigiLock(SpoutBlock block) {
		// TODO: fasten up the load of DigiLock, select from a HASHMAP second
		// time
		block = getDigiLockBlock(block);
		String query = "SELECT * FROM " + BIT.digilockTable + " WHERE (x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
				+ "');";
		ResultSet result = null;
		if (BITConfig.STORAGE_TYPE.equals("MYSQL")) {
			try {
				result = BIT.manageMySQL.sqlQuery(query);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else { // SQLLITE
			result = BIT.manageSQLite.sqlQuery(query);
		}

		try {
			if (result != null && result.next()) {
				String pincode = result.getString("pincode");
				String owner = result.getString("owner");
				int closetimer = result.getInt("closetimer");
				String coowners = result.getString("coowners");
				String users = result.getString("users");
				int typeId = result.getInt("typeId");
				String connectedTo = result.getString("connectedto");
				int useCost = result.getInt("usecost");
				BITDigiLock digilock = new BITDigiLock(block, pincode, owner,
						closetimer, coowners, users, typeId, connectedTo,
						useCost);
				return digilock;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if the block is locked
	 * @param block
	 * @return true if it is locked, false if not
	 */
	public static Boolean isLocked(SpoutBlock block) {
		// TODO: Implement a HASHMAP for testing if the block is locked.
		// BITMessages.showInfo("isLocked was called");
		if (block != null) {
			if (isLockable(block)) {
				block = getDigiLockBlock(block);
				String query = "SELECT * FROM " + BIT.digilockTable
						+ " WHERE (x = " + block.getX() + " AND y = "
						+ block.getY() + " AND z = " + block.getZ()
						+ " AND world='" + block.getWorld().getName() + "');";
				ResultSet result = null;
				if (BITConfig.STORAGE_TYPE.equals("MYSQL")) {
					try {
						result = BIT.manageMySQL.sqlQuery(query);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else { // SQLLITE
					result = BIT.manageSQLite.sqlQuery(query);
				}
				try {
					if (result != null && result.next()) {
						return true;
					} else {
						return false;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * economy to find the the next block which is connected to a lever or a
	 * stonebutton.
	 * @param sBlock SpoutBlock
	 * @return SpoutBlock
	 */
	public static SpoutBlock getDigiLockBlock(SpoutBlock sBlock) {
		if (isDoor(sBlock)) {
			if (isDoubleDoor(sBlock)) {
				sBlock = getLeftDoubleDoor(sBlock);
			}
			Door door = (Door) sBlock.getState().getData();
			if (door.isTopHalf()) {
				sBlock = sBlock.getRelative(BlockFace.DOWN);
			}
		} else if (isChest(sBlock) && (sBlock.getState() instanceof Chest)) {
			Chest sChest1 = (Chest) sBlock.getState();
			if (sChest1.getInventory() instanceof DoubleChestInventory) {
				Chest sChest2;
				DoubleChestInventory di = (DoubleChestInventory) sChest1.getInventory();
				if (sChest1.getBlockInventory().equals(di.getLeftSide())) {
					sChest2 = (Chest) di.getRightSide().getHolder();
				} else {
					sChest2 = (Chest) di.getLeftSide().getHolder();
				}
				SpoutBlock sBlock2 = (SpoutBlock) sChest2.getBlock();

				if (sChest1.getX() == sChest2.getX()) {
					if (sChest1.getZ() < sChest2.getZ()) {
						return sBlock;
					} else {
						return sBlock2;
					}
				} else {
					if (sChest1.getX() < sChest2.getX()) {
						return sBlock;
					} else {
						return sBlock2;
					}
				}
			}
		}
		return sBlock;
	}

	// Dockter 12/27/11, Added check to see if global sound is on or off.
	public static void playDigiLockSound(SpoutBlock sBlock) {
		if (BITConfig.DIGILOCK_SOUND_ON) {
			SpoutManager
					.getSoundManager()
					.playGlobalCustomSoundEffect(
							BIT.plugin,
							BITConfig.DIGILOCK_SOUND,
							true, sBlock.getLocation(), 5);
		}
	}

	public static boolean isNeighbourLocked(SpoutBlock block) {
		if (block != null) {
			for (BlockFace bf : BlockFace.values()) {
				if (isLocked(block.getRelative(bf))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isNeighbourSameOwner(SpoutBlock block, String owner) {
		for (BlockFace bf : BlockFace.values()) {
			if (isLocked(block.getRelative(bf))) {
				BITDigiLock digilock = loadDigiLock(block
						.getRelative(bf));
				if (digilock.getOwner().equalsIgnoreCase(owner)) {
					return true;
				}
			}
		}
		return false;
	}

	// *******************************************************
	//
	// CHEST
	//
	// *******************************************************
	public static boolean isChest(Block block) {
		if (block != null) {
			if (block.getType().equals(Material.CHEST)
					|| block.getType().equals(Material.LOCKED_CHEST)) {
				return true;
			}
		}
		return false;
	}

	// *******************************************************
	//
	// SIGN
	//
	// *******************************************************
	public static boolean isSign(Block block) {
		if (block != null) {
			if (block.getType().equals(Material.SIGN)
					|| block.getType().equals(Material.WALL_SIGN)
					|| block.getType().equals(Material.SIGN_POST)) {
				return true;
			}
		}
		return false;
	}

	// *******************************************************
	//
	// BOOKSHELF
	//
	// *******************************************************
	public static boolean isBookshelf(SpoutBlock sBlock) {
		if (sBlock != null) {
			if (sBlock.getType().equals(Material.BOOKSHELF)) {
				return true;
			}
		}
		return false;
	}

	// *******************************************************
	//
	// STONE_BUTTON
	//
	// *******************************************************

	/**
	 * Check if sBlock is a STONE_BUTTON
	 * @param sBlock
	 * @return true or false
	 */
	public static boolean isButton(SpoutBlock sBlock) {
		if (sBlock != null) {
			if (sBlock.getType().equals(Material.STONE_BUTTON)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the STONE_BUTTON is on.
	 * @param block
	 * @return
	 */
	public static boolean isButtonOn(SpoutBlock block) {
		Button button = (Button) block.getState().getData();
		return button.isPowered();
	}

	/**
	 * Handle the actions when a player presses the STONE_BUTTON
	 * @param sPlayer SpoutPlayer
	 * @param sBlock  SpoutBlock
	 * @param cost	the cost the player is charged when the button is pressed.
	 */
	public static void pressButtonOn(SpoutPlayer sPlayer, SpoutBlock sBlock,
									 int cost) {
		boolean doTheWork = false;
		BITDigiLock digilock = loadDigiLock(sBlock);
		SpoutBlock nextBlock = digilock.getNextLockedBlock(sPlayer, sBlock);
		if (nextBlock != null) {
			BITDigiLock nextDigilock = loadDigiLock(nextBlock);
			if (digilock.getOwner().equalsIgnoreCase(nextDigilock.getOwner())) {
				doTheWork = true;
			} else {
				sPlayer.sendMessage("You are not the owner of the "
						+ nextBlock.getType());
			}
			boolean pressButton = true;
			if (BIT.useEconomy
					&& cost > 0
					&& doTheWork
					&& digilock.isUser(sPlayer)
					&& !(digilock.isOwner(sPlayer) || digilock
					.isCoowner(sPlayer))) {
				if (BIT.plugin.economy.hasAccount(sPlayer.getName())) {
					if (BIT.plugin.economy.has(sPlayer.getName(), cost)) {
						BIT.plugin.economy.withdrawPlayer(sPlayer.getName(), cost);
						if (BIT.plugin.economy.hasAccount(nextDigilock.getOwner())) {
							BIT.plugin.economy.depositPlayer(nextDigilock.getOwner(), cost);
						}

						sPlayer.sendMessage("Your account ("
								+ BIT.plugin.economy.getBalance(
								sPlayer.getName())
								+ ") has been deducted "
								+ BIT.plugin.economy.format(cost) + ".");
					} else {
						sPlayer.sendMessage("You dont have enough money ("
								+ BIT.plugin.economy.getBalance(
								sPlayer.getName())
								+ "). Cost is:"
								+ BIT.plugin.economy.format(cost));
						pressButton = false;
					}
				}
			}
			if (pressButton && doTheWork) {
				Button button = (Button) sBlock.getState().getData();
				button.setPowered(true);
				// x | 8 ^ 8 = 0
				// sBlock.setData((byte) ((lever.getData() | 8) ^ 8));
				if (isDoubleDoor(nextBlock)) {
					openDoubleDoor(sPlayer, nextBlock, 0);
					scheduleCloseDoubleDoor(sPlayer, nextBlock, 5,
							0);
				} else if (isDoor(nextBlock)) {
					openDoor(sPlayer, nextBlock, 0);
					scheduleCloseDoor(sPlayer, nextBlock, 5, 0);
				} else if (isTrapdoor(nextBlock)) {
					openTrapdoor(sPlayer, nextBlock, 0);
					scheduleCloseTrapdoor(sPlayer, nextBlock, 5);
				} else if (isDispenser(nextBlock)) {
					Dispenser dispenser = (Dispenser) nextBlock.getState();
					dispenser.dispense();
				}
			}
		}
	}

	/**
	 * Check if sBlock is a DISPENSER.
	 * @param sBlock
	 * @return
	 */
	public static boolean isDispenser(SpoutBlock sBlock) {
		if (sBlock != null) {
			if (sBlock.getType().equals(Material.DISPENSER)) {
				return true;
			}
		}
		return false;
	}

	// *******************************************************
	//
	// DOORS
	//
	// *******************************************************

	/**
	 * Check if sBlock is a LEVER.
	 * @param sBlock
	 * @return
	 */
	public static boolean isLever(SpoutBlock sBlock) {
		if (sBlock != null) {
			if (sBlock.getType().equals(Material.LEVER)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the LEVER on - on sBlock.
	 * @param sBlock
	 * @return
	 */
	public static boolean isLeverOn(SpoutBlock sBlock) {
		Lever lever = (Lever) sBlock.getState().getData();
		return lever.isPowered();
	}

	public static void leverOn(SpoutPlayer sPlayer, SpoutBlock sBlock, int cost) {
		boolean doTheWork = false;
		BITDigiLock digilock = loadDigiLock(sBlock);
		if (digilock != null) {
			SpoutBlock nextBlock = digilock.getNextLockedBlock(sPlayer, sBlock);
			if (nextBlock != null) {
				BITDigiLock nextDigilock = loadDigiLock(nextBlock);

				if (digilock.getOwner().equalsIgnoreCase(
						nextDigilock.getOwner())) {
					doTheWork = true;
				} else {
					sPlayer.sendMessage("You are not the owner of the "
							+ nextBlock.getType());
				}
				boolean setleveron = true;
				if (BIT.useEconomy
						&& cost > 0
						&& doTheWork
						&& digilock.isUser(sPlayer)
						&& !(digilock.isOwner(sPlayer) || digilock
						.isCoowner(sPlayer))) {
					if (BIT.plugin.economy.hasAccount(sPlayer.getName())) {
						if (BIT.plugin.economy.has(sPlayer.getName(), cost)) {
							BIT.plugin.economy.withdrawPlayer(sPlayer.getName(), cost);
							if (BIT.plugin.economy.hasAccount(nextDigilock
									.getOwner())) {
								BIT.plugin.economy.depositPlayer(
										nextDigilock.getOwner(), cost);
							}
							sPlayer.sendMessage("Your account ("
									+ BIT.plugin.economy.getBalance(
									sPlayer.getName())
									+ ") has been deducted "
									+ BIT.plugin.economy.format(cost) + ".");
						} else {
							sPlayer.sendMessage("You dont have enough money ("
									+ BIT.plugin.economy.getBalance(
									sPlayer.getName())
									+ "). Cost is:"
									+ BIT.plugin.economy.format(cost));
							setleveron = false;
						}
					}
				}
				if (setleveron && doTheWork) {

					Lever lever = (Lever) sBlock.getState().getData();
					lever.setPowered(true);
					// x | 8 ^ 8 = 0
					// sBlock.setData((byte) ((lever.getData() | 8) ^ 8));

					if (isDoubleDoor(nextBlock)) {
						openDoubleDoor(sPlayer, nextBlock, 0);
					} else if (isDoor(nextBlock)) {
						openDoor(sPlayer, nextBlock, 0);
					} else if (isTrapdoor(nextBlock)) {
						openTrapdoor(sPlayer, nextBlock, 0);
					} else if (nextBlock.getType().equals(Material.DISPENSER)) {
						Dispenser dispenser = (Dispenser) nextBlock.getState();
						dispenser.dispense();
					}
					if (digilock.getClosetimer() > 0) {
						scheduleLeverOff(sPlayer, sBlock,
								digilock.getClosetimer());
					}
				}
			}
		}
	}

	public static void leverOff(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		if (isLeverOn(sBlock)) {
			BITDigiLock digilock = loadDigiLock(sBlock);
			if (digilock != null) {
				SpoutBlock nextBlock = digilock.getNextLockedBlock(sPlayer,
						sBlock);
				if (nextBlock != null) {
					BITDigiLock nextDigilock = loadDigiLock(nextBlock);
					if (digilock.getOwner().equalsIgnoreCase(
							nextDigilock.getOwner())) {
						Lever lever = (Lever) sBlock.getState().getData();
						lever.setPowered(false);
						// sBlock.setData((byte) (lever.getData() |8));
						if (isDoubleDoor(nextBlock)) {
							closeDoubleDoor(sPlayer, nextBlock, 0);
						} else if (isDoor(nextBlock)) {
							closeDoor(sPlayer, nextBlock, 0);
						} else if (isTrapdoor(nextBlock)) {
							closeTrapdoor(sPlayer, nextBlock);
						}
					} else {
						sPlayer.sendMessage("You are not the owner of the "
								+ nextBlock.getType());
					}
				}
			}
		}
	}

	//
	public static int scheduleLeverOff(final SpoutPlayer sPlayer,
									   final SpoutBlock sBlock, final int closetimer) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = BIT.plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(BIT.plugin, new Runnable() {
					@Override
					public void run() {
						SpoutBlock sb = sBlock;
						SpoutPlayer sp = sPlayer;
						if (BITConfig.DEBUG_DOOR) {
							sp.sendMessage("Turning lever off in " + closetimer
									+ " seconds");
						}
						if (isLeverOn(sBlock)) {
							leverOff(sp, sb);
						}
					}
				}, fs);
		return taskID;
	}

	// *******************************************************
	//
	// DOORS
	//
	// *******************************************************

	public static boolean isDoor(Block block) {
		if (block != null) {
			if (block.getType().equals(Material.WOOD_DOOR)) {
				return true;
			} else if (block.getType().equals(Material.WOODEN_DOOR)) {
				return true;
			} else if (block.getType().equals(Material.IRON_DOOR)) {
				return true;
			} else if (block.getType().equals(Material.IRON_DOOR_BLOCK)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isDoorOpen(SpoutBlock sBlock) {
		if ((sBlock.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openDoor(SpoutPlayer sPlayer, SpoutBlock sBlock, int cost) {
		boolean opendoor = true;
		BITDigiLock digilock = loadDigiLock(sBlock);
		if (BIT.useEconomy && cost > 0 && digilock.isUser(sPlayer)
				&& !(digilock.isOwner(sPlayer) || digilock.isCoowner(sPlayer))) {
			if (BIT.plugin.economy.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.economy.has(sPlayer.getName(),
						cost)) {
					BIT.plugin.economy.withdrawPlayer(sPlayer.getName(),
							cost);
					if (BIT.plugin.economy.hasAccount(digilock.getOwner())) {
						BIT.plugin.economy.depositPlayer(digilock.getOwner(),
								cost);
					}
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.economy.getBalance(sPlayer.getName())
							+ ") has been deducted "
							+ BIT.plugin.economy.format(cost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.economy.getBalance(sPlayer.getName())
							+ "). Cost is:"
							+ BIT.plugin.economy.format(cost));
					opendoor = false;
				}
			}
		}
		Door door = (Door) sBlock.getState().getData();
		SpoutBlock nextBlock;
		if (opendoor) {
			if (!isDoorOpen(sBlock)) {
				playDigiLockSound(sBlock);
				sBlock.setData((byte) (sBlock.getState().getData().getData() | 4));
				if (door.isTopHalf()) {
					nextBlock = sBlock.getRelative(BlockFace.DOWN);
					nextBlock.setData((byte) (nextBlock.getState().getData()
							.getData() | 4));
				} else {
					nextBlock = sBlock.getRelative(BlockFace.UP);
					nextBlock.setData((byte) (nextBlock.getState().getData()
							.getData() | 4));
				}
				if (digilock != null) {
					if (digilock.getClosetimer() > 0 && !isDoubleDoor(sBlock)) {
						scheduleCloseDoor(sPlayer, sBlock,
								digilock.getClosetimer(), 0);
					}
				}
			}
		}
	}

	public static void closeDoor(SpoutPlayer sPlayer, SpoutBlock sBlock,
								 int cost) {
		boolean closedoor = true;
		BITDigiLock digilock = loadDigiLock(sBlock);
		if (BIT.useEconomy && cost > 0 && digilock.isUser(sPlayer)
				&& !(digilock.isOwner(sPlayer) || digilock.isCoowner(sPlayer))) {
			if (BIT.plugin.economy.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.economy.has(sPlayer.getName(),
						cost)) {
					BIT.plugin.economy.withdrawPlayer(sPlayer.getName(),
							cost);
					if (BIT.plugin.economy.hasAccount(digilock.getOwner())) {
						BIT.plugin.economy.depositPlayer(digilock.getOwner(),
								cost);
					}

					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.economy.getBalance(sPlayer.getName())
							+ ") has been deducted "
							+ BIT.plugin.economy.format(cost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.economy.getBalance(sPlayer.getName())
							+ "). Cost is:"
							+ BIT.plugin.economy.format(cost));
					closedoor = false;
				}
			}
		}
		if (closedoor) {
			if (isDoorOpen(sBlock)) {
				playDigiLockSound(sBlock);
				Door door = (Door) sBlock.getState().getData();
				SpoutBlock nextBlock;
				sBlock.setData((byte) ((sBlock.getState().getData().getData() | 4) ^ 4));
				if (door.isTopHalf()) {
					nextBlock = sBlock.getRelative(BlockFace.DOWN);
					nextBlock.setData((byte) ((nextBlock.getState().getData()
							.getData() | 4) ^ 4));
				} else {
					nextBlock = sBlock.getRelative(BlockFace.UP);
					nextBlock.setData((byte) ((nextBlock.getState().getData()
							.getData() | 4) ^ 4));
				}
			}
		}
	}

	public static void closeDoor(SpoutBlock sBlock) {
		if (isDoorOpen(sBlock)) {
			playDigiLockSound(sBlock);
			Door door = (Door) sBlock.getState().getData();
			SpoutBlock nextBlock;
			sBlock.setData((byte) ((sBlock.getState().getData().getData() | 4) ^ 4));
			if (door.isTopHalf()) {
				nextBlock = sBlock.getRelative(BlockFace.DOWN);
				nextBlock.setData((byte) ((nextBlock.getState().getData()
						.getData() | 4) ^ 4));
			} else {
				nextBlock = sBlock.getRelative(BlockFace.UP);
				nextBlock.setData((byte) ((nextBlock.getState().getData()
						.getData() | 4) ^ 4));
			}
		}
	}

	public static void toggleDoor(SpoutBlock sBlock) {
		playDigiLockSound(sBlock);
		Door door = (Door) sBlock.getState().getData();
		SpoutBlock nextBlock;
		sBlock.setData((byte) (sBlock.getState().getData().getData() ^ 4));
		if (door.isTopHalf()) {
			nextBlock = sBlock.getRelative(BlockFace.DOWN);
			nextBlock
					.setData((byte) (nextBlock.getState().getData().getData() ^ 4));
		} else {
			nextBlock = sBlock.getRelative(BlockFace.UP);
			nextBlock
					.setData((byte) (nextBlock.getState().getData().getData() ^ 4));
		}
	}

	public static int scheduleCloseDoor(final SpoutPlayer sPlayer,
										final SpoutBlock sBlock, final int closetimer, final int cost) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = BIT.plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(BIT.plugin, new Runnable() {
					@Override
					public void run() {
						SpoutBlock sb = sBlock;
						SpoutPlayer sp = sPlayer;
						int c = cost;
						if (BITConfig.DEBUG_DOOR) {
							sp.sendMessage("Autoclosing the door in "
									+ closetimer + " seconds");
						}
						if (isDoor(sb) && !isDoubleDoor(sb)) {
							if (isDoorOpen(sb)) {
								closeDoor(sp, sb, c);
								playDigiLockSound(sBlock);
							}
						}
					}
				}, fs);
		return taskID;
	}

	// *******************************************************
	//
	// TRAPDOORS
	//
	// *******************************************************

	public static boolean isTrapdoor(Block block) {
		if (block != null) {
			if (block.getType().equals(Material.TRAP_DOOR)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTrapdoorOpen(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		if ((sBlock.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openTrapdoor(SpoutPlayer sPlayer, SpoutBlock sBlock,
									int cost) {
		boolean opentrapdoor = true;
		BITDigiLock digilock = loadDigiLock(sBlock);
		if (BIT.useEconomy && cost > 0 && digilock.isUser(sPlayer)
				&& !(digilock.isOwner(sPlayer) || digilock.isCoowner(sPlayer))) {
			if (BIT.plugin.economy.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.economy.has(sPlayer.getName(),
						cost)) {
					BIT.plugin.economy.withdrawPlayer(sPlayer.getName(),
							cost);
					if (BIT.plugin.economy.hasAccount(digilock.getOwner())) {
						BIT.plugin.economy.depositPlayer(digilock.getOwner(),
								cost);
					}
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.economy.getBalance(sPlayer.getName())
							+ ") has been deducted "
							+ BIT.plugin.economy.format(cost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.economy.getBalance(sPlayer.getName())
							+ "). Cost is:"
							+ BIT.plugin.economy.format(cost));
					opentrapdoor = false;
				}
			}
		}
		if (opentrapdoor) {
			if (!isTrapdoorOpen(sPlayer, sBlock)) {
				sBlock.setData((byte) (sBlock.getState().getData().getData() | 4));
				if (digilock != null) {
					playDigiLockSound(sBlock);
					if (digilock.getClosetimer() > 0) {
						scheduleCloseTrapdoor(sPlayer, sBlock,
								digilock.getClosetimer());
					}
				}
			}
		}
	}

	public static void closeTrapdoor(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		sBlock.setData((byte) ((sBlock.getState().getData().getData() | 4) ^ 4));
	}

	public static void toggleTrapdoor(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		sBlock.setData((byte) (sBlock.getState().getData().getData() ^ 4));
	}

	public static int scheduleCloseTrapdoor(final SpoutPlayer sPlayer,
											final SpoutBlock sBlock, final int closetimer) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = BIT.plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(BIT.plugin, new Runnable() {
					@Override
					public void run() {
						SpoutBlock sb = sBlock;
						SpoutPlayer sp = sPlayer;
						if (BITConfig.DEBUG_DOOR) {
							sp.sendMessage("Autoclosing the trapdoor in "
									+ closetimer + " seconds");
						}
						if (sBlock.getType() == Material.TRAP_DOOR) {
							if (isTrapdoorOpen(sp, sb)) {
								closeTrapdoor(sp, sb);
								playDigiLockSound(sBlock);
							}
						}
					}
				}, fs);
		return taskID;
	}

	// *******************************************************
	//
	// FENCE_GATE
	//
	// *******************************************************

	public static boolean isFenceGate(Block block) {
		if (block != null) {
			if (block.getType().equals(Material.FENCE_GATE)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFenceGateOpen(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		if ((sBlock.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openFenceGate(SpoutPlayer sPlayer, SpoutBlock sBlock,
									 int cost) {
		boolean openFenceGate = true;
		BITDigiLock digilock = loadDigiLock(sBlock);
		if (BIT.useEconomy && cost > 0 && digilock.isUser(sPlayer)
				&& !(digilock.isOwner(sPlayer) || digilock.isCoowner(sPlayer))) {
			if (BIT.plugin.economy.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.economy.has(sPlayer.getName(),
						cost)) {
					BIT.plugin.economy.withdrawPlayer(sPlayer.getName(),
							cost);
					if (BIT.plugin.economy.hasAccount(digilock.getOwner())) {
						BIT.plugin.economy.depositPlayer(digilock.getOwner(),
								cost);
					}
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.economy.getBalance(sPlayer.getName())
							+ ") has been deducted "
							+ BIT.plugin.economy.format(cost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.economy.getBalance(sPlayer.getName())
							+ "). Cost is:"
							+ BIT.plugin.economy.format(cost));
					openFenceGate = false;
				}
			}
		}
		if (openFenceGate) {
			if (!isFenceGateOpen(sPlayer, sBlock)) {
				sBlock.setData((byte) (sBlock.getState().getData().getData() | 4));
				if (digilock != null) {
					playDigiLockSound(sBlock);
					if (digilock.getClosetimer() > 0) {
						scheduleCloseFenceGate(sPlayer, sBlock,
								digilock.getClosetimer());
					}
				}
			}
		}
	}

	public static void closeFenceGate(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		sBlock.setData((byte) ((sBlock.getState().getData().getData() | 4) ^ 4));
	}

	public static void toggleFenceGate(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		sBlock.setData((byte) (sBlock.getState().getData().getData() ^ 4));
	}

	public static int scheduleCloseFenceGate(final SpoutPlayer sPlayer,
											 final SpoutBlock sBlock, final int closetimer) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = BIT.plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(BIT.plugin, new Runnable() {
					@Override
					public void run() {
						SpoutBlock sb = sBlock;
						SpoutPlayer sp = sPlayer;
						if (BITConfig.DEBUG_DOOR) {
							sp.sendMessage("Autoclosing the fencegate in "
									+ closetimer + " seconds");
						}
						if (sBlock.getType() == Material.FENCE_GATE) {
							if (isFenceGateOpen(sp, sb)) {
								closeFenceGate(sp, sb);
								playDigiLockSound(sBlock);
							}
						}
					}
				}, fs);
		return taskID;
	}

	// *******************************************************
	//
	// DOUBLEDOORS
	//
	// *******************************************************

	/**
	 * Checks if the block is a part of a double door
	 * @param sBlock
	 * @return
	 */
	public static boolean isDoubleDoor(SpoutBlock sBlock) {
		// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
		// left door:WEST,NORTH_WEST Right door:SOUTH,SOUTH_WEST
		// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
		// left door:EAST,SOUTH_EAST Right door:NORTH,NORTH_EAST
		if (sBlock != null) {
			if (isDoor(sBlock)) {
				if (isDoor(sBlock.getRelative(BlockFace.EAST))
						|| isDoor(sBlock.getRelative(BlockFace.NORTH))
						|| isDoor(sBlock.getRelative(BlockFace.SOUTH))
						|| isDoor(sBlock.getRelative(BlockFace.WEST))) {
					Door door = (Door) sBlock.getState().getData();
					if (door.getFacing() == BlockFace.EAST
							&& door.getHingeCorner() == BlockFace.SOUTH_EAST) {
						if (isDoor(sBlock.getRelative(BlockFace.NORTH))) {
							Door door2 = (Door) sBlock
									.getRelative(BlockFace.NORTH).getState()
									.getData();
							if (door2.getHingeCorner() == BlockFace.NORTH_EAST) {
								return true;
							}
						} else if (isDoor(sBlock.getRelative(BlockFace.WEST))) {
							Door door2 = (Door) sBlock
									.getRelative(BlockFace.WEST).getState()
									.getData();
							if (door2.getHingeCorner() == BlockFace.SOUTH_WEST) {
								return true;
							}
						} else {
							BITMessages.showInfo("Doubledoor EAST5 false");
						}
					} else if (door.getFacing() == BlockFace.NORTH
							&& door.getHingeCorner() == BlockFace.NORTH_EAST) {
						if (isDoor(sBlock.getRelative(BlockFace.WEST))) {
							Door door2 = (Door) sBlock
									.getRelative(BlockFace.WEST).getState()
									.getData();
							if (door2.getHingeCorner() == BlockFace.NORTH_WEST) {
								return true;
							}
						} else if (isDoor(sBlock.getRelative(BlockFace.SOUTH))) {
							Door door2 = (Door) sBlock
									.getRelative(BlockFace.SOUTH).getState()
									.getData();
							if (door2.getHingeCorner() == BlockFace.SOUTH_EAST) {
								return true;
							}
						}
					} else if (door.getFacing() == BlockFace.SOUTH
							&& door.getHingeCorner() == BlockFace.SOUTH_WEST) {
						if (isDoor(sBlock.getRelative(BlockFace.EAST))) {
							Door door2 = (Door) sBlock
									.getRelative(BlockFace.EAST).getState()
									.getData();
							if (door2.getHingeCorner() == BlockFace.SOUTH_EAST) {
								return true;
							}
						} else if (isDoor(sBlock.getRelative(BlockFace.NORTH))) {
							Door door2 = (Door) sBlock
									.getRelative(BlockFace.NORTH).getState()
									.getData();
							if (door2.getHingeCorner() == BlockFace.NORTH_WEST) {
								return true;
							}
						}
					} else if (door.getFacing() == BlockFace.WEST
							&& door.getHingeCorner() == BlockFace.NORTH_WEST) {
						if (isDoor(sBlock.getRelative(BlockFace.SOUTH))) {
							Door door2 = (Door) sBlock
									.getRelative(BlockFace.SOUTH).getState()
									.getData();
							if (door2.getHingeCorner() == BlockFace.SOUTH_WEST) {
								return true;
							}
						} else if (isDoor(sBlock.getRelative(BlockFace.EAST))) {
							Door door2 = (Door) sBlock
									.getRelative(BlockFace.EAST).getState()
									.getData();
							if (door2.getHingeCorner() == BlockFace.NORTH_EAST) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * checks if the double door is open
	 * @param sBlock
	 * @return true if the double door is open, false if not
	 */
	public static boolean isDoubleDoorOpen(SpoutBlock sBlock) {
		return (isDoorOpen(getLeftDoubleDoor(sBlock)) || !isDoorOpen(getRightDoubleDoor(sBlock)));
	}

	public static void closeDoubleDoor(SpoutPlayer sPlayer, SpoutBlock sBlock,
									   int cost) {
		if (isDoubleDoor(sBlock)) {
			if (isLeftDoubleDoor(sBlock)) {
				closeDoor(sPlayer, sBlock, 0);
				openDoor(sPlayer, getRightDoubleDoor(sBlock), 0);
			} else {
				openDoor(sPlayer, sBlock, cost);
				closeDoor(sPlayer, getLeftDoubleDoor(sBlock), cost);
			}
		}
	}

	public static void openDoubleDoor(SpoutPlayer sPlayer, SpoutBlock sBlock,
									  int cost) {
		if (isDoubleDoor(sBlock)) {
			if (isLeftDoubleDoor(sBlock)) {
				openDoor(sPlayer, sBlock, cost);
				closeDoor(sPlayer, getRightDoubleDoor(sBlock), 0);
			} else {
				closeDoor(sPlayer, sBlock, 0);
				openDoor(sPlayer, getLeftDoubleDoor(sBlock), cost);
			}
		}
		BITDigiLock digilock = loadDigiLock(sBlock);
		if (digilock != null) {
			if (digilock.getClosetimer() > 0) {
				scheduleCloseDoubleDoor(sPlayer, sBlock,
						digilock.getClosetimer(), 0);
			}
		}
	}

	public static boolean isLeftDoubleDoor(SpoutBlock sBlock) {
		if (isDoubleDoor(sBlock)) {
			Door door = (Door) sBlock.getState().getData();
			// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
			// left door:WEST,NORTH_WEST Right door:SOUTH,SOUTH_WEST
			// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
			// left door:EAST,SOUTH_EAST Right door:NORTH,NORTH_EAST
			if (door.getFacing() == BlockFace.NORTH
					&& door.getHingeCorner() == BlockFace.NORTH_EAST) {
				if (isDoor(sBlock.getRelative(BlockFace.WEST))) {
					Door door2 = (Door) sBlock.getRelative(BlockFace.WEST)
							.getState().getData();
					if (door2.getHingeCorner() == BlockFace.NORTH_WEST) {
						return true;
					}
				} else {
					return false;
				}
			} else if (door.getFacing() == BlockFace.WEST
					&& door.getHingeCorner() == BlockFace.NORTH_WEST) {
				if (isDoor(sBlock.getRelative(BlockFace.SOUTH))) {
					Door door2 = (Door) sBlock.getRelative(BlockFace.SOUTH)
							.getState().getData();
					if (door2.getHingeCorner() == BlockFace.SOUTH_WEST) {
						return true;
					}
				} else {
					return false;
				}
			} else if (door.getFacing() == BlockFace.EAST
					&& door.getHingeCorner() == BlockFace.SOUTH_EAST) {
				if (isDoor(sBlock.getRelative(BlockFace.NORTH))) {
					Door door2 = (Door) sBlock.getRelative(BlockFace.NORTH)
							.getState().getData();
					if (door2.getHingeCorner() == BlockFace.NORTH_EAST) {
						return true;
					}
				} else {
					return false;
				}
			} else if (door.getFacing() == BlockFace.SOUTH
					&& door.getHingeCorner() == BlockFace.SOUTH_WEST) {
				if (isDoor(sBlock.getRelative(BlockFace.EAST))) {
					Door door2 = (Door) sBlock.getRelative(BlockFace.EAST)
							.getState().getData();
					if (door2.getHingeCorner() == BlockFace.SOUTH_EAST) {
						return true;
					}
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public static SpoutBlock getLeftDoubleDoor(SpoutBlock sBlock) {
		if (isLeftDoubleDoor(sBlock)) {
			return sBlock;
		} else {
			Door door = (Door) sBlock.getState().getData();
			// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
			// left door:WEST,NORTH_WEST Right door:SOUTH,SOUTH_WEST
			// left door:EAST,SOUTH_EAST Right door:NORTH,NORTH_EAST
			// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
			if (door.getFacing() == BlockFace.NORTH
					&& door.getHingeCorner() == BlockFace.NORTH_EAST) {
				return sBlock.getRelative(BlockFace.SOUTH);
			} else if (door.getFacing() == BlockFace.WEST
					&& door.getHingeCorner() == BlockFace.NORTH_WEST) {
				return sBlock.getRelative(BlockFace.EAST);
			} else if (door.getFacing() == BlockFace.EAST
					&& door.getHingeCorner() == BlockFace.SOUTH_EAST) {
				return sBlock.getRelative(BlockFace.WEST);
			} else {
				// if (door.getFacing() == BlockFace.SOUTH
				// && door.getHingeCorner() == BlockFace.SOUTH_WEST) {
				return sBlock.getRelative(BlockFace.NORTH);
			}
		}
	}

	public static boolean isRightDoubleDoor(SpoutBlock sBlock) {
		if (isDoubleDoor(sBlock)) {
			if (isLeftDoubleDoor(sBlock)) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public static SpoutBlock getRightDoubleDoor(SpoutBlock sBlock) {
		if (isRightDoubleDoor(sBlock)) {
			return sBlock;
		} else {
			Door door = (Door) sBlock.getState().getData();
			// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
			// left door:WEST,NORTH_WEST Right door:SOUTH,SOUTH_WEST
			// left door:EAST,SOUTH_EAST Right door:NORTH,NORTH_EAST
			// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
			if (door.getFacing() == BlockFace.NORTH
					&& door.getHingeCorner() == BlockFace.NORTH_EAST) {
				return sBlock.getRelative(BlockFace.WEST);
			} else if (door.getFacing() == BlockFace.WEST
					&& door.getHingeCorner() == BlockFace.NORTH_WEST) {
				return sBlock.getRelative(BlockFace.SOUTH);
			} else if (door.getFacing() == BlockFace.EAST
					&& door.getHingeCorner() == BlockFace.SOUTH_EAST) {
				return sBlock.getRelative(BlockFace.NORTH);
			} else {
				// (door.getFacing() == BlockFace.SOUTH
				// && door.getHingeCorner() == BlockFace.SOUTH_WEST) {
				return sBlock.getRelative(BlockFace.EAST);
			}
		}
	}

	public static int scheduleCloseDoubleDoor(final SpoutPlayer sPlayer,
											  final SpoutBlock sBlock, final int closetimer, final int cost) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = BIT.plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(BIT.plugin, new Runnable() {
					@Override
					public void run() {
						SpoutBlock sb = sBlock;
						SpoutPlayer sp = sPlayer;
						int c = cost;
						if (BITConfig.DEBUG_DOOR) {
							sp.sendMessage("Autoclosing the DoubleDoor in "
									+ closetimer + " seconds");
						}
						if (isDoubleDoor(sBlock)) {
							if (isDoubleDoorOpen(sb)) {
								closeDoubleDoor(sp, sb, c);
								playDigiLockSound(sBlock);
							}
						}
					}
				}, fs);
		return taskID;
	}

	// *******************************************************
	//
	// JUKEBOX
	//
	// *******************************************************

	/**
	 * Check if sBlock is a JUKEBOX
	 * @param sBlock
	 * @return true or false
	 */
	public static boolean isJukebox(SpoutBlock sBlock) {
		if (sBlock != null) {
			if (sBlock.getType().equals(Material.JUKEBOX)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPiston(SpoutBlock sBlock) {  //Dockter this doesnt work yet.
		if (sBlock != null) {
			if (sBlock.getType().equals(Material.PISTON_BASE) || sBlock.getType().equals(Material.PISTON_STICKY_BASE)) {
				return true;
			}
		}
		return false;
	}
}
