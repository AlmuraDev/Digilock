package com.almuramc.digilock.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.almuramc.digilock.Digilock;
import com.almuramc.digilock.LockCore;

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

	public static LockCore loadDigiLock(SpoutBlock block) {
		// TODO: fasten up the load of lock, select from a HASHMAP second
		// time
		block = getDigiLockBlock(block);
		String query = "SELECT * FROM " + Digilock.getHandler().getTableName() + " WHERE (x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
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
				LockCore lock = new LockCore(Digilock.getInstance(), block, pincode, owner,
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
	public static Boolean isLocked(SpoutBlock block) {
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
		if (Digilock.getConf().playLockSound()) {
			SpoutManager
					.getSoundManager()
					.playGlobalCustomSoundEffect(
							Digilock.getInstance(),
							Digilock.getConf().getSoundURL(),
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
	 * @param cost    the cost the player is charged when the button is pressed.
	 */
	public static void pressButtonOn(SpoutPlayer sPlayer, SpoutBlock sBlock,
									 int cost) {
		boolean doTheWork = false;
		LockCore lock = loadDigiLock(sBlock);
		SpoutBlock nextBlock = lock.getNextLockedBlock(sPlayer, sBlock);
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
		LockCore lock = loadDigiLock(sBlock);
		if (lock != null) {
			SpoutBlock nextBlock = lock.getNextLockedBlock(sPlayer, sBlock);
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
						openDoubleDoor(sPlayer, nextBlock, 0);
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

	public static void leverOff(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		if (isLeverOn(sBlock)) {
			LockCore lock = loadDigiLock(sBlock);
			if (lock != null) {
				SpoutBlock nextBlock = lock.getNextLockedBlock(sPlayer,
						sBlock);
				if (nextBlock != null) {
					LockCore nextLock = loadDigiLock(nextBlock);
					if (lock.getOwner().equalsIgnoreCase(
							nextLock.getOwner())) {
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
		int taskID = Digilock.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(Digilock.getInstance(), new Runnable() {
					@Override
					public void run() {
						SpoutBlock sb = sBlock;
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

	public static boolean isDoorOpen(SpoutBlock sBlock) {
		if ((sBlock.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openDoor(SpoutPlayer sPlayer, SpoutBlock sBlock, int cost) {
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
				if (lock != null) {
					if (lock.getClosetimer() > 0 && !isDoubleDoor(sBlock)) {
						scheduleCloseDoor(sPlayer, sBlock,
								lock.getClosetimer(), 0);
					}
				}
			}
		}
	}

	public static void closeDoor(SpoutPlayer sPlayer, SpoutBlock sBlock,
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
		int taskID = Digilock.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(Digilock.getInstance(), new Runnable() {
					@Override
					public void run() {
						SpoutBlock sb = sBlock;
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
		int taskID = Digilock.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(Digilock.getInstance(), new Runnable() {
					@Override
					public void run() {
						SpoutBlock sb = sBlock;
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
		int taskID = Digilock.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(Digilock.getInstance(), new Runnable() {
					@Override
					public void run() {
						SpoutBlock sb = sBlock;
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
							Messages.showInfo("Doubledoor EAST5 false");
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
		LockCore lock = loadDigiLock(sBlock);
		if (lock != null) {
			if (lock.getClosetimer() > 0) {
				scheduleCloseDoubleDoor(sPlayer, sBlock,
						lock.getClosetimer(), 0);
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
		int taskID = Digilock.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(Digilock.getInstance(), new Runnable() {
					@Override
					public void run() {
						SpoutBlock sb = sBlock;
						SpoutPlayer sp = sPlayer;
						int c = cost;
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
