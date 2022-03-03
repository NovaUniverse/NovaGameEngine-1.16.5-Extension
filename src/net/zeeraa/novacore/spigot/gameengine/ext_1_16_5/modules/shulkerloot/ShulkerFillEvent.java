package net.zeeraa.novacore.spigot.gameengine.ext_1_16_5.modules.shulkerloot;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.zeeraa.novacore.spigot.loottable.LootTable;

public class ShulkerFillEvent extends Event implements Cancellable {
	private static final HandlerList HANDLERS = new HandlerList();

	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	private Block block;
	private LootTable lootTable;

	private boolean cancelled;
	private boolean lootTableChanged;

	public ShulkerFillEvent(Block block, LootTable lootTable) {
		this.block = block;
		this.lootTable = lootTable;

		this.cancelled = false;
		this.lootTableChanged = false;
	}

	public Block getBlock() {
		return block;
	}

	public Location getLocation() {
		return block.getLocation();
	}

	public LootTable getLootTable() {
		return lootTable;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	public boolean hasLootTableChanged() {
		return lootTableChanged;
	}

	public void setLootTable(LootTable lootTable) {
		this.lootTable = lootTable;
		lootTableChanged = true;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}