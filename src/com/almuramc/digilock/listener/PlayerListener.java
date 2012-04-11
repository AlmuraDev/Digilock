package com.almuramc.digilock.listener;

import com.almuramc.digilock.Digilock;
import com.almuramc.digilock.LockCore;
import com.almuramc.digilock.util.BlockTools;
import com.almuramc.digilock.util.LockInventory;
import com.almuramc.digilock.util.Messages;
import com.almuramc.digilock.util.Permissions;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PlayerListener implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (event.isCancelled()) {
			return;
		}
		// DOORS, DOUBLEDOORS, TRAPDOORS LEVERS, BUTTON can be handled with both
		// mousebuttons, the rest is only with RIGHT_CLICK
		if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getClickedBlock();
		// for faster handling
		if (!BlockTools.isLockable(sBlock)) {
			return;
		}
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		ItemStack itemInHand = sPlayer.getInventory().getItemInHand();
		// sPlayer.sendMessage("PlayerListener:"+itemInHand.getTypeId());
		int id = sPlayer.getEntityId();

		// Call setPincode

		if (sPlayer.isSpoutCraftEnabled()
				&& BlockTools.isLockable(sBlock)
				&& LockCore.holdingKey.get(id).equals("KEY_LCONTROL")
				&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				&& (Permissions.hasPerm(sPlayer, "lock.create",
				Permissions.NOT_QUIET) || Permissions.hasPerm(
				sPlayer, "lock.admin", Permissions.NOT_QUIET))) {

			event.setCancelled(true);
			if (BlockTools.isLocked(sBlock)) {
				LockCore lock = BlockTools.loadDigiLock(sBlock);
				if (lock.isOwner(sPlayer)
						|| lock.isCoowner(sPlayer)
						|| Permissions.hasPerm(sPlayer, "lock.admin",
						Permissions.NOT_QUIET)) {
					LockCore.setPincode(sPlayer, sBlock);
				} else {
					sPlayer.sendMessage("You are not the owner or coowner");
				}
			} else {
				LockCore.setPincode(sPlayer, sBlock);
			}

			// Call openEditSignGUI
		} else

			// HANDLING THAT PLAYER CLICK ON A BLOCK WITH A DIGILOCK
			if (BlockTools.isLocked(sBlock)) {
				LockCore lock = BlockTools.loadDigiLock(sBlock);

				// HANDLING A LOCKED CHEST AND DOUBLECHEST
				if (BlockTools.isChest(sBlock)) {
					if ((lock.getPincode().equals("") || lock.getPincode()
							.equalsIgnoreCase("fingerprint"))
							&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)) {
						// OPEN CHEST BY FINGERPRINT / NAME
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							if (sBlock.getState() instanceof Chest) {
								Chest sChest = (Chest) (sBlock.getState());
								Inventory inv = sChest.getInventory();
								Messages.sendNotification(sPlayer,
										"Opened by fingerprint");
								BlockTools.playDigiLockSound(sBlock);
								sPlayer.openInventory(inv);
							} else {
								event.setCancelled(true);
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							}
						}
					} else {
						event.setCancelled(true);
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
								Action.RIGHT_CLICK_BLOCK)
								&& Permissions.hasPerm(sPlayer, "lock.use",
								Permissions.NOT_QUIET)) {
							LockCore.getPincode(sPlayer, sBlock);
						} else {
							sPlayer.sendMessage("Locked with Digilock by " + lock.owner);
						}
					}
				}

				// HANDLING A LOCKED DOUBLEDOOR
				else if (BlockTools.isDoubleDoor(sBlock)) {
					event.setCancelled(true);
					if (lock.getPincode().equals("")
							|| lock.getPincode()
							.equalsIgnoreCase("fingerprint")
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)) {
						// TOGGLE DOOR BY FINGERPRINT / NAME
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)
								|| Permissions.hasPerm(sPlayer, "lock.admin",
								Permissions.NOT_QUIET)) {
							BlockTools.playDigiLockSound(sBlock);
							if (BlockTools.isDoubleDoorOpen(sBlock)) {
								BlockTools.closeDoubleDoor(sPlayer, sBlock, 0);
							} else {
								BlockTools.openDoubleDoor(sPlayer, sBlock,
										lock.getUseCost());
							}
							Messages.sendNotification(sPlayer,
									"Used with fingerprint");
						} else {
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							if (BlockTools.isDoubleDoorOpen(sBlock)) {
								BlockTools.playDigiLockSound(sBlock);
								BlockTools.closeDoubleDoor(sPlayer, sBlock, 0);
							}
						}
					} else {
						// ASK FOR PINCODE
						if (!BlockTools.isDoubleDoorOpen(sBlock)
								&& Permissions.hasPerm(sPlayer, "lock.use",
								Permissions.NOT_QUIET)) {
							if (sPlayer.isSpoutCraftEnabled()) {
								LockCore.getPincode(sPlayer,
										BlockTools.getLeftDoubleDoor(sBlock));
							} else {
								sPlayer.sendMessage("Digilock'ed by "
										+ lock.owner);
							}
						} else {
							BlockTools.closeDoubleDoor(sPlayer, sBlock, 0);
							BlockTools.playDigiLockSound(sBlock);
						}
					}
				}

				// HANDLING A LOCKED DOOR
				else if (BlockTools.isDoor(sBlock)) {
					event.setCancelled(true);
					if (lock.getPincode().equals("")
							|| lock.getPincode()
							.equalsIgnoreCase("fingerprint")
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)) {
						// TOGGLE DOOR BY FINGERPRINT / NAME
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)
								|| Permissions.hasPerm(sPlayer, "lock.admin",
								Permissions.NOT_QUIET)) {
							BlockTools.playDigiLockSound(sBlock);
							if (BlockTools.isDoorOpen(sBlock)) {
								BlockTools.closeDoor(sPlayer, sBlock, 0);
							} else {
								BlockTools.openDoor(sPlayer, sBlock,
										lock.getUseCost());
							}
							Messages.sendNotification(sPlayer,
									"Used with fingerprint");
						} else {
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							if (BlockTools.isDoorOpen(sBlock)) {
								BlockTools.playDigiLockSound(sBlock);
								BlockTools.closeDoor(sPlayer, sBlock, 0);
							}
						}
					} else {
						// ASK FOR PINCODE
						if (!BlockTools.isDoorOpen(sBlock)) {
							if (sPlayer.isSpoutCraftEnabled()
									&& Permissions.hasPerm(sPlayer,
									"lock.use",
									Permissions.NOT_QUIET)) {
								LockCore.getPincode(sPlayer, sBlock);
							} else {
								sPlayer.sendMessage("Digilock'ed by "
										+ lock.owner);
							}
						} else {
							BlockTools.closeDoor(sPlayer, sBlock, 0);
							BlockTools.playDigiLockSound(sBlock);
						}
					}
				}

				// HANDLING A LOCKED TRAP_DOOR
				else if (sBlock.getType().equals(Material.TRAP_DOOR)) {
					event.setCancelled(true);
					if (lock.getPincode().equals("")
							|| lock.getPincode()
							.equalsIgnoreCase("fingerprint")
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)) {
						// TOGGLE DOOR BY FINGERPRINT / NAME
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							BlockTools.playDigiLockSound(sBlock);
							if (BlockTools.isTrapdoorOpen(sPlayer, sBlock)) {
								BlockTools.closeTrapdoor(sPlayer, sBlock);
							} else {
								BlockTools.openTrapdoor(sPlayer, sBlock,
										lock.getUseCost());
							}
							Messages.sendNotification(sPlayer,
									"Used with fingerprint");
						} else {
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							if (BlockTools.isTrapdoorOpen(sPlayer, sBlock)) {
								BlockTools.playDigiLockSound(sBlock);
								BlockTools.closeTrapdoor(sPlayer, sBlock);
							}
						}
					} else {
						// ASK FOR PINCODE
						if (!BlockTools.isTrapdoorOpen(sPlayer, sBlock)) {
							if (sPlayer.isSpoutCraftEnabled()
									&& Permissions.hasPerm(sPlayer,
									"lock.use",
									Permissions.NOT_QUIET)) {
								LockCore.getPincode(sPlayer, sBlock);
							} else {
								sPlayer.sendMessage("Digilock'ed by "
										+ lock.owner);
							}
						} else {
							BlockTools.closeTrapdoor(sPlayer, sBlock);
							BlockTools.playDigiLockSound(sBlock);
						}
					}
				}

				// HANDLING A FENCE GATE
				else if (sBlock.getType().equals(Material.FENCE_GATE)) {
					event.setCancelled(true);
					if (lock.getPincode().equals("")
							|| lock.getPincode()
							.equalsIgnoreCase("fingerprint")
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)) {
						// TOGGLE DOOR BY FINGERPRINT / NAME
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							BlockTools.playDigiLockSound(sBlock);
							if (BlockTools.isFenceGateOpen(sPlayer, sBlock)) {
								BlockTools.closeFenceGate(sPlayer, sBlock);
							} else {
								BlockTools.openFenceGate(sPlayer, sBlock,
										lock.getUseCost());
							}
							Messages.sendNotification(sPlayer,
									"Used with fingerprint");
						} else {
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							if (BlockTools.isFenceGateOpen(sPlayer, sBlock)) {
								BlockTools.playDigiLockSound(sBlock);
								BlockTools.closeFenceGate(sPlayer, sBlock);
							}
						}
					} else {
						// ASK FOR PINCODE
						if (!BlockTools.isFenceGateOpen(sPlayer, sBlock)) {
							if (sPlayer.isSpoutCraftEnabled()
									&& Permissions.hasPerm(sPlayer,
									"lock.use",
									Permissions.NOT_QUIET)) {
								LockCore.getPincode(sPlayer, sBlock);
							} else {
								sPlayer.sendMessage("Digilock'ed by "
										+ lock.owner);
							}
						} else {
							BlockTools.closeFenceGate(sPlayer, sBlock);
							BlockTools.playDigiLockSound(sBlock);
						}
					}
				}

				// HANDLING A LOCKED DISPENCER
				else if (sBlock.getType().equals(Material.DISPENSER)) {
					if ((lock.getPincode().equals("") || lock.getPincode()
							.equalsIgnoreCase("fingerprint"))
							&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)) {
						// USE DISPENSER BY FINGERPRINT (playername)
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							Messages.sendNotification(sPlayer,
									"Opened with fingerprint");
							BlockTools.playDigiLockSound(lock.getBlock());
							Dispenser dispenser = (Dispenser) sBlock.getState();
							Inventory inv = dispenser.getInventory();
							sPlayer.openInventoryWindow(inv);
						} else {
							event.setCancelled(true);
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						event.setCancelled(true);
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
								Action.RIGHT_CLICK_BLOCK)
								&& Permissions.hasPerm(sPlayer, "lock.use",
								Permissions.NOT_QUIET)) {
							LockCore.getPincode(sPlayer, sBlock);
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ lock.owner);
						}
					}
				}

				// HANDLING FURNACE
				else if (sBlock.getType().equals(Material.FURNACE)) {
					if ((lock.getPincode().equals("") || lock.getPincode()
							.equalsIgnoreCase("fingerprint")
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET))
							&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						// USE FURNACE BY FINGERPRINT (playername)
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							Messages.sendNotification(sPlayer,
									"Used with fingerprint");
							BlockTools.playDigiLockSound(lock.getBlock());
							Furnace furnace = (Furnace) sBlock.getState();
							Inventory inv = furnace.getInventory();
							sPlayer.openInventoryWindow(inv);
						} else {
							event.setCancelled(true);
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						event.setCancelled(true);
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
								Action.RIGHT_CLICK_BLOCK)
								&& Permissions.hasPerm(sPlayer, "lock.use",
								Permissions.NOT_QUIET)) {
							LockCore.getPincode(sPlayer, sBlock);
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ lock.owner);
						}
					}
				}

				// HANDLING LEVER
				else if (sBlock.getType().equals(Material.LEVER)) {
					Lever lever = (Lever) sBlock.getState().getData();
					SpoutBlock nextLockableBlock = lock.getNextLockableBlock(
							sPlayer, sBlock);
					if (lock.getPincode().equals("")
							|| lock.getPincode()
							.equalsIgnoreCase("fingerprint")
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)) {
						// USE LEVER BY FINGERPRINT
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							if (nextLockableBlock != null) {
								if (BlockTools.isLocked(nextLockableBlock)) {
									Messages.sendNotification(sPlayer,
											"Used with fingerprint");
									BlockTools.playDigiLockSound(sBlock);
									if (lever.isPowered()) {
										BlockTools.leverOff(sPlayer, sBlock);
									} else {
										BlockTools.leverOn(sPlayer, sBlock,
												lock.getUseCost());
									}
									BlockTools.playDigiLockSound(sBlock);
								} else {
									sPlayer.sendMessage("The connected block "
											+ nextLockableBlock.getType()
											+ " is not locked. Please lock it.");
									if (BlockTools.isDoubleDoor(nextLockableBlock)) {
										BlockTools.closeDoubleDoor(sPlayer,
												nextLockableBlock, 0);
									} else if (BlockTools.isDoor(nextLockableBlock)) {
										BlockTools.closeDoor(sPlayer,
												nextLockableBlock, 0);
									} else if (BlockTools.isTrapdoor(nextLockableBlock)) {
										BlockTools.closeTrapdoor(sPlayer,
												nextLockableBlock);
									}
									BlockTools.leverOff(sPlayer, sBlock);
									event.setCancelled(true);
								}
							} else {
								sPlayer.sendMessage("The lever is not connected to anything.");
							}
						} else {
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							event.setCancelled(true);
							BlockTools.leverOff(sPlayer, sBlock);
						}
					} else { // LEVER with pincode
						if (nextLockableBlock != null) {
							if (BlockTools.isLocked(nextLockableBlock)) {
								if (!BlockTools.isLeverOn(sBlock)) {
									if (sPlayer.isSpoutCraftEnabled()
											&& Permissions.hasPerm(sPlayer,
											"lock.use",
											Permissions.NOT_QUIET)) {
										LockCore.getPincode(sPlayer, sBlock);
									} else {
										sPlayer.sendMessage("Digilock'ed by "
												+ lock.owner);
										event.setCancelled(true);
									}
								} else {
									if (BlockTools.isDoubleDoor(nextLockableBlock)) {
										BlockTools.closeDoubleDoor(sPlayer,
												nextLockableBlock, 0);
									} else if (BlockTools.isDoor(nextLockableBlock)) {
										BlockTools.closeDoor(sPlayer,
												nextLockableBlock, 0);
									} else if (BlockTools.isTrapdoor(nextLockableBlock)) {
										BlockTools.closeTrapdoor(sPlayer,
												nextLockableBlock);
									}
									BlockTools.leverOff(sPlayer, sBlock);
								}
							} else {
								sPlayer.sendMessage("The connected block "
										+ nextLockableBlock.getType()
										+ " is not locked. Please lock it.");
								if (BlockTools.isDoubleDoor(nextLockableBlock)) {
									BlockTools.closeDoubleDoor(sPlayer,
											nextLockableBlock, 0);
								} else if (BlockTools.isDoor(nextLockableBlock)) {
									BlockTools.closeDoor(sPlayer,
											nextLockableBlock, 0);
								} else if (BlockTools.isTrapdoor(nextLockableBlock)) {
									BlockTools.closeTrapdoor(sPlayer,
											nextLockableBlock);
								}
								BlockTools.leverOff(sPlayer, sBlock);
								event.setCancelled(true);
							}
						} else {
							sPlayer.sendMessage("The lever is not connected to anything.");
						}
					}
				}

				// HANDLING STONE_BUTTON
				else if (sBlock.getType().equals(Material.STONE_BUTTON)) {
					if (lock.getPincode().equals("")
							|| lock.getPincode()
							.equalsIgnoreCase("fingerprint")
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)) {
						// PRESS STONE_BUTTON BY FINGERPRINT
						// (playername)
						Button button = (Button) sBlock.getState().getData();
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							Messages.sendNotification(sPlayer,
									"Used with fingerprint");
							BlockTools.playDigiLockSound(sBlock);
							if (!button.isPowered()) {
								BlockTools.pressButtonOn(sPlayer, sBlock,
										lock.getUseCost());
							}
						} else {
							event.setCancelled(true);
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						event.setCancelled(true);
						if (sPlayer.isSpoutCraftEnabled()
								&& Permissions.hasPerm(sPlayer, "lock.use",
								Permissions.NOT_QUIET)) {
							LockCore.getPincode(sPlayer, sBlock);
							if (lock.getPincode().equals(
									LockCore.pincodeGUI.get(id).getText())) {
								// okay - go on
							} else {
								// event.setCancelled(true);
							}
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ lock.owner);
							// event.setCancelled(true);
						}
					}
				}

				// HANDLING SIGN and SIGN_POST
				else if (BlockTools.isSign(sBlock)) {
					if ((lock.getPincode().equals("") || lock.getPincode()
							.equalsIgnoreCase("fingerprint"))
							&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)) {
						// USE SIGN BY FINGERPRINT (playername)
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							Messages.sendNotification(sPlayer,
									"Used with fingerprint");
							if (sPlayer.isSpoutCraftEnabled()
									&& Digilock.getConf().useSignGUI()
									&& LockCore.holdingKey.get(id).equals("KEY_LSHIFT")
									&& Permissions.hasPerm(sPlayer,
									"lock.signadmin",
									Permissions.NOT_QUIET)) {
								Sign sign = (Sign) sBlock.getState();
								sPlayer.openSignEditGUI(sign);
							}
						} else {
							event.setCancelled(true);
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
								Action.RIGHT_CLICK_BLOCK)
								&& Permissions.hasPerm(sPlayer, "lock.use",
								Permissions.NOT_QUIET)) {
							LockCore.getPincode(sPlayer, sBlock);
							if (lock.getPincode().equals(
									LockCore.pincodeGUI.get(id).getText())) {
								// okay - go on
							} else {
								// if (G333Config.config.DEBUG_DOOR)
								event.setCancelled(true);
							}
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ lock.owner);
							event.setCancelled(true);
						}
					}
				}

				// BOOKSHELF
				else if ((sBlock.getType().equals(Material.BOOKSHELF))) {
					if ((lock.getPincode().equals("") || lock.getPincode()
							.equalsIgnoreCase("fingerprint"))
							&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)
							&& Permissions.hasPerm(sPlayer, "bookshelf.use",
							Permissions.NOT_QUIET)) {
						// USE SIGN BY FINGERPRINT (playername)
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							Messages.sendNotification(sPlayer,
									"Used with fingerprint");
							LockInventory bitInventory = LockInventory
									.loadBitInventory(sPlayer, sBlock);
							bitInventory.openBitInventory(sPlayer, bitInventory);
						} else {
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
								Action.RIGHT_CLICK_BLOCK)
								&& Permissions.hasPerm(sPlayer, "lock.use",
								Permissions.NOT_QUIET)) {
							LockCore.getPincode(sPlayer, sBlock);
							if (lock.getPincode().equals(
									LockCore.pincodeGUI.get(id).getText())) {
								// okay - go on
							}
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ lock.owner);

							event.setCancelled(true);
						}
					}
					// JUKEBOX
				} else if ((BlockTools.isJukebox(sBlock))) {
					if ((lock.getPincode().equals("") || lock.getPincode()
							.equalsIgnoreCase("fingerprint"))
							&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET)) {
						// USE SIGN BY FINGERPRINT (playername)
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							Messages.sendNotification(sPlayer,
									"Used with fingerprint");
							// BITInventory bitInventory = BITInventory
							// .loadBitInventory(sPlayer, sBlock);
							// bitInventory.openBitInventory(sPlayer, bitInventory);
						} else {
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						event.setCancelled(true);
						Jukebox jukebox = (Jukebox) sBlock.getState();
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
								Action.RIGHT_CLICK_BLOCK)
								&& Permissions.hasPerm(sPlayer, "lock.use",
								Permissions.NOT_QUIET)
								&& ((itemInHand.getTypeId() >= 2256 && itemInHand
								.getTypeId() <= 2266) || jukebox
								.isPlaying())) {
							LockCore.getPincode(sPlayer, sBlock);
							if (lock.getPincode().equals(
									LockCore.pincodeGUI.get(id).getText())) {
								// okay - go on
							}
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ lock.owner);
						}
					}
				}

				// BREWING_STAND
				else if (sBlock.getType().equals(Material.BREWING_STAND)) {
					if ((lock.getPincode().equals("") || lock.getPincode()
							.equalsIgnoreCase("fingerprint")
							&& Permissions.hasPerm(sPlayer, "lock.use",
							Permissions.NOT_QUIET))
							&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						// USE BREWING STAND BY FINGERPRINT (playername)
						if (lock.isOwner(sPlayer)
								|| lock.isCoowner(sPlayer)
								|| lock.isUser(sPlayer)) {
							Messages.sendNotification(sPlayer,
									"Used with fingerprint");
							BlockTools.playDigiLockSound(lock.getBlock());
						} else {
							event.setCancelled(true);
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						event.setCancelled(true);
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
								Action.RIGHT_CLICK_BLOCK)
								&& Permissions.hasPerm(sPlayer, "lock.use",
								Permissions.NOT_QUIET)) {
							LockCore.getPincode(sPlayer, sBlock);
							sPlayer.sendMessage("Locking brewing stand with pincode is not suported yet!");
							//BlockBrewingStand bs = (BlockBrewingStand) lock.getBlock();
							//TODO: open brewing stand / inventory.
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ lock.owner);
						}
					}
				} else {
					sPlayer.sendMessage("ERROR: PlayerListener. Cant handle block:"
							+ sBlock.getType());
				}

				// } else {
				// // the player has not lock.use permission.
				// G333Messages.sendNotification(sPlayer, "Locked with Digilock.");
				// event.setCancelled(true);
				// }

				// ELSE - IT WAS NOT A LOCKED BLOCK
			} else {
				// HANDLING THE DOUBLEDOOR
				if (BlockTools.isDoubleDoor(sBlock)) {
					// if LEFT_CLICK_BLOCK is canceled the double door cant be
					// broken.
					if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						event.setCancelled(true);
						if (BlockTools.isDoubleDoorOpen(sBlock)) {
							BlockTools.closeDoubleDoor(sPlayer, sBlock, 0);
						} else {
							BlockTools.openDoubleDoor(sPlayer, sBlock, 0);
						}
					}
				}
				// HANDLING THE DOOR
				else if (BlockTools.isDoor(sBlock)) {

				}
				// HANDLING TRAP_DOOR
				else if (BlockTools.isTrapdoor(sBlock)) {

				}
				// HANDLING DISPENCER
				else if (BlockTools.isDispenser(sBlock)) {

				}
				// HANDLING FURNACE
				else if (sBlock.getType().equals(Material.FURNACE)) {

				}
				// HANDLING LEVER
				else if (BlockTools.isLever(sBlock)) {
					Lever lever = (Lever) sBlock.getState().getData();
					if (lever.isPowered()) {

						BlockTools.leverOff(sPlayer, sBlock);
					} else {
						BlockTools.leverOn(sPlayer, sBlock, 0);
					}
				}
				// HANDLING STONE_BUTTON
				else if (BlockTools.isButton(sBlock)) {

				}

				// HANDLING SIGN and SIGN_POST
				else if (BlockTools.isSign(sBlock)) {
					if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
							&& Digilock.getConf().useSignGUI()
							&& LockCore.holdingKey.get(id).equals("KEY_LSHIFT")
							&& Permissions.hasPerm(sPlayer,
							"lock.signadmin",
							Permissions.NOT_QUIET)) {
						Sign sign = (Sign) sBlock.getState();
						sPlayer.openSignEditGUI(sign);
					}
				}
			}
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		int id = event.getPlayer().getEntityId();
		LockCore.addUserData(id);
	}

	public void onPlayerKick(PlayerKickEvent event) {
		int id = event.getPlayer().getEntityId();
		LockCore.removeUserData(id);
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		int id = event.getPlayer().getEntityId();
		LockCore.addUserData(id);
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		int id = event.getPlayer().getEntityId();
		LockCore.removeUserData(id);
	}
}
