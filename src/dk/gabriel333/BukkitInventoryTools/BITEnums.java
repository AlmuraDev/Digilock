package dk.gabriel333.BukkitInventoryTools;

public class BITEnums {

	public enum BITCustom_Screen {
		GETPINCODE_SCREEN, SETPINCODE_SCREEN, CREATEBOOKSHELF_SCREEN
	}

	public enum InventoryType {
		PLAYER_INVENTORY(1, "Player"), SPOUTBACKPACK_INVENTORY(2, "Backpack"), CHEST_INVENTORY(
				3, "Chest"), BOOKSHELF_INVENTORY(4, "Bookshelf");

		private final int type;
		private final String desc;

		InventoryType(int type, String desc) {
			this.type = type;
			this.desc = desc;
		}

		public int inventoryTypeId() {
			return this.type;
		}

		public String description() {
			return this.desc;
		}

		// Example
		// public static void main(String[] args){
		// for (InventoryType i: InventoryType.values()){
		// System.out.println(i + "value is "+ new Integer(i.inventoryType()) +
		// " desc is " + i.description());
		// }
	}

}
