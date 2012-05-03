package com.almuramc.digilock.gui;

import org.bukkit.ChatColor;

import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;

public class LockGUI extends GenericPopup {
	private GenericListWidget list;
	private GenericButton select;
	private GenericButton close;
	private GenericButton configure;

	public LockGUI() {
		GenericLabel label = new GenericLabel(ChatColor.YELLOW + "");
		label.setX(175).setY(25);
		label.setPriority(RenderPriority.Lowest);
		label.setWidth(-1).setHeight(-1);

		GenericTexture border = new GenericTexture("http://www.almuramc.com/downloads/window.png");
		border.setX(65).setY(20);
		border.setPriority(RenderPriority.High);
		border.setWidth(350).setHeight(200);

		GenericGradient gradient = new GenericGradient();
		gradient.setTopColor(new Color(0.25F, 0.25F, 0.25F, 1.0F));
		gradient.setBottomColor(new Color(0.35F, 0.35F, 0.35F, 1.0F));
		gradient.setWidth(300).setHeight(200);
		gradient.setX(65).setY(20);
		gradient.setPriority(RenderPriority.Highest);

		this.select = new GenericButton("Select");
		this.select.setX(95).setY(195);
		this.select.setWidth(45).setHeight(15);
		this.select.setPriority(RenderPriority.Lowest);

		this.close = new GenericButton("Close");
		this.close.setX(155).setY(195);
		this.close.setWidth(45).setHeight(15);
		this.close.setPriority(RenderPriority.Lowest);
		this.close.setTooltip(ChatColor.RED + "A Spout bug has broken this button! Press ESC to close.");
		this.close.setEnabled(false);

		this.configure = new GenericButton("Configure");
		this.configure.setX(215).setY(195);
		this.configure.setWidth(60).setHeight(20);
		this.configure.setPriority(RenderPriority.Lowest);
	}
}
