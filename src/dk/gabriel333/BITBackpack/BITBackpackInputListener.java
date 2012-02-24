package dk.gabriel333.BITBackpack;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet100OpenWindow;
//import net.minecraft.server.Packet101CloseWindow;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BITBackpack.BITBackpack;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITPermissions;

public class BITBackpackInputListener extends InputListener {

	public BIT plugin;

	public BITBackpackInputListener(BIT plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		String keypressed = event.getKey().name();
		ScreenType screentype = event.getScreenType();
		SpoutPlayer sPlayer = event.getPlayer();
		if (!(screentype == ScreenType.GAME_SCREEN
				|| screentype == ScreenType.CHEST_INVENTORY
				|| screentype == ScreenType.PLAYER_INVENTORY
				|| screentype == ScreenType.DISPENSER_INVENTORY
				|| screentype == ScreenType.FURNACE_INVENTORY || screentype == ScreenType.WORKBENCH_INVENTORY)) {
			return;
		}
		if (keypressed.equalsIgnoreCase(BITConfig.LIBRARY_BACKPACK)) {
			if (BITPermissions.hasPerm(sPlayer, "backpack.use",
					BITPermissions.NOT_QUIET)) {
				if (BITBackpack.sizeInConfig(sPlayer.getWorld(), sPlayer) > 0 || !BIT.useEconomy) {
					if (!BITBackpack.canOpenBackpack(sPlayer.getWorld(),
							sPlayer)) {
						sPlayer.sendMessage("You are not allowed to open the backpack here.");
						return;
					} else {
						if (screentype == ScreenType.CHEST_INVENTORY) {
							if (!BITBackpack.openedInventoriesOthers
									.containsKey(sPlayer.getName())) {
								if (BITBackpack.openedInventories
										.containsKey(sPlayer.getName())) {
									Inventory inv = BITBackpack.openedInventories
											.get(sPlayer.getName());
									BITBackpack.inventories.put(
											sPlayer.getName(),
											inv.getContents());
									sPlayer.closeActiveWindow();
									removeBalanceWidget(sPlayer);
								}
							} else {
								if (BITBackpack.openedInventories
										.containsKey(BITBackpack.openedInventoriesOthers
												.get(sPlayer.getName()))) {
									Inventory inv = BITBackpack.openedInventories
											.get(BITBackpack.openedInventoriesOthers
													.get(sPlayer.getName()));
									BITBackpack.inventories.put(
											BITBackpack.openedInventoriesOthers
													.get(sPlayer.getName()),
											inv.getContents());
									BITBackpack.openedInventoriesOthers
											.remove(sPlayer.getName());
									sPlayer.closeActiveWindow();
									removeBalanceWidget(sPlayer);
								}
							}
						}
						openBackpack(sPlayer, screentype);
					}
				} else {
						sPlayer.sendMessage("You need to buy a backpack. Use /backpack buy");
				}
			}
		} else if (keypressed.equalsIgnoreCase(BITConfig.LIBRARY_WORKBENCH)) {
			if (BITPermissions.hasPerm(event.getPlayer(), "backpack.workbench",
					BITPermissions.NOT_QUIET)) {
				if (BITConfig.SBP_workbenchEnabled) {
					if (!BITBackpack.openedInventoriesOthers.containsKey(event
							.getPlayer().getName())) {
						if (!BITBackpack.hasWorkbench(event.getPlayer())) {
							return;
						}
						if (BITConfig.SBP_workbenchInventory
								&& !event.getPlayer().getInventory()
										.contains(Material.WORKBENCH)) {
							return;
						}
						final int windowNumber = 1;
						if (screentype == ScreenType.WORKBENCH_INVENTORY) {
							// SpoutPlayer player = event.getPlayer();
							// final EntityPlayer entityPlayer = ((CraftPlayer)
							// player)
							// .getHandle();
							// TODO: save state of items when leaving window

							sPlayer.closeActiveWindow();
							removeBalanceWidget(sPlayer);

							// entityPlayer.netServerHandler
							// .sendPacket(new Packet101CloseWindow(
							// windowNumber));
						} else if (screentype == ScreenType.GAME_SCREEN
								|| screentype == ScreenType.PLAYER_INVENTORY
								|| screentype == ScreenType.DISPENSER_INVENTORY
								|| screentype == ScreenType.FURNACE_INVENTORY
								|| screentype == ScreenType.CHEST_INVENTORY) {
							// SpoutPlayer player = event.getPlayer();
							if (BITBackpack.openedInventories
									.containsKey(sPlayer.getName())) {
								BITBackpack.openedInventories.remove(sPlayer
										.getName());
								sPlayer.closeActiveWindow();
								removeBalanceWidget(sPlayer);
							}
							final EntityPlayer entityPlayer = ((CraftPlayer) sPlayer)
									.getHandle();
							entityPlayer.netServerHandler
									.sendPacket(new Packet100OpenWindow(
											windowNumber, 1, "Crafting", 9));
							entityPlayer.activeContainer = new BITWorkbench(
									entityPlayer, windowNumber);
							addBalanceWidget(sPlayer);
						}
					}
				} else {
					if (BITConfig.DEBUG_PERMISSIONS) {
						event.getPlayer().sendMessage(
								"SBP_workbenchEnabled is false in config.yml");
					}
				}
			}
		} else if (keypressed.equalsIgnoreCase(BITConfig.LIBRARY_SORTKEY)) {

			// The player pressed KEY_E
		} else if (keypressed.equalsIgnoreCase(sPlayer.getInventoryKey()
				.toString())) {
			if (screentype == ScreenType.GAME_SCREEN
					|| screentype == ScreenType.PLAYER_INVENTORY
					|| screentype == ScreenType.DISPENSER_INVENTORY
					|| screentype == ScreenType.FURNACE_INVENTORY
					|| screentype == ScreenType.WORKBENCH_INVENTORY
					|| screentype == ScreenType.CHEST_INVENTORY) {
				if (BITBackpack.openedInventories
						.containsKey(sPlayer.getName())) {
					BITBackpack.openedInventories.remove(sPlayer.getName());
				}
				if (screentype != ScreenType.GAME_SCREEN) {
					sPlayer.closeActiveWindow();
					removeBalanceWidget(sPlayer);
				} else {
					sPlayer.openScreen(ScreenType.PLAYER_INVENTORY);
					addBalanceWidget(sPlayer);
				}
			}
		}
	}

	public void removeBalanceWidget(SpoutPlayer sPlayer) {
		if (BITBackpack.widgets.containsKey(sPlayer.getName())
				&& BITConfig.SBP_useWidget) {
			BITBackpack.widgets.get(sPlayer.getName()).setVisible(false)
					.setDirty(true);
			BITBackpack.widgets.remove(sPlayer.getName());
		}
	}

	public void addBalanceWidget(SpoutPlayer sPlayer) {
		if (plugin.Method != null && BITConfig.SBP_useWidget) {
			if (BITBackpack.widgets.containsKey(sPlayer.getName())
					&& BITConfig.SBP_useWidget) {
				BITBackpack.widgets.get(sPlayer.getName()).setVisible(true)
						.setDirty(true);
			} else {
				if (!plugin.Method.hasAccount(sPlayer.getName())) {
					return;
				}
				GenericLabel widget = new GenericLabel("");
				widget.setText(
						BIT.li.getMessage("money")
								+ String.format(plugin.Method
										.format(plugin.Method.getAccount(
												sPlayer.getName()).balance())))
						.setTextColor(new Color(1.0F, 1.0F, 1.0F, 1.0F))
						.setX(BITConfig.SBP_widgetX)
						.setY(BITConfig.SBP_widgetY).setHeight(15).setWidth(50)
						.setFixed(false);
				sPlayer.getMainScreen().attachWidget(plugin, widget);
				BITBackpack.widgets.put(sPlayer.getName(), widget);
			}
		}
	}

	public void openBackpack(SpoutPlayer sPlayer, ScreenType screentype) {
		if (!BITBackpack.openedInventoriesOthers.containsValue(sPlayer
				.getName())) {
			if (screentype == ScreenType.GAME_SCREEN
					|| screentype == ScreenType.PLAYER_INVENTORY
					|| screentype == ScreenType.DISPENSER_INVENTORY
					|| screentype == ScreenType.FURNACE_INVENTORY
					|| screentype == ScreenType.WORKBENCH_INVENTORY) {
				if (BIT.useEconomy) {
					addBalanceWidget(sPlayer);
				}
				BITBackpack.loadInventory(sPlayer, sPlayer.getWorld());
				Inventory inv = SpoutManager.getInventoryBuilder().construct(
						BITBackpack.inventories.get(sPlayer.getName()).length,
						BITBackpack.inventoryName);
				if (BITBackpack.inventories.containsKey(sPlayer.getName())) {
					inv.setContents(BITBackpack.inventories.get(sPlayer
							.getName()));
					BITBackpack.openedInventories.put(sPlayer.getName(), inv);
				}
				sPlayer.openInventoryWindow(inv);
			}
		} else {
			sPlayer.sendMessage(BIT.li.getMessage("someoneisusingyour")
					+ ChatColor.RED + BITBackpack.inventoryName
					+ ChatColor.WHITE + BIT.li.getMessage("!"));
		}
	}

}