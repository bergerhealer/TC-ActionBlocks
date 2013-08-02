package net.timstans.actionblocks;

import net.timstans.actionblocks.blocks.BlockAction;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.config.FileConfiguration;

public class TCActionBlocks extends JavaPlugin {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new ActionBlocksListener(), this);
		FileConfiguration config = new FileConfiguration(this, "config.yml");
		config.load();
		config.setHeader("This is the configuration of the Blocks add-on of TrainCarts.");
		config.addHeader("In here you can configure all blocks available");
		config.setHeader("blocks", "\nConfiguration for individual action blocks can be set below");
		BlockAction.init(config.getNode("blocks"));
		config.save();
	}

	@Override
	public void onDisable() {
		BlockAction.deinit();
	}
}
