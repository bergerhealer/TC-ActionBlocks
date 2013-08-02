package net.timstans.actionblocks;

import net.timstans.actionblocks.blocks.BlockAction;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.bergerkiller.bukkit.common.collections.BlockMap;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.MemberBlockChangeEvent;

public class ActionBlocksListener implements Listener {
	private final BlockMap<Boolean> blockPoweredStates = new BlockMap<Boolean>();

	@EventHandler(priority = EventPriority.NORMAL)
	public void onMemberBlockChange(MemberBlockChangeEvent event) {
		MinecartMember<?> member = event.getMember();
		if (member.isDerailed()) {
			return; //just in case?
		}
		Block from = event.getFrom().getRelative(BlockFace.DOWN);
		Block to = event.getTo().getRelative(BlockFace.DOWN);
		if (!BlockUtil.equals(from, to)) {
			BlockAction old = BlockAction.get(from);
			if (old != null) {
				old.onBlockLeave(member, from);
			}
		}
		BlockAction neww = BlockAction.get(to);
		if (neww != null) {
			neww.onBlockEnter(member, to);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		final Block block = event.getBlock();
		final BlockAction ba = BlockAction.get(block);
		if (ba != null) {
			final boolean powered = block.isBlockIndirectlyPowered();
			final Boolean wasPowered = blockPoweredStates.put(block, powered);
			if (wasPowered != null && wasPowered.booleanValue() == powered) {
				// No change in power - no event
				return;
			}
			// Change in power or added to map for the first time - fire event
			CommonUtil.nextTick(new Runnable() {
				public void run() {
					if (ba.isPowerInverted()) {
						ba.onRedstoneChange(block, !powered);
					} else {
						ba.onRedstoneChange(block, powered);
					}
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.isCancelled()) {
			Block block = event.getBlock();
			if (Util.ISTCRAIL.get(block)) {
				block = block.getRelative(BlockFace.DOWN);
				BlockAction ba = BlockAction.get(block);
				if (ba != null) {
					try {
						if (ba.onBuild(event.getPlayer(), block)) {
							return;
						}
					} catch (NoPermissionException ex) {
						event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to make this action block!");
					}
					event.setCancelled(true);
				}
			}
		}
	}

}
