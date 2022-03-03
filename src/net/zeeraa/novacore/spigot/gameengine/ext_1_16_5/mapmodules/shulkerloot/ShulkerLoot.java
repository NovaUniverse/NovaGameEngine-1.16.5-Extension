package net.zeeraa.novacore.spigot.gameengine.ext_1_16_5.mapmodules.shulkerloot;

import org.json.JSONObject;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.RandomGenerator;
import net.zeeraa.novacore.spigot.gameengine.ext_1_16_5.modules.shulkerloot.ShulkerLootManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.game.Game;
import net.zeeraa.novacore.spigot.module.modules.game.map.GameMap;
import net.zeeraa.novacore.spigot.module.modules.game.map.mapmodule.MapModule;
import net.zeeraa.novacore.spigot.module.modules.game.triggers.DelayedGameTrigger;
import net.zeeraa.novacore.spigot.module.modules.game.triggers.GameTrigger;
import net.zeeraa.novacore.spigot.module.modules.game.triggers.TriggerCallback;
import net.zeeraa.novacore.spigot.module.modules.game.triggers.TriggerFlag;

public class ShulkerLoot extends MapModule {
	private String lootTable;

	private int minRefillTime;
	private int maxRefillTime;

	private boolean announceRefills;

	private DelayedGameTrigger trigger;

	public ShulkerLoot(JSONObject json) {
		super(json);

		this.lootTable = null;
		this.minRefillTime = -1;
		this.maxRefillTime = -1;
		this.announceRefills = true;
		this.trigger = null;

		if (json.has("loot_table")) {
			this.lootTable = json.getString("loot_table");
		}

		if (json.has("min_refill_time")) {
			this.minRefillTime = json.getInt("min_refill_time");
		}

		if (json.has("max_refill_time")) {
			this.maxRefillTime = json.getInt("max_refill_time");
		}

		if (json.has("announce_refills")) {
			this.announceRefills = json.getBoolean("announce_refills");
		}

		if (minRefillTime != -1 && maxRefillTime == -1) {
			maxRefillTime = minRefillTime;
		} else if (maxRefillTime != -1 && minRefillTime == -1) {
			minRefillTime = maxRefillTime;
		}

		this.trigger = new DelayedGameTrigger("novagameengine.shulker.refill", minRefillTime * 20, new TriggerCallback() {
			@Override
			public void run(GameTrigger trigger, TriggerFlag reason) {
				ShulkerLootManager.getInstance().refillShulkers(announceRefills);
				startTask();
			}
		});
		trigger.addFlag(TriggerFlag.STOP_ON_GAME_END);
	}

	public String getLootTable() {
		return lootTable;
	}

	public int getMinRefillTime() {
		return minRefillTime;
	}

	public int getMaxRefillTime() {
		return maxRefillTime;
	}

	public boolean isRefillsEnabled() {
		return minRefillTime > 0;
	}

	public boolean isAnnounceRefills() {
		return announceRefills;
	}

	@Override
	public void onMapLoad(GameMap map) {
		if (ModuleManager.isDisabled(ShulkerLootManager.class)) {
			Log.info("Loading ChestLootManager because the game map has a chest or ender chest loot table");
			ModuleManager.enable(ShulkerLootManager.class);
		}

		ShulkerLootManager.getInstance().setLootTable(this.getLootTable());
	}

	@Override
	public void onGameStart(Game game) {
		if (isRefillsEnabled()) {
			game.addTrigger(trigger);
			startTask();
		}
	}

	public DelayedGameTrigger getTrigger() {
		return trigger;
	}

	private void startTask() {
		int delay = RandomGenerator.generate(minRefillTime, maxRefillTime);

		Log.debug("Next shulker refill in " + delay + " seconds");

		trigger.stop();
		trigger.setDelay(delay * 20);
		trigger.start();
	}
}