package net.timstans.actionblocks.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;

public class BlockActionBrake extends BlockAction {
	private double brakespeed = 0.5;

	@Override
	public boolean onBuild(Player player, Block block) throws NoPermissionException {
		Permission.BUILD_EJECTOR.handle(player);
		player.sendMessage(ChatColor.GREEN + "You built the brake action block!");
		player.sendMessage(ChatColor.YELLOW + "It sets the speed to "+brakespeed+" ");
		return true;
	}
	@Override
	public void load(ConfigurationNode node) {
		super.load(node);
		this.brakespeed = node.get("brakespeed", this.brakespeed);
	}

	@Override
	public Material getDefaultMaterial() {
		return Material.MOSSY_COBBLESTONE;
	}
	@Override
	public void onGroupEnter(MinecartGroup group, Block block) {
		if (isPowered(block)) {
			//launch
			group.getProperties().setSpeedLimit(brakespeed);
		} else {
			
		}
	}

	@Override
	public String getName() {
		return "brake";
	}

}
