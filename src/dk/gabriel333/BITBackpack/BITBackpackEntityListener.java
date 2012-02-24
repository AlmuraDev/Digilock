package dk.gabriel333.BITBackpack;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.BITPermissions;

public class BITBackpackEntityListener extends EntityListener {
	@SuppressWarnings("unused")
	private BIT plugin;

	public BITBackpackEntityListener(BIT plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (!BITPermissions.hasPerm(player, "backpack.nodrop",
					BITPermissions.QUIET)) {
				if (BITBackpack.canOpenBackpack(player.getWorld(), player)) {
					// if (!((SpoutPlayer) player).isSpoutCraftEnabled()) {
					BITBackpack.loadInventory(player, player.getWorld());
					// }
					if (BITBackpack.inventories.containsKey(player.getName())) {
						ItemStack[] items = BITBackpack.inventories.get(player
								.getName());
						for (ItemStack item : items) {
							if (item != null && item.getAmount() > 0) {
								player.getWorld().dropItem(
										player.getLocation(), item);
							}
						}
						Inventory inventory = SpoutManager
								.getInventoryBuilder()
								.construct(
										BITBackpack
												.allowedSize(player.getWorld(),
														player, true),
										BITBackpack.inventoryName);
						for (Integer i = 0; i < BITBackpack.allowedSize(
								player.getWorld(), player, true); i++) {
							ItemStack item = new ItemStack(0, 0);
							inventory.setItem(i, item);
						}
						BITBackpack.inventories.put(player.getName(),
								inventory.getContents());
						BITBackpackInventorySaveTask.saveInventory(player,
								player.getWorld());
						player.sendMessage(BIT.li.getMessage("your")
								+ ChatColor.RED + BITBackpack.inventoryName + ChatColor.WHITE
								+ BIT.li.getMessage("hasbroken"));
					}
				}
			} else {
				
			}
		}
	}
}