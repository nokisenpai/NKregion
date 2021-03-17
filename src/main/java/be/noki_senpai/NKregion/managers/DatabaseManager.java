package be.noki_senpai.NKregion.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import be.noki_senpai.NKregion.NKregion;
import be.noki_senpai.NKregion.utils.SQLConnect;

public class DatabaseManager
{
	private static Connection bdd = null;

	private ConsoleCommandSender console = null;
	private ConfigManager configManager = null;

	public DatabaseManager(ConfigManager configManager)
	{
		this.console = Bukkit.getConsoleSender();
		this.configManager = configManager;
	}

	public enum common
	{
		PLAYERS("NK_players"),
		SERVERS("NK_servers"),
		WORLDS("NK_worlds");

		private String name = "";

		common(String name)
		{
			this.name = name;
		}

		public String toString()
		{
			return name;
		}

		public static int size()
		{
			return common.values().length;
		}
	}

	public enum table
	{
		REGION(ConfigManager.PREFIX + "region");

		private String name = "";

		table(String name)
		{
			this.name = name;
		}

		public String toString()
		{
			return name;
		}

		public static int size()
		{
			return table.values().length;
		}
	}

	public enum worldguard
	{
		REGION("wg_region"),
		CUBOID("wg_region_cuboid"),
		USER("wg_user"),
		WORLD("wg_world");

		private String name = "";

		worldguard(String name)
		{
			this.name = name;
		}

		public String toString()
		{
			return name;
		}

		public static int size()
		{
			return worldguard.values().length;
		}
	}

	public boolean loadDatabase()
	{
		// Setting database informations
		SQLConnect.setInfo(configManager.getDbHost(), configManager.getDbPort(), configManager.getDbName(), configManager.getDbUser(), configManager.getDbPassword());

		// Try to connect to database
		try
		{
			bdd = SQLConnect.getHikariDS().getConnection();
		}
		catch(SQLException e)
		{
			bdd = null;
			console.sendMessage(
					ChatColor.DARK_RED + NKregion.PNAME + " Error while attempting database connexion. Verify your access informations in config.yml");
			e.printStackTrace();
			return false;
		}

		try
		{
			// Check if tables already exist on database
			if(!existTables())
			{
				// Create database structure if not exist
				createTable();
			}

		}
		catch(SQLException e)
		{
			console.sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while creating database structure. (Error#A.2.002)");
			return false;
		}

		return true;
	}

	public void unloadDatabase()
	{
		if(bdd != null)
		{
			try
			{
				bdd.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	private boolean existTables() throws SQLException
	{
		// Select all tables beginning with the prefix
		String req = "SHOW TABLES FROM " + configManager.getDbName() + " LIKE '" + ConfigManager.PREFIX + "%'";
		ResultSet resultat = null;
		PreparedStatement ps = null;

		try
		{
			ps = bdd.prepareStatement(req);
			resultat = ps.executeQuery();
			int count = 0;
			while(resultat.next())
			{
				count++;
			}

			// if all tables are missing
			if(count == 0)
			{
				console.sendMessage(ChatColor.GREEN + NKregion.PNAME + " Missing table(s). First start.");
				return false;
			}
			resultat.close();
			ps.close();

			// if 1 or more tables are missing
			if(count < table.size())
			{
				console.sendMessage(ChatColor.DARK_RED + NKregion.PNAME
						+ " Missing table(s). Please don't alter tables name or structure in database. (Error#main.Storage.002)");
				return false;
			}
		}
		catch(SQLException e1)
		{
			console.sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while testing existance of tables. (Error#main.Storage.003)");
		}
		finally
		{
			if(ps != null)
			{
				ps.close();
			}
			if(resultat != null)
			{
				resultat.close();
			}
		}

		return true;
	}

	private void createTable() throws SQLException
	{
		try
		{
			bdd = getConnection();

			String req = null;
			Statement s = null;

			console.sendMessage(ChatColor.GREEN + NKregion.PNAME + " Creating Database structure ...");

			try
			{
				// Creating request table
				req = "CREATE TABLE IF NOT EXISTS `" + table.REGION + "` (" + ""
						+ "`id` int(11) NOT NULL AUTO_INCREMENT,"
						+ "`player_id` int(11) NOT NULL,"
						+ "`server_id` INT NOT NULL,"
						+ "`world_id` INT NOT NULL,"
						+ "`name` varchar(100) NULL,"
						+ "`world_spawn_x` double NOT NULL,"
						+ "`world_spawn_y` double NOT NULL,"
						+ "`world_spawn_z` double NOT NULL,"
						+ "`nether_id` INT NULL,"
						+ "`nether_spawn_x` double NULL,"
						+ "`nether_spawn_y` double NULL,"
						+ "`nether_spawn_z` double NULL,"
						+ "`state` varchar(25) NULL,"
						+ "PRIMARY KEY (`id`)"
						+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
				s = bdd.createStatement();
				s.execute(req);
				s.close();

				console.sendMessage(ChatColor.GREEN + NKregion.PNAME + " Database structure created.");
			}
			catch(SQLException e)
			{
				console.sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while creating database structure. (Error#main.Storage.000)");
				e.printStackTrace();
			}
			finally
			{
				if(s != null)
				{
					s.close();
				}
			}
		}
		catch(SQLException e)
		{
			console.sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while creating database structure. (Error#main.Storage.001)");
		}
	}

	// Getter 'bdd'
	public static Connection getConnection()
	{
		try
		{
			if(!bdd.isValid(1))
			{
				if(!bdd.isClosed())
				{
					bdd.close();
				}
				bdd = SQLConnect.getHikariDS().getConnection();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return bdd;
	}
}
