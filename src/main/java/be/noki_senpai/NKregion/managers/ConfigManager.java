package be.noki_senpai.NKregion.managers;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import be.noki_senpai.NKregion.NKregion;

public class ConfigManager
{
	private ConsoleCommandSender console = null;
	private FileConfiguration config = null;

	private String dbHost = null;
	private int dbPort = 3306;
	private String dbName = null;
	private String dbUser = null;
	private String dbPassword = null;

	public static String PREFIX = null;
	public static String SERVERNAME = null;

	// Constructor
	public ConfigManager(FileConfiguration config)
	{
		this.console = Bukkit.getConsoleSender();
		this.config = config;
	}

	public boolean loadConfig()
	{
		// Check if "use-mysql" is to true. Plugin only use MySQL database.
		if(!config.getBoolean("use-mysql"))
		{
			console.sendMessage(ChatColor.DARK_RED + NKregion.PNAME
					+ " Disabled because this plugin only use MySQL database. Please set to true the 'use-mysql' field in config.yml");
			return false;
		}

		// Get database access informations
		dbHost = config.getString("host");
		dbPort = config.getInt("port");
		dbName = config.getString("dbName");
		dbUser = config.getString("user");
		dbPassword = config.getString("password");

		// Get prefix used for table name on database
		PREFIX = config.getString("table-prefix", "NKregion_");

		// Get server name gave to bungeecord config
		SERVERNAME = config.getString("server-name", "world");

		return true;
	}

	// ######################################
	// Getters (only)
	// ######################################

	public String getDbHost()
	{
		return dbHost;
	}

	public int getDbPort()
	{
		return dbPort;
	}

	public String getDbName()
	{
		return dbName;
	}

	public String getDbUser()
	{
		return dbUser;
	}

	public String getDbPassword()
	{
		return dbPassword;
	}
}
