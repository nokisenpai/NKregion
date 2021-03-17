package be.noki_senpai.NKregion.managers;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import be.noki_senpai.NKregion.NKregion;

public class Manager
{
	private ConsoleCommandSender console = null;
	private ConfigManager configManager = null;
	private DatabaseManager databaseManager = null;
	private PlayerManager playerManager = null;
	private QueueManager queueManager = null;
	private RegionManager regionManager = null;

	public Manager(NKregion instance)
	{
		console = Bukkit.getConsoleSender();
		configManager = new ConfigManager(instance.getConfig());
		databaseManager = new DatabaseManager(configManager);
		playerManager = new PlayerManager();
		queueManager = new QueueManager();
		regionManager = new RegionManager(playerManager);
	}

	// ######################################
	// Getters & Setters
	// ######################################

	// Console
	public ConsoleCommandSender getConsole()
	{
		return console;
	}

	// PluginManager
	public ConfigManager getConfigManager()
	{
		return configManager;
	}

	// DatabaseManager
	public DatabaseManager getDatabaseManager()
	{
		return databaseManager;
	}

	// PlayerManager
	public PlayerManager getPlayerManager()
	{
		return playerManager;
	}

	// QueueManager
	public QueueManager getQueueManager()
	{
		return queueManager;
	}

	// RegionManager
	public RegionManager getRegionManager()
	{
		return regionManager;
	}
}
