package com.almuramc.digilock.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.almuramc.digilock.Digilock;
import com.almuramc.digilock.LockCore;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;

public class BlockTools {
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

	public static LockCore loadDigiLock(Block sBlock) {
		// TODO: fasten up the load of lock, select from a HASHMAP second
		// time
		sBlock = getDigiLockBlock(sBlock);
		String query = "SELECT * FROM " + Digilock.getHandler().getTableName() + " WHERE (x = "
				+ sBlock.getX() + " AND y = " + sBlock.getY() + " AND z = "
				+ sBlock.getZ() + " AND world='" + sBlock.getWorld().getName()
				+ "');";
		ResultSet result = null;
		if (Digilock.getConf().getSQLType().equals("MYSQL")) {
			result = Digilock.getHandler().getMySQLHandler().query(query);
		} else { // SQLLITE
			result = Digilock.getHandler().getSqliteHandler().query(query);
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
				LockCore lock = new LockCore(Digilock.getInstance(), sBlock, pincode, owner,
						closetimer, coowners, users, typeId, connectedTo,
						useCost);
				result.close();

				return lock;
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
	public static Boolean isLocked(Block block) {
		// TODO: Implement a HASHMAP for testing if the block is locked.
		// Messages.showInfo("isLocked was called");
		if (block != null) {
			if (isLockable(block)) {
				block = getDigiLockBlock(block);
				String query = "SELECT * FROM " + Digilock.getHandler().getTableName()
						+ " WHERE (x = " + block.getX() + " AND y = "
						+ block.getY() + " AND z = " + block.getZ()
						+ " AND world='" + block.getWorld().getName() + "');";
				ResultSet result = null;
				if (Digilock.getConf().getSQLType().equals("MYSQL")) {
					result = Digilock.getHandler().getMySQLHandler().query(query);
				} else { // SQLLITE
					result = Digilock.getHandler().getSqliteHandler().query(query);
				}
				try {
					if (result != null && result.next()) {
						result.close();
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
	public static Block getDigiLockBlock(Block sBlock) {
		if (isDoor(sBlock)) {
			if (isDoubleDoor(sBlock)) {
				return (Block) getDoubleDoor(sBlock);
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
				Block sBlock2 = (Block) sChest2.getBlock();

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
	public static void playDigiLockSound(Block doors) {
		if (Digilock.getConf().playLockSound()) {
			SpoutManager.getSoundManager().playGlobalCustomSoundEffect(Digilock.getInstance(),	Digilock.getConf().getSoundURL(),true, doors.getLocation(), 5);
		}
	}

	public static boolean isNeighbourLocked(Block block) {
		if (block != null) {
			for (BlockFace bf : BlockFace.values()) {
				if (isLocked(block.getRelative(bf))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isNeighbourSameOwner(Block block, String owner) {
		for (BlockFace bf : BlockFace.values()) {
			if (isLocked(block.getRelative(bf))) {
				LockCore lock = loadDigiLock(block
						.getRelative(bf));
				if (lock.getOwner().equalsIgnoreCase(owner)) {
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
	// STONE_BUTTON
	//
	// *******************************************************

	/**
	 * Check if sBlock is a STONE_BUTTON
	 * @param sBlock
	 * @return true or false
	 */
	public static boolean isButton(Block sBlock) {
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
	public static boolean isButtonOn(Block block) {
		Button button = (Button) block.getState().getData();
		return button.isPowered();
	}

	/**
	 * Handle the actions when a player presses the STONE_BUTTON
	 * @param sPlayer SpoutPlayer
	 * @param sBlock  SpoutBlock
	 * @param cost    the cost the player is charged when the button is pressed.
	 */
	public static void pressButtonOn(SpoutPlayer sPlayer, Block sBlock,
									 int cost) {
		boolean doTheWork = false;
		LockCore lock = loadDigiLock(sBlock);
		Block nextBlock = lock.getNextLockedBlock(sPlayer, sBlock);
		if (nextBlock != null) {
			LockCore nextLock = loadDigiLock(nextBlock);
			if (lock.getOwner().equalsIgnoreCase(nextLock.getOwner())) {
				doTheWork = true;
			} else {
				sPlayer.sendMessage("You are not the owner of the "
						+ nextBlock.getType());
			}
			boolean pressButton = true;
			if (Digilock.getConf().useEconomy()
					&& cost > 0
					&& doTheWork
					&& lock.isUser(sPlayer)
					&& !(lock.isOwner(sPlayer) || lock
					.isCoowner(sPlayer))) {
				if (Digilock.getHooks().getEconHook().hasAccount(sPlayer.getName())) {
					if (Digilock.getHooks().getEconHook().has(sPlayer.getName(), cost)) {
						Digilock.getHooks().getEconHook().withdrawPlayer(sPlayer.getName(), cost);
						if (Digilock.getHooks().getEconHook().hasAccount(nextLock.getOwner())) {
							Digilock.getHooks().getEconHook().depositPlayer(nextLock.getOwner(), cost);
						}

						sPlayer.sendMessage("Your account ("
								+ Digilock.getHooks().getEconHook().getBalance(
								sPlayer.getName())
								+ ") has been deducted "
								+ Digilock.getHooks().getEconHook().format(cost) + ".");
					} else {
						sPlayer.sendMessage("You dont have enough money ("
								+ Digilock.getHooks().getEconHook().getBalance(
								sPlayer.getName())
								+ "). Cost is:"
								+ Digilock.getHooks().getEconHook().format(cost));
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
					changeDoorStates(true, sPlayer, cost, nextBlock, getDoubleDoor(nextBlock));
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
	 * @param sb
	 * @return
	 */
	public static boolean isDispenser(Block sb) {
		if (sb != null) {
			if (sb.getType().equals(Material.DISPENSER)) {
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
	public static boolean isLever(Block sBlock) {
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
	public static boolean isLeverOn(Block sBlock) {
		Lever lever = (Lever) sBlock.getState().getData();
		return lever.isPowered();
	}

	public static void leverOn(SpoutPlayer sPlayer, Block sBlock, int cost) {
		boolean doTheWork = false;
		LockCore lock = loadDigiLock(sBlock);
		if (lock != null) {
			Block nextBlock = lock.getNextLockedBlock(sPlayer, sBlock);
			if (nextBlock != null) {
				LockCore nextLock = loadDigiLock(nextBlock);

				if (lock.getOwner().equalsIgnoreCase(
						nextLock.getOwner())) {
					doTheWork = true;
				} else {
					sPlayer.sendMessage("You are not the owner of the "
							+ nextBlock.getType());
				}
				boolean setleveron = true;
				if (Digilock.getConf().useEconomy()
						&& cost > 0
						&& doTheWork
						&& lock.isUser(sPlayer)
						&& !(lock.isOwner(sPlayer) || lock
						.isCoowner(sPlayer))) {
					if (Digilock.getHooks().getEconHook().hasAccount(sPlayer.getName())) {
						if (Digilock.getHooks().getEconHook().has(sPlayer.getName(), cost)) {
							Digilock.getHooks().getEconHook().withdrawPlayer(sPlayer.getName(), cost);
							if (Digilock.getHooks().getEconHook().hasAccount(nextLock
									.getOwner())) {
								Digilock.getHooks().getEconHook().depositPlayer(
										nextLock.getOwner(), cost);
							}
							sPlayer.sendMessage("Your account ("
									+ Digilock.getHooks().getEconHook().getBalance(
									sPlayer.getName())
									+ ") has been deducted "
									+ Digilock.getHooks().getEconHook().format(cost) + ".");
						} else {
							sPlayer.sendMessage("You dont have enough money ("
									+ Digilock.getHooks().getEconHook().getBalance(
									sPlayer.getName())
									+ "). Cost is:"
									+ Digilock.getHooks().getEconHook().format(cost));
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
						changeDoorStates(true, sPlayer, cost, nextBlock, getDoubleDoor(nextBlock));
					} else if (isDoor(nextBlock)) {
						openDoor(sPlayer, nextBlock, 0);
					} else if (isTrapdoor(nextBlock)) {
						openTrapdoor(sPlayer, nextBlock, 0);
					} else if (nextBlock.getType().equals(Material.DISPENSER)) {
						Dispenser dispenser = (Dispenser) nextBlock.getState();
						dispenser.dispense();
					}
					if (lock.getClosetimer() > 0) {
						scheduleLeverOff(sPlayer, sBlock,
								lock.getClosetimer());
					}
				}
			}
		}
	}

	public static void leverOff(SpoutPlayer sPlayer, Block sBlock) {
		if (isLeverOn(sBlock)) {
			LockCore lock = loadDigiLock(sBlock);
			if (lock != null) {
				Block nextBlock = lock.getNextLockedBlock(sPlayer,
						sBlock);
				if (nextBlock != null) {
					LockCore nextLock = loadDigiLock(nextBlock);
					if (lock.getOwner().equalsIgnoreCase(
							nextLock.getOwner())) {
						Lever lever = (Lever) sBlock.getState().getData();
						lever.setPowered(false);
						// sBlock.setData((byte) (lever.getData() |8));
						if (isDoubleDoor(nextBlock)) {
							changeDoorStates(true, sPlayer, 0, sBlock, getDoubleDoor(sBlock));
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
									   final Block sBlock, final int closetimer) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = Digilock.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(Digilock.getInstance(), new Runnable() {
					@Override
					public void run() {
						Block sb = sBlock;
						SpoutPlayer sp = sPlayer;
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

	public static boolean isDoorOpen(Block sBlock) {
		if ((sBlock.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openDoor(SpoutPlayer sPlayer, Block sBlock, int cost) {
		boolean opendoor = true;
		LockCore lock = loadDigiLock(sBlock);
		if (Digilock.getConf().useEconomy() && cost > 0 && lock.isUser(sPlayer)
				&& !(lock.isOwner(sPlayer) || lock.isCoowner(sPlayer))) {
			if (Digilock.getHooks().getEconHook().hasAccount(sPlayer.getName())) {
				if (Digilock.getHooks().getEconHook().has(sPlayer.getName(),
						cost)) {
					Digilock.getHooks().getEconHook().withdrawPlayer(sPlayer.getName(),
							cost);
					if (Digilock.getHooks().getEconHook().hasAccount(lock.getOwner())) {
						Digilock.getHooks().getEconHook().depositPlayer(lock.getOwner(),
								cost);
					}
					sPlayer.sendMessage("Your account ("
							+ Digilock.getHooks().getEconHook().getBalance(sPlayer.getName())
							+ ") has been deducted "
							+ Digilock.getHooks().getEconHook().format(cost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ Digilock.getHooks().getEconHook().getBalance(sPlayer.getName())
							+ "). Cost is:"
							+ Digilock.getHooks().getEconHook().format(cost));
					opendoor = false;
				}
			}
		}
		Door door = (Door) sBlock.getState().getData();
		Block nextBlock;
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
				if (lock != null) {
					if (lock.getClosetimer() > 0 && !isDoubleDoor(sBlock)) {
						scheduleCloseDoor(sPlayer, sBlock,
								lock.getClosetimer(), 0);
					}
				}
			}
		}
	}

	public static void closeDoor(SpoutPlayer sPlayer, Block sBlock,
								 int cost) {
		boolean closedoor = true;
		LockCore lock = loadDigiLock(sBlock);
		if (Digilock.getConf().useEconomy() && cost > 0 && lock.isUser(sPlayer)
				&& !(lock.isOwner(sPlayer) || lock.isCoowner(sPlayer))) {
			if (Digilock.getHooks().getEconHook().hasAccount(sPlayer.getName())) {
				if (Digilock.getHooks().getEconHook().has(sPlayer.getName(),
						cost)) {
					Digilock.getHooks().getEconHook().withdrawPlayer(sPlayer.getName(),
							cost);
					if (Digilock.getHooks().getEconHook().hasAccount(lock.getOwner())) {
						Digilock.getHooks().getEconHook().depositPlayer(lock.getOwner(),
								cost);
					}

					sPlayer.sendMessage("Your account ("
							+ Digilock.getHooks().getEconHook().getBalance(sPlayer.getName())
							+ ") has been deducted "
							+ Digilock.getHooks().getEconHook().format(cost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ Digilock.getHooks().getEconHook().getBalance(sPlayer.getName())
							+ "). Cost is:"
							+ Digilock.getHooks().getEconHook().format(cost));
					closedoor = false;
				}
			}
		}
		if (closedoor) {
			if (isDoorOpen(sBlock)) {
				playDigiLockSound(sBlock);
				Door door = (Door) sBlock.getState().getData();
				Block nextBlock;
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

	public static void closeDoor(Block sBlock) {
		if (isDoorOpen(sBlock)) {
			playDigiLockSound(sBlock);
			Door door = (Door) sBlock.getState().getData();
			Block nextBlock;
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

	public static void toggleDoor(Block sBlock) {
		playDigiLockSound(sBlock);
		Door door = (Door) sBlock.getState().getData();
		Block nextBlock;
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
										final Block sBlock, final int closetimer, final int cost) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = Digilock.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(Digilock.getInstance(), new Runnable() {
					@Override
					public void run() {
						Block sb = sBlock;
						SpoutPlayer sp = sPlayer;
						int c = cost;
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

	public static boolean isTrapdoorOpen(SpoutPlayer sPlayer, Block sBlock) {
		if ((sBlock.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openTrapdoor(SpoutPlayer sPlayer, Block sBlock,
									int cost) {
		boolean opentrapdoor = true;
		LockCore lock = loadDigiLock(sBlock);
		if (Digilock.getConf().useEconomy() && cost > 0 && lock.isUser(sPlayer)
				&& !(lock.isOwner(sPlayer) || lock.isCoowner(sPlayer))) {
			if (Digilock.getHooks().getEconHook().hasAccount(sPlayer.getName())) {
				if (Digilock.getHooks().getEconHook().has(sPlayer.getName(),
						cost)) {
					Digilock.getHooks().getEconHook().withdrawPlayer(sPlayer.getName(),
							cost);
					if (Digilock.getHooks().getEconHook().hasAccount(lock.getOwner())) {
						Digilock.getHooks().getEconHook().depositPlayer(lock.getOwner(),
								cost);
					}
					sPlayer.sendMessage("Your account ("
							+ Digilock.getHooks().getEconHook().getBalance(sPlayer.getName())
							+ ") has been deducted "
							+ Digilock.getHooks().getEconHook().format(cost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ Digilock.getHooks().getEconHook().getBalance(sPlayer.getName())
							+ "). Cost is:"
							+ Digilock.getHooks().getEconHook().format(cost));
					opentrapdoor = false;
				}
			}
		}
		if (opentrapdoor) {
			if (!isTrapdoorOpen(sPlayer, sBlock)) {
				sBlock.setData((byte) (sBlock.getState().getData().getData() | 4));
				if (lock != null) {
					playDigiLockSound(sBlock);
					if (lock.getClosetimer() > 0) {
						scheduleCloseTrapdoor(sPlayer, sBlock,
								lock.getClosetimer());
					}
				}
			}
		}
	}

	public static void closeTrapdoor(SpoutPlayer sPlayer, Block sBlock) {
		sBlock.setData((byte) ((sBlock.getState().getData().getData() | 4) ^ 4));
	}

	public static void toggleTrapdoor(SpoutPlayer sPlayer, Block sBlock) {
		sBlock.setData((byte) (sBlock.getState().getData().getData() ^ 4));
	}

	public static int scheduleCloseTrapdoor(final SpoutPlayer sPlayer,
											final Block sBlock, final int closetimer) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = Digilock.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(Digilock.getInstance(), new Runnable() {
					@Override
					public void run() {
						Block sb = sBlock;
						SpoutPlayer sp = sPlayer;
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

	public static boolean isFenceGateOpen(SpoutPlayer sPlayer, Block sBlock) {
		if ((sBlock.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openFenceGate(SpoutPlayer sPlayer, Block sBlock,
									 int cost) {
		boolean openFenceGate = true;
		LockCore lock = loadDigiLock(sBlock);
		if (Digilock.getConf().useEconomy() && cost > 0 && lock.isUser(sPlayer)
				&& !(lock.isOwner(sPlayer) || lock.isCoowner(sPlayer))) {
			if (Digilock.getHooks().getEconHook().hasAccount(sPlayer.getName())) {
				if (Digilock.getHooks().getEconHook().has(sPlayer.getName(),
						cost)) {
					Digilock.getHooks().getEconHook().withdrawPlayer(sPlayer.getName(),
							cost);
					if (Digilock.getHooks().getEconHook().hasAccount(lock.getOwner())) {
						Digilock.getHooks().getEconHook().depositPlayer(lock.getOwner(),
								cost);
					}
					sPlayer.sendMessage("Your account ("
							+ Digilock.getHooks().getEconHook().getBalance(sPlayer.getName())
							+ ") has been deducted "
							+ Digilock.getHooks().getEconHook().format(cost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ Digilock.getHooks().getEconHook().getBalance(sPlayer.getName())
							+ "). Cost is:"
							+ Digilock.getHooks().getEconHook().format(cost));
					openFenceGate = false;
				}
			}
		}
		if (openFenceGate) {
			if (!isFenceGateOpen(sPlayer, sBlock)) {
				sBlock.setData((byte) (sBlock.getState().getData().getData() | 4));
				if (lock != null) {
					playDigiLockSound(sBlock);
					if (lock.getClosetimer() > 0) {
						scheduleCloseFenceGate(sPlayer, sBlock,
								lock.getClosetimer());
					}
				}
			}
		}
	}

	public static void closeFenceGate(SpoutPlayer sPlayer, Block sBlock) {
		sBlock.setData((byte) ((sBlock.getState().getData().getData() | 4) ^ 4));
	}

	public static void toggleFenceGate(SpoutPlayer sPlayer, Block sBlock) {
		sBlock.setData((byte) (sBlock.getState().getData().getData() ^ 4));
	}

	public static int scheduleCloseFenceGate(final SpoutPlayer sPlayer,
											 final Block sBlock, final int closetimer) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = Digilock.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(Digilock.getInstance(), new Runnable() {
					@Override
					public void run() {
						Block sb = sBlock;
						SpoutPlayer sp = sPlayer;
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

	public static boolean isDoubleDoor(Block sBlock) {
		if (getDoubleDoor(sBlock) != null) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if the block is a part of a double door
	 * @param sBlock
	 * @return
	 */
	public static Block getDoubleDoor(Block sBlock) {
		Block block = sBlock;
		if ((sBlock.getData() & 0x8) == 0x8) {
			block = block.getRelative(BlockFace.DOWN);
		}
		Block found = findAdjacentBlock(block, block.getType());
		if (found != null) {
			if (found.getType() == Material.WOOD_DOOR) {
				return found;
			}
			if (found.getType() == Material.IRON_DOOR) {
				return found;
			}
		}
		return null;
	}

	public static void changeDoorStates(boolean allowDoorToOpen, SpoutPlayer sPlayer, int cost, Block... doors) {
		boolean cont = false;

		for (Block door : doors) {
			if (door == null) {
				continue;
			}

			if (!allowDoorToOpen && (door.getData() & 0x4) == 0) {
				continue;
			}

			// Get the top half of the door
			Block topHalf = door.getRelative(BlockFace.UP);

			// Now xor both data values with 0x8, the flag that states if the door is open
			door.setData((byte) (door.getData() ^ 0x4));

			// Only change the block above it if it is something we can open or close
			if (isValid(topHalf.getType())) {
				topHalf.setData((byte) (topHalf.getData() ^ 0x4));
				cont = true;
			}
		}

		if (!cont) {
			return;
		}

		Block sBlock = (Block) doors[0];
		playDigiLockSound((Block) doors[0]);
		LockCore lock = loadDigiLock(sBlock);
		if (Digilock.getConf().useEconomy() && cost > 0 && lock.isUser(sPlayer) && !(lock.isOwner(sPlayer) || lock.isCoowner(sPlayer))) {
			if (Digilock.getHooks().getEconHook().hasAccount(sPlayer.getName())) {
				if (Digilock.getHooks().getEconHook().has(sPlayer.getName(), cost)) {
					Digilock.getHooks().getEconHook().withdrawPlayer(sPlayer.getName(), cost);
					if (Digilock.getHooks().getEconHook().hasAccount(lock.getOwner())) {
						Digilock.getHooks().getEconHook().depositPlayer(lock.getOwner(), cost);
					}

					sPlayer.sendMessage("Your account (" + Digilock.getHooks().getEconHook().getBalance(sPlayer.getName()) + ") has been deducted " + Digilock.getHooks().getEconHook().format(cost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money (" + Digilock.getHooks().getEconHook().getBalance(sPlayer.getName()) + "). Cost is:" + Digilock.getHooks().getEconHook().format(cost));
				}
			}
		}
	}

	public static boolean isDoubleDoorOpen(Block block) {
		if ((block.getData() & 0x4) == 0) {
			return false;
		}

		return true;
	}

	private static boolean isValid(Material material) {
		return material == Material.IRON_DOOR_BLOCK || material == Material.WOODEN_DOOR || material == Material.FENCE_GATE;
	}

	public static Block findAdjacentBlock(Block block, Material material, Block... ignore) {
		BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
		List<Block> ignoreList = Arrays.asList(ignore);

		for (BlockFace face : faces) {
			Block adjacentBlock = block.getRelative(face);

			if (adjacentBlock.getType() == material && !ignoreList.contains(adjacentBlock)) {
				return adjacentBlock;
			}
		}

		return null;
	}

	public static int scheduleCloseDoubleDoor(final SpoutPlayer sPlayer, final Block sBlock, final int closetimer, final int cost) {
		final Block doubleDoorBlock = getDoubleDoor(sBlock);

		int taskID = Digilock.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Digilock.getInstance(), new Runnable() {
			public void run() {
				changeDoorStates(false, sPlayer, cost, sBlock, doubleDoorBlock);
			}
		}, closetimer);
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
	public static boolean isJukebox(Block sBlock) {
		if (sBlock != null) {
			if (sBlock.getType().equals(Material.JUKEBOX)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPiston(Block sb) {  //Dockter this doesnt work yet.
		if (sb != null) {
			if (sb.getType().equals(Material.PISTON_BASE) || sb.getType().equals(Material.PISTON_STICKY_BASE)) {
				return true;
			}
		}
		return false;
	}
}
