package net.zeeraa.novacore.spigot.gameengine.ext_1_16_5;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.zeeraa.novacore.spigot.gameengine.ext_1_16_5.mapmodules.shulkerloot.ShulkerLoot;
import net.zeeraa.novacore.spigot.gameengine.ext_1_16_5.modules.shulkerloot.ShulkerLootManager;
import net.zeeraa.novacore.spigot.language.LanguageReader;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.game.map.mapmodule.MapModuleManager;

public class GameEngineExtension extends JavaPlugin implements Listener {
	private static GameEngineExtension instance;

	public static GameEngineExtension getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		GameEngineExtension.instance = this;

		try {
			LanguageReader.readFromJar(this.getClass(), "/lang/en-us.json");
		} catch (Exception e) {
			e.printStackTrace();
		}

		ModuleManager.loadModule(ShulkerLootManager.class);

		MapModuleManager.addMapModule("novagameengine.1_16_5.shulkerloot", ShulkerLoot.class);

		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll((Plugin) this);
		Bukkit.getScheduler().cancelTasks(this);
	}
}