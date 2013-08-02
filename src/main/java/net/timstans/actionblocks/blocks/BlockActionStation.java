package net.timstans.actionblocks.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.collections.BlockMap;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;

public class BlockActionStation extends BlockAction {
	private double launchDistance = 10;
	private BlockMap<BlockFace> launchDirections = new BlockMap<BlockFace>();

	@Override
	public Material getDefaultMaterial() {
		return Material.OBSIDIAN;
	}

	@Override
	public String getName() {
		return "station";
	}

	@Override
	public void load(ConfigurationNode node) {
		super.load(node);
		this.launchDistance = node.get("launchDistance", this.launchDistance);
	}

	@Override
	public void onGroupEnter(MinecartGroup group, Block block) {
		launchDirections.put(block, group.head().getDirectionTo());
		group.getActions().clear();
		if (isPowered(block)) {
			//launch
			group.head().getActions().addActionLaunch(this.launchDistance, TrainCarts.launchForce);
		} else {
			//stop
			group.middle().getActions().addActionLaunch(block.getLocation().add(0.5, 1.5, 0.5), 0.0);
			if (TrainCarts.playSoundAtStation) {
				group.getActions().addActionSizzle();
			}
			group.getActions().addActionWaitForever();
		}
	}

	@Override
	public void onRedstoneChange(Block block, boolean powered) {
		if (powered) {
			MinecartMember<?> member = MinecartMemberStore.getAt(block.getRelative(BlockFace.UP));
			if (member != null) {
				member.getGroup().getActions().clear();
				BlockFace face = this.launchDirections.get(block);
				if (face == null) {
					member.getActions().addActionLaunch(this.launchDistance, TrainCarts.launchForce);
				} else {
					member.getActions().addActionLaunch(face, this.launchDistance, TrainCarts.launchForce);
				}
			}
		}
	}

	@Override
	public boolean onBuild(Player player, Block block) throws NoPermissionException {
		Permission.BUILD_STATION.handle(player);
		player.sendMessage(ChatColor.GREEN + "You built the station action sign!");
		player.sendMessage(ChatColor.YELLOW + "It can stop and launch trains.");
		return true;
	}
}
