package net.timstans.actionblocks.blocks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.permissions.NoPermissionException;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.Permission;

public class BlockActionEjector extends BlockAction {

	@Override
	public Material getDefaultMaterial() {
		return Material.IRON_BLOCK;
	}

	@Override
	public String getName() {
		return "ejector";
	}

	@Override
	public void onMemberEnter(MinecartMember<?> member, Block block) {
		if (isPowered(block)) {
			member.eject();
		}
	}

	@Override
	public void onRedstoneChange(Block block, boolean powered) {
		if (powered) {
			MinecartMember<?> mm = MinecartMemberStore.getAt(block.getRelative(BlockFace.UP));
			if (mm != null) {
				mm.eject();
			}
		}
	}

	@Override
	public boolean onBuild(Player player, Block block) throws NoPermissionException {
		Permission.BUILD_EJECTOR.handle(player);
		player.sendMessage(ChatColor.GREEN + "You built the ejector action sign!");
		player.sendMessage(ChatColor.YELLOW + "It can eject players from minecarts.");
		return true;
	}
}
