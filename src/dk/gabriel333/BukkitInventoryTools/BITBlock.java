package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Material;
import org.getspout.spout.block.SpoutCraftBlock;
import org.getspout.spout.block.SpoutCraftChunk;

public class BITBlock extends SpoutCraftBlock {
	

	public BITBlock(SpoutCraftChunk chunk, int x, int y, int z) {
		super(chunk, x, y, z);
		// TODO Auto-generated constructor stub
	}

	public boolean isBookshelf() {
		if (this.getType().equals(Material.BOOKSHELF)) {
			return true;
		} else {
			return false;
		}
	}
	
	protected final static Material writeableMaterials[] = { Material.BOOK,
		Material.PAINTING, Material.PAPER, Material.MAP, Material.SIGN,
		Material.SIGN_POST, Material.WALL_SIGN };
	
	public boolean isWriteable() {
		for (Material i : writeableMaterials) {
			if (i == this.getType())
				return true;
		}
		return false;
	}
}
