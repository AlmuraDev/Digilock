package dk.gabriel333.BukkitInventoryTools.DigiLock;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;
import dk.gabriel333.Library.BITPermissions;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BITPlayerListener implements Listener {

        @EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (event.isCancelled())
			return;
		if (BITConfig.DEBUG_ONENABLE) {
			event.getPlayer().sendMessage("BITPlayerListener - return(1)");
			return;
		}
		// DOORS, DOUBLEDOORS, TRAPDOORS LEVERS, BUTTON can be handled with both
		// mousebuttons, the rest is only with RIGHT_CLICK
		if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event
				.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getClickedBlock();
		// for faster handling
		if (!BlockTools.isLockable(sBlock)) {
			return;
		}
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		ItemStack itemInHand = sPlayer.getInventory().getItemInHand();
		// sPlayer.sendMessage("BITPlayerListener:"+itemInHand.getTypeId());
		if (sBlock.getType() == Material.BOOKSHELF
				&& sBlock.getType() == itemInHand.getType()
				// && !BIT.holdingKey.equals("L-CONTROL"))
				&& !(BlockTools.isLocked(sBlock) || BITInventory
						.isBitInventoryCreated(sBlock))) {
			// This allows the user to place a new Bookshelf on a Bookshelf
			// where the Inventory is created.

			return;
		}
		if (sBlock.getType() == Material.BOOKSHELF
				&& sBlock.getType() == itemInHand.getType()
				&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				&& (BlockTools.isLocked(sBlock) || BITInventory
						.isBitInventoryCreated(sBlock))) {
			event.setCancelled(true);
		}
		int id = sPlayer.getEntityId();

		if (BITConfig.DEBUG_GUI)
			sPlayer.sendMessage("BITPlayerListener:" + " Your action was:"
					+ event.getAction() + " on sBlock:" + sBlock.getType()
					+ " with:" + itemInHand.getType() + " while holding:"
					+ BIT.holdingKey.get(id));

		// Call setPincode
		if (sPlayer.isSpoutCraftEnabled()
				&& BlockTools.isLockable(sBlock)
				&& BIT.holdingKey.get(id).equals("KEY_LCONTROL")
				&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				&& (BITPermissions.hasPerm(sPlayer, "digilock.create",
						BITPermissions.NOT_QUIET) || BITPermissions.hasPerm(
						sPlayer, "digilock.admin", BITPermissions.NOT_QUIET))) {

			event.setCancelled(true);
			if (BlockTools.isLocked(sBlock)) {
				BITDigiLock digilock = BlockTools.loadDigiLock(sBlock);
				if (digilock.isOwner(sPlayer)
						|| digilock.isCoowner(sPlayer)
						|| BITPermissions.hasPerm(sPlayer, "digilock.admin",
								BITPermissions.NOT_QUIET)) {
					BITDigiLock.setPincode(sPlayer, sBlock);
				} else {
					sPlayer.sendMessage("You are not the owner or coowner");
				}
			} else {
				BITDigiLock.setPincode(sPlayer, sBlock);
			}

			// Call openEditSignGUI
		} else

		// HANDLING THAT PLAYER CLICK ON A BLOCK WITH A DIGILOCK
		if (BlockTools.isLocked(sBlock)) {
			BITDigiLock digilock = BlockTools.loadDigiLock(sBlock);

			// HANDLING A LOCKED CHEST AND DOUBLECHEST
			if (BlockTools.isChest(sBlock)) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint"))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)) {
					// OPEN CHEST BY FINGERPRINT / NAME
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						SpoutChest sChest = (SpoutChest) sBlock.getState();
						Inventory inv = sChest.getLargestInventory();
						BITMessages.sendNotification(sPlayer,
								"Opened by fingerprint");
						BlockTools.playDigiLockSound(sBlock);
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
							&& BITPermissions.hasPerm(sPlayer, "digilock.use",
									BITPermissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
					} else {
						sPlayer.sendMessage("Locked with Digilock by "+digilock.owner);
					}
				}
			}

			// HANDLING A LOCKED DOUBLEDOOR
			else if (BlockTools.isDoubleDoor(sBlock)) {
				event.setCancelled(true);
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)) {
					// TOGGLE DOOR BY FINGERPRINT / NAME
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer) 
							|| BITPermissions.hasPerm(sPlayer, "digilock.admin",
								BITPermissions.NOT_QUIET)){
						BlockTools.playDigiLockSound(sBlock);
						if (BlockTools.isDoubleDoorOpen(sBlock)) {
							BlockTools.closeDoubleDoor(sPlayer, sBlock, 0);
						} else {
							BlockTools.openDoubleDoor(sPlayer, sBlock,
									digilock.getUseCost());
						}
						BITMessages.sendNotification(sPlayer,
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
							&& BITPermissions.hasPerm(sPlayer, "digilock.use",
									BITPermissions.NOT_QUIET)) {
						if (sPlayer.isSpoutCraftEnabled()) {
							BITDigiLock.getPincode(sPlayer,
									BlockTools.getLeftDoubleDoor(sBlock));
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ digilock.owner);
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
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)) {
					// TOGGLE DOOR BY FINGERPRINT / NAME
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)
							|| BITPermissions.hasPerm(sPlayer, "digilock.admin",
								BITPermissions.NOT_QUIET)){
						BlockTools.playDigiLockSound(sBlock);
						if (BlockTools.isDoorOpen(sBlock)) {
							BlockTools.closeDoor(sPlayer, sBlock, 0);
						} else {
							BlockTools.openDoor(sPlayer, sBlock,
									digilock.getUseCost());
						}
						BITMessages.sendNotification(sPlayer,
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
								&& BITPermissions.hasPerm(sPlayer,
										"digilock.use",
										BITPermissions.NOT_QUIET)) {
							BITDigiLock.getPincode(sPlayer, sBlock);
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ digilock.owner);
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
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)) {
					// TOGGLE DOOR BY FINGERPRINT / NAME
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BlockTools.playDigiLockSound(sBlock);
						if (BlockTools.isTrapdoorOpen(sPlayer, sBlock)) {
							BlockTools.closeTrapdoor(sPlayer, sBlock);
						} else {
							BlockTools.openTrapdoor(sPlayer, sBlock,
									digilock.getUseCost());
						}
						BITMessages.sendNotification(sPlayer,
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
								&& BITPermissions.hasPerm(sPlayer,
										"digilock.use",
										BITPermissions.NOT_QUIET)) {
							BITDigiLock.getPincode(sPlayer, sBlock);
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ digilock.owner);
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
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)) {
					// TOGGLE DOOR BY FINGERPRINT / NAME
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BlockTools.playDigiLockSound(sBlock);
						if (BlockTools.isFenceGateOpen(sPlayer, sBlock)) {
							BlockTools.closeFenceGate(sPlayer, sBlock);
						} else {
							BlockTools.openFenceGate(sPlayer, sBlock,
									digilock.getUseCost());
						}
						BITMessages.sendNotification(sPlayer,
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
								&& BITPermissions.hasPerm(sPlayer,
										"digilock.use",
										BITPermissions.NOT_QUIET)) {
							BITDigiLock.getPincode(sPlayer, sBlock);
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ digilock.owner);
						}
					} else {
						BlockTools.closeFenceGate(sPlayer, sBlock);
						BlockTools.playDigiLockSound(sBlock);
					}
				}
			}

			// HANDLING A LOCKED DISPENCER
			else if (sBlock.getType().equals(Material.DISPENSER)) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint"))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)) {
					// USE DISPENSER BY FINGERPRINT (playername)
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BITMessages.sendNotification(sPlayer,
								"Opened with fingerprint");
						BlockTools.playDigiLockSound(digilock.getBlock());
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
							&& BITPermissions.hasPerm(sPlayer, "digilock.use",
									BITPermissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
					} else {
						sPlayer.sendMessage("Digilock'ed by "
								+ digilock.owner);
					}
				}
			}

			// HANDLING FURNACE
			else if (sBlock.getType().equals(Material.FURNACE)) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint")
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					// USE FURNACE BY FINGERPRINT (playername)
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BITMessages.sendNotification(sPlayer,
								"Used with fingerprint");
						BlockTools.playDigiLockSound(digilock.getBlock());
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
							&& BITPermissions.hasPerm(sPlayer, "digilock.use",
									BITPermissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
					} else {
						sPlayer.sendMessage("Digilock'ed by "
								+ digilock.owner);
					}
				}
			}

			// HANDLING LEVER
			else if (sBlock.getType().equals(Material.LEVER)) {
				Lever lever = (Lever) sBlock.getState().getData();
				SpoutBlock nextLockableBlock = digilock.getNextLockableBlock(
						sPlayer, sBlock);
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)) {
					// USE LEVER BY FINGERPRINT
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						if (nextLockableBlock != null) {
							if (BlockTools.isLocked(nextLockableBlock)) {
								BITMessages.sendNotification(sPlayer,
										"Used with fingerprint");
								BlockTools.playDigiLockSound(sBlock);
								if (lever.isPowered()) {
									BlockTools.leverOff(sPlayer, sBlock);
								} else {
									BlockTools.leverOn(sPlayer, sBlock,
											digilock.getUseCost());
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
										&& BITPermissions.hasPerm(sPlayer,
												"digilock.use",
												BITPermissions.NOT_QUIET)) {
									BITDigiLock.getPincode(sPlayer, sBlock);
								} else {
									sPlayer.sendMessage("Digilock'ed by "
											+ digilock.owner);
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
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)) {
					// PRESS STONE_BUTTON BY FINGERPRINT
					// (playername)
					Button button = (Button) sBlock.getState().getData();
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BITMessages.sendNotification(sPlayer,
								"Used with fingerprint");
						BlockTools.playDigiLockSound(sBlock);
						if (!button.isPowered()) {
							BlockTools.pressButtonOn(sPlayer, sBlock,
									digilock.getUseCost());
						}
					} else {
						event.setCancelled(true);
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
					}
				} else {
					event.setCancelled(true);
					if (sPlayer.isSpoutCraftEnabled()
							&& BITPermissions.hasPerm(sPlayer, "digilock.use",
									BITPermissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
						if (digilock.getPincode().equals(
								BITDigiLock.pincodeGUI.get(id).getText())) {
							// okay - go on
						} else {
							// event.setCancelled(true);
						}
					} else {
						sPlayer.sendMessage("Digilock'ed by "
								+ digilock.owner);
						// event.setCancelled(true);
					}
				}
			}

			// HANDLING SIGN and SIGN_POST
			else if (BlockTools.isSign(sBlock)) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint"))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)) {
					// USE SIGN BY FINGERPRINT (playername)
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BITMessages.sendNotification(sPlayer,
								"Used with fingerprint");
						if (sPlayer.isSpoutCraftEnabled()
								&& BITConfig.LIBRARY_USESIGNEDITGUI
								&& BIT.holdingKey.get(id).equals("KEY_LSHIFT")
								&& BITPermissions.hasPerm(sPlayer,
										"digilock.signadmin",
										BITPermissions.NOT_QUIET)) {
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
							&& BITPermissions.hasPerm(sPlayer, "digilock.use",
									BITPermissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
						if (digilock.getPincode().equals(
								BITDigiLock.pincodeGUI.get(id).getText())) {
							// okay - go on
						} else {
							// if (G333Config.config.DEBUG_DOOR)
							event.setCancelled(true);
						}
					} else {
						sPlayer.sendMessage("Digilock'ed by "
								+ digilock.owner);
						event.setCancelled(true);
					}
				}
			}

			// BOOKSHELF
			else if ((sBlock.getType().equals(Material.BOOKSHELF))) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint"))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)
						&& BITPermissions.hasPerm(sPlayer, "bookshelf.use",
								BITPermissions.NOT_QUIET)) {
					// USE SIGN BY FINGERPRINT (playername)
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BITMessages.sendNotification(sPlayer,
								"Used with fingerprint");
						BITInventory bitInventory = BITInventory
								.loadBitInventory(sPlayer, sBlock);
						bitInventory.openBitInventory(sPlayer, bitInventory);
					} else {
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
					}
				} else {
					if (sPlayer.isSpoutCraftEnabled()
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)
							&& BITPermissions.hasPerm(sPlayer, "digilock.use",
									BITPermissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
						if (digilock.getPincode().equals(
								BITDigiLock.pincodeGUI.get(id).getText())) {
							// okay - go on
						}
					} else {
						sPlayer.sendMessage("Digilock'ed by "
								+ digilock.owner);

						event.setCancelled(true);
					}
				}
				// JUKEBOX
			} else if ((BlockTools.isJukebox(sBlock))) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint"))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET)) {
					// USE SIGN BY FINGERPRINT (playername)
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BITMessages.sendNotification(sPlayer,
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
							&& BITPermissions.hasPerm(sPlayer, "digilock.use",
									BITPermissions.NOT_QUIET)
							&& ((itemInHand.getTypeId() >= 2256 && itemInHand
									.getTypeId() <= 2266) || jukebox
									.isPlaying())) {
						BITDigiLock.getPincode(sPlayer, sBlock);
						if (digilock.getPincode().equals(
								BITDigiLock.pincodeGUI.get(id).getText())) {
							// okay - go on
						}
					} else {
						sPlayer.sendMessage("Digilock'ed by "
								+ digilock.owner);
					}
				}
			}

			// BREWING_STAND
			else if (sBlock.getType().equals(Material.BREWING_STAND)) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint")
						&& BITPermissions.hasPerm(sPlayer, "digilock.use",
								BITPermissions.NOT_QUIET))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					// USE BREWING STAND BY FINGERPRINT (playername)
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BITMessages.sendNotification(sPlayer,
								"Used with fingerprint");
						BlockTools.playDigiLockSound(digilock.getBlock());
					} else {
						event.setCancelled(true);
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
					}
				} else {
					event.setCancelled(true);
					if (sPlayer.isSpoutCraftEnabled()
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)
							&& BITPermissions.hasPerm(sPlayer, "digilock.use",
									BITPermissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
						sPlayer.sendMessage("Locking brewing stand with pincode is not suported yet!");
						//BlockBrewingStand bs = (BlockBrewingStand) digilock.getBlock();
						//TODO: open brewing stand / inventory.
					} else {
						sPlayer.sendMessage("Digilock'ed by "
								+ digilock.owner);
					}
				}
			}

			else {
				sPlayer.sendMessage("ERROR: BITPlayerListener. Cant handle block:"
						+ sBlock.getType());
			}

			// } else {
			// // the player has not digilock.use permission.
			// G333Messages.sendNotification(sPlayer, "Locked with Digilock.");
			// event.setCancelled(true);
			// }

			// ELSE - IT WAS NOT A LOCKED BLOCK
		} else {

			if (BITConfig.DEBUG_GUI) {
				sPlayer.sendMessage("There is no digilock on this block");

			}
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
						&& BITConfig.LIBRARY_USESIGNEDITGUI
						&& BIT.holdingKey.get(id).equals("KEY_LSHIFT")
						&& BITPermissions.hasPerm(sPlayer,
								"digilock.signadmin",
								BITPermissions.NOT_QUIET)) {
					Sign sign = (Sign) sBlock.getState();
					sPlayer.openSignEditGUI(sign);
				}
			}

			// BOOKSHELF
			else if (BlockTools.isBookshelf(sBlock)
					&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
					&& !BIT.holdingKey.get(id).equals("KEY_LCONTROL")
					&& (BITConfig.BOOKSHELF_ENABLE))   {  //  Added this option to config to prevent BOOKWORM Conflict

				if (BITInventory.isBitInventoryCreated(sBlock)
						&& BITPermissions.hasPerm(sPlayer, "bookshelf.use",
								BITPermissions.NOT_QUIET)) {
					BITInventory bitInventory = BITInventory.loadBitInventory(
							sPlayer, sBlock);
					bitInventory.openBitInventory(sPlayer, bitInventory);
				} else if (!BITInventory.isBitInventoryCreated(sBlock)
						&& BITPermissions.hasPerm(sPlayer, "bookshelf.create",
								BITPermissions.NOT_QUIET)) {
					BITInventory.setBookshelfInventory(sPlayer, sBlock);
				}

			}
			// BREWING STAND
			else if (sBlock.getType().equals(Material.BREWING_STAND)) {

			}
		}
	}

        @EventHandler
	public void onItemHeldChange(PlayerItemHeldEvent event) {
		if (BITConfig.DEBUG_GUI) {
			SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
			sPlayer.sendMessage("Event:" + event.getEventName() );
		}

		// ItemStack item =
		// event.getPlayer().getInventory().getItem(event.getNewSlot());
		// if (item != null && item.getType() == Material.BOOK &&
		// item.getDurability() != 0) {
		// sBook book = plugin.getBookById(item.getDurability());
		// if (book != null) {
		// event.getPlayer().sendMessage(BookWorm.TEXT_COLOR +
		// BookWorm.S_READ_BOOK + ": " + BookWorm.TEXT_COLOR_2 +
		// book.getTitle());
		// }
		// }
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		int id = event.getPlayer().getEntityId();
		BIT.addUserData(id);
	}

	public void onPlayerKick(PlayerKickEvent event) {
		int id = event.getPlayer().getEntityId();
		BIT.removeUserData(id);
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		int id = event.getPlayer().getEntityId();
		BIT.addUserData(id);
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		int id = event.getPlayer().getEntityId();
		BIT.removeUserData(id);
	}

}
