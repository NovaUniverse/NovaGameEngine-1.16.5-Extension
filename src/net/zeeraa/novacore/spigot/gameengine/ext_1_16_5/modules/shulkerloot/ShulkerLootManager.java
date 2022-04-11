package net.zeeraa.novacore.spigot.gameengine.ext_1_16_5.modules.shulkerloot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependantSound;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.loottable.LootTable;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class ShulkerLootManager extends NovaModule implements Listener {
	private static ShulkerLootManager instance;

	private ArrayList<Location> shulkers;

	private String lootTable;

	public static ShulkerLootManager getInstance() {
		return instance;
	}

	public ShulkerLootManager() {
		super("NovaGameEngineExt1_16_5.ShulkerLootManager");

		ShulkerLootManager.instance = this;
		this.shulkers = new ArrayList<Location>();
		this.lootTable = null;
	}

	public void refillShulkers() {
		this.refillShulkers(false);
	}

	public void refillShulkers(boolean announce) {
		shulkers.clear();
		if (announce) {

			Bukkit.getOnlinePlayers().forEach(player -> {
				player.sendMessage(LanguageManager.getString(player, "novagameengine.shulker.refill"));
				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.NOTE_PLING, 1F, 1F);
			});
		}
	}

	public String getLootTable() {
		return lootTable;
	}

	public void setLootTable(String lootTable) {
		this.lootTable = lootTable;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Log.trace(e.getAction() + " " + e.getClickedBlock().getType().name());
			Block block = e.getClickedBlock();
			if (block.getState() instanceof ShulkerBox) {
				if (lootTable != null) {
					if (!shulkers.contains(block.getLocation())) {
						Log.trace("Filling shulker at location " + block.getLocation().toString());

						LootTable lootTable = NovaCore.getInstance().getLootTableManager().getLootTable(this.lootTable);

						if (lootTable == null) {
							Log.warn("Missing loot table " + this.lootTable);
							return;
						}

						shulkers.add(block.getLocation());

						ShulkerBox chest = (ShulkerBox) block.getState();

						Inventory inventory = chest.getInventory();

						ShulkerFillEvent event = new ShulkerFillEvent(block, lootTable);

						Bukkit.getServer().getPluginManager().callEvent(event);

						if (event.isCancelled()) {
							return;
						}

						if (event.hasLootTableChanged()) {
							lootTable = event.getLootTable();
						}

						inventory.clear();

						List<ItemStack> loot = lootTable.generateLoot();

						inventory.clear();

						while (loot.size() > inventory.getSize()) {
							loot.remove(0);
						}

						while (loot.size() > 0) {
							Random random = new Random();

							int slot = random.nextInt(inventory.getSize());

							if (inventory.getItem(slot) == null) {
								ItemStack item = loot.remove(0);
								inventory.setItem(slot, item);
							}
						}
					}
				}
			}
		}
	}
}