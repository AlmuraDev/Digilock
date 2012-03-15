package dk.gabriel333.BukkitInventoryTools.DigiLock;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;
import dk.gabriel333.Library.BITPermissions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Lever;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BITDigiLockKeyHandler implements BindingExecutionDelegate {

    public BIT plugin;
    public Keyboard key;

    public BITDigiLockKeyHandler(BIT plugin, Keyboard key) {
        this.plugin = plugin;
    }

    @Override
    public void keyPressed(KeyBindingEvent event) {
    }


    @Override
    public void keyReleased(KeyBindingEvent event) {
        SpoutPlayer sPlayer = event.getPlayer();
        ScreenType screentype = event.getScreenType();
        SpoutBlock targetblock = (SpoutBlock) sPlayer.getTargetBlock(null, 4);
        // External SpoutBackpack

        // Remove broken DigiLocks
        if (BlockTools.isLocked(targetblock)
                && !BlockTools.isLockable(targetblock)) {
            BITDigiLock digilock = BlockTools.loadDigiLock(targetblock);
            digilock.RemoveDigiLock(sPlayer);
            sPlayer.sendMessage("Warning: You had an DigiLock on a illegal block. The DigiLock has been removed.");
            sPlayer.sendMessage("Make a ticket and tell the developer how it happened on:");
            sPlayer.sendMessage("http://dev.bukkit.org/server-mods/bukkitinventorytools/tickets/");
        }

        // GAME_SCREEN
        else if (BlockTools.isLockable(targetblock)) {
            if (screentype == ScreenType.GAME_SCREEN) {
                if ((BITPermissions.hasPerm(sPlayer, "digilock.create",
                                            BITPermissions.QUIET) || BITPermissions
                        .hasPerm(sPlayer, "digilock.admin",
                                 BITPermissions.QUIET))) {
                    if (BlockTools.isLocked(targetblock)) {
                        BITDigiLock digilock = BlockTools.loadDigiLock(targetblock);
                        if (BlockTools.isDoubleDoor(targetblock)) {
                            BlockTools.closeDoubleDoor(sPlayer, targetblock, 0);
                        } else if (BlockTools.isDoor(targetblock)) {
                            BlockTools.closeDoor(sPlayer, targetblock, 0);
                        } else if (BlockTools.isTrapdoor(targetblock)) {
                            BlockTools.closeTrapdoor(sPlayer, targetblock);
                        }
                        if (sPlayer.getName().equals(digilock.getOwner())) {
                            BITMessages.sendNotification(sPlayer,
                                                         "You are the owner");
                            BITDigiLock.setPincode(sPlayer, targetblock);
                        } else {
                            BITMessages.sendNotification(sPlayer,
                                                         "Locked with Digilock");
                        }
                    } else { // TARGETBLOCK IS NOT LOCKED
                        if (sPlayer.isSpoutCraftEnabled()) {
                            if (BlockTools.isBookshelf(targetblock)) {
                                if (!BITInventory
                                        .isBitInventoryCreated(targetblock)) {
                                    String coowners = "";
                                    String name = "";
                                    String owner = sPlayer.getName();
                                    int usecost = 0;
                                    Inventory inventory = SpoutManager
                                                          .getInventoryBuilder().construct(
                                                              BITConfig.BOOKSHELF_SIZE,
                                                              name);
                                    BITInventory.saveBitInventory(sPlayer,
                                                                  targetblock, owner, name, coowners,
                                                                  inventory, usecost);
                                }
                            } else if (BlockTools.isDoubleDoor(targetblock)) {
                                SpoutBlock leftdoor = BlockTools.getLeftDoubleDoor(targetblock);
                                BlockTools.closeDoubleDoor(sPlayer, leftdoor,
                                                           0);
                                BITDigiLock.setPincode(sPlayer, leftdoor);
                            } else if (BlockTools.isDoor(targetblock)) {
                                BlockTools.closeDoor(targetblock);
                                BITDigiLock.setPincode(sPlayer, targetblock);
                            } else {
                                BITDigiLock.setPincode(sPlayer, targetblock);
                            }
                        } else {
                            sPlayer.sendMessage("Install SpoutCraft or use command /dlock to create lock.");
                        }

                    }
                }
            }
        }
        /*
        		// CUSTOM_SCREEN
        		else if (screentype == ScreenType.CUSTOM_SCREEN) {
        			if (keypressed.equals("KEY_ESCAPE") || keypressed.equals("KEY_E")) {
        				// TODO: the lever must swing back to off, when the
        				// player press ESC. Next lines does not work. :-(
        				// if (BlockTools.isLever(targetblock)) {
        				// if ( BlockTools.isLeverOn(targetblock)) {
        				// BlockTools.leverOff(sPlayer, targetblock);
        				// } else {
        				// BlockTools.leverOn(sPlayer, targetblock,0);
        				// }
        				// sPlayer.sendMessage("setting lever to off");
        				// Lever lever = (Lever) targetblock.getState().getData();
        				// lever.setPowered(false);
        				// }
        				if (BlockTools.isLever(targetblock)) {
        					Lever lever = (Lever) targetblock.getState().getData();
        					// lever.setPowered(false);
        					targetblock.setData((byte) (lever.getData() | 8));
        				}
        				sPlayer.closeActiveWindow();
        				BlockTools.cleanupPopupScreen(sPlayer);

        			} else if (keypressed.equals("KEY_RETURN")) {

        			}
        		}

        		else {
        			// UNHANDLED SCREENTYPE
        		}
        */
    }
    /*
    @EventHandler
    public void onKeyReleasedEvent(KeyReleasedEvent event) {
    	// SpoutPlayer sPlayer = event.getPlayer();
    	// Keyboard keyUp = event.getKey();
    	// event.getPlayer().sendMessage(
    	// "sPlayer:" + sPlayer.getName() + "Pressed key:" + keyUp);
    }

    @EventHandler
    public void onRenderDistanceChange(RenderDistanceChangeEvent event) {

    }
           */
}
