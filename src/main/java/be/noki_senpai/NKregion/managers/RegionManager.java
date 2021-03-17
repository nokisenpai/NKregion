package be.noki_senpai.NKregion.managers;

import be.noki_senpai.NKregion.NKregion;
import be.noki_senpai.NKregion.data.NKPlayer;
import be.noki_senpai.NKregion.data.Region;
import be.noki_senpai.NKregion.data.RegionInterface;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RegionManager
{
	public static int SERVERID = -1;
	private Map<String, Integer> worlds = new HashMap<>();
	private Map<String, String> tpRegion = new HashMap<>();
	private Map<String, Region> regions = new LinkedHashMap<String, Region>();
	private Map<Integer, RegionInterface> regionInterface = new HashMap<>();

	PlayerManager playerManager = null;
	private ConsoleCommandSender console = null;

	public RegionManager(PlayerManager playerManager)
	{
		this.playerManager = playerManager;
		console = Bukkit.getConsoleSender();

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				reloadRegions();
			}
		}.runTaskTimerAsynchronously(NKregion.getPlugin(), 0, 300 * 20);
	}

	// Load data from database
	public boolean loadData()
	{
		if(!makeServerId())
		{
			return false;
		}

		if(!makeWorldsId())
		{
			return false;
		}

		// load regions
		if(!loadRegions())
		{
			return false;
		}

		return true;
	}

	public void unloadData()
	{
		unloadRegions();
	}

	// **************************************
	// **************************************
	// Load all data
	// **************************************
	// **************************************

	// ######################################
	// Get server id
	// ######################################

	public boolean makeServerId()
	{
		Connection bdd = null;
		ResultSet resultat = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "SELECT id FROM " + DatabaseManager.common.SERVERS + " WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setString(1, ConfigManager.SERVERNAME);

			resultat = ps.executeQuery();

			if(resultat.next())
			{
				SERVERID = resultat.getInt(1);
			}
			else
			{
				ps.close();
				resultat.close();
				return false;
			}

			ps.close();
			resultat.close();
		}
		catch(SQLException e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while getting server id.");
			e.printStackTrace();
		}
		return true;
	}

	// ######################################
	// Get worlds id
	// ######################################

	public boolean makeWorldsId()
	{
		Connection bdd = null;
		ResultSet resultat = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			for(World world : Bukkit.getWorlds())
			{
				Bukkit.getConsoleSender().sendMessage(world.getName());
				req = "SELECT id FROM " + DatabaseManager.common.WORLDS + " WHERE server_id = ? AND name = ?";
				ps = bdd.prepareStatement(req);
				ps.setInt(1, SERVERID);
				ps.setString(2, world.getName());

				resultat = ps.executeQuery();

				if(resultat.next())
				{
					worlds.put(world.getName(), resultat.getInt(1));
				}
				else
				{
					ps.close();
					resultat.close();
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while getting worlds id.");
					return false;
				}

				ps.close();
				resultat.close();
			}
		}
		catch(SQLException e1)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while getting worlds id.");
			e1.printStackTrace();
		}
		return true;
	}

	public boolean loadRegions()
	{
		Connection bdd = null;
		ResultSet resultat = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "SELECT rg.*, p.name AS player_name, p.uuid AS uuid , srv.name AS server_name, w.name AS world_name, wn.name AS nether_name FROM "
					+ DatabaseManager.table.REGION + " rg " + "LEFT JOIN " + DatabaseManager.common.PLAYERS + " p ON rg.player_id = p.id "
					+ "LEFT JOIN " + DatabaseManager.common.SERVERS + " srv ON rg.server_id = srv.id " + "LEFT JOIN " + DatabaseManager.common.WORLDS
					+ " w ON rg.world_id = w.id " + "LEFT JOIN " + DatabaseManager.common.WORLDS + " wn ON rg.nether_id = wn.id " + "";
			ps = bdd.prepareStatement(req);
			resultat = ps.executeQuery();

			while(resultat.next())
			{
				if(resultat.getString("state").equals("enabled") || resultat.getString("state").equals("lock"))
				{
					Location worldSpawn = null;
					Location netherSpawn = null;
					if(resultat.getString("server_name").equals(ConfigManager.SERVERNAME))
					{
						worldSpawn = new Location(Bukkit.getWorld(resultat.getString("world_name")), resultat.getDouble("world_spawn_x"), resultat.getDouble("world_spawn_y"), resultat.getDouble("world_spawn_z"));
						netherSpawn = new Location(Bukkit.getWorld(resultat.getString("nether_name")), resultat.getDouble("nether_spawn_x"), resultat.getDouble("nether_spawn_y"), resultat.getDouble("nether_spawn_z"));
					}

					boolean locked = false;
					if(resultat.getString("state").equals("lock"))
					{
						locked = true;
					}

					regions.put(resultat.getString("world_name"), new Region(resultat.getInt("id"), resultat.getString("player_name"), UUID.fromString(resultat.getString("uuid")), resultat.getString("name"), resultat.getString("server_name"), worldSpawn, netherSpawn, locked));
				}
			}

			ps.close();
			resultat.close();
		}
		catch(SQLException e1)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while getting worlds id.");
			e1.printStackTrace();
		}
		loadRegionInterface(regions);
		return true;
	}

	public void reloadRegions()
	{
		Map<String, Region> tmpRegions = new LinkedHashMap<String, Region>(regions);
		unloadRegions();
		Connection bdd = null;
		ResultSet resultat = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			req = "SELECT rg.*, p.name AS player_name, p.uuid AS uuid, srv.name AS server_name, w.name AS world_name, wn.name AS nether_name FROM "
					+ DatabaseManager.table.REGION + " rg " + "LEFT JOIN " + DatabaseManager.common.PLAYERS + " p ON rg.player_id = p.id "
					+ "LEFT JOIN " + DatabaseManager.common.SERVERS + " srv ON rg.server_id = srv.id " + "LEFT JOIN " + DatabaseManager.common.WORLDS
					+ " w ON rg.world_id = w.id " + "LEFT JOIN " + DatabaseManager.common.WORLDS + " wn ON rg.nether_id = wn.id " + "";
			ps = bdd.prepareStatement(req);
			resultat = ps.executeQuery();

			while(resultat.next())
			{
				if(resultat.getString("state").equals("enabled") || resultat.getString("state").equals("lock"))
				{
					if(!tmpRegions.containsKey(resultat.getString("world_name")))
					{
						Bukkit.broadcastMessage(ChatColor.BLUE + "La nouvelle région " + ChatColor.AQUA + resultat.getString("name") + ChatColor.BLUE
								+ " est maintenant accessible.");
					}

					Location worldSpawn = null;
					Location netherSpawn = null;
					if(resultat.getString("server_name").equals(ConfigManager.SERVERNAME))
					{
						worldSpawn = new Location(Bukkit.getWorld(resultat.getString("world_name")), resultat.getDouble("world_spawn_x"), resultat.getDouble("world_spawn_y"), resultat.getDouble("world_spawn_z"));
						netherSpawn = new Location(Bukkit.getWorld(resultat.getString("nether_name")), resultat.getDouble("nether_spawn_x"), resultat.getDouble("nether_spawn_y"), resultat.getDouble("nether_spawn_z"));
					}

					boolean locked = false;
					if(resultat.getString("state").equals("lock"))
					{
						locked = true;
					}

					regions.put(resultat.getString("world_name"), new Region(resultat.getInt("id"), resultat.getString("player_name"), UUID.fromString(resultat.getString("uuid")), resultat.getString("name"), resultat.getString("server_name"), worldSpawn, netherSpawn, locked));
				}
				else
				{
					if(regions.containsKey(resultat.getString("world_name")))
					{
						regions.remove(resultat.getString("world_name"));
						Bukkit.broadcastMessage(ChatColor.BLUE + "La région " + ChatColor.AQUA + resultat.getString("name") + ChatColor.BLUE
								+ " n'est plus accessible.");
					}
				}
			}

			ps.close();
			resultat.close();
		}
		catch(SQLException e1)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while getting worlds id.");
			e1.printStackTrace();
		}
		loadRegionInterface(regions);
	}

	public void unloadRegions()
	{
		regions.clear();
	}

	// ######################################
	// addRegion
	// ######################################

	public void addRegion(NKPlayer player, String worldName, String seed, double worldX, double worldZ, String worldDifficulty, String netherDifficulty, String regionName)
	{
		Bukkit.broadcastMessage(ChatColor.RED + "/!\\ " + ChatColor.BLUE
				+ "La création d'une région vient de commencer. Vous risquez de subir du lag pendant quelques instants." + ChatColor.RED + " /!\\");

		String netherName = worldName + "_n";

		// *******************
		// *** MAP WORLD ****
		// *******************
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(seed.equals("0"))
				{
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv create " + worldName + " NORMAL");
				}
				else if(seed.equals("flat"))
				{
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv create " + worldName + " NORMAL -t FLAT");
				}
				else
				{
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv create " + worldName + " NORMAL -s " + seed);
				}
			}
		}.runTask(NKregion.getPlugin());

		int wgWorldId = -1;
		while(true)
		{
			wgWorldId = checkWorldWG(worldName);
			if(wgWorldId == -1)
			{
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				break;
			}
		}
		insertRegionWG(worldName, wgWorldId);

		try
		{
			Thread.sleep(2000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

		insertCuboidWG(worldName, wgWorldId, worldX, worldZ, 500);

		int worldY = Objects.requireNonNull(Bukkit.getWorld(worldName)).getHighestBlockYAt((int) worldX, (int) worldZ);

		// *******************
		// *** MAP NETHER ****
		// *******************
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv create " + netherName + " NETHER");
			}
		}.runTask(NKregion.getPlugin());

		int wgNetherId = -1;
		while(true)
		{
			wgNetherId = checkWorldWG(netherName);
			if(wgNetherId == -1)
			{
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				break;
			}
		}
		insertRegionWG(netherName, wgNetherId);

		try
		{
			Thread.sleep(1000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

		Location fortress = Bukkit.getWorld(netherName).locateNearestStructure(new Location(Bukkit.getWorld(netherName), 0, 0, 0), StructureType.NETHER_FORTRESS, 20, false);
		if(fortress == null)
		{
			fortress = Bukkit.getWorld(netherName).locateNearestStructure(new Location(Bukkit.getWorld(netherName), 0, 0, 0), StructureType.NETHER_FORTRESS, 50, false);
		}
		if(fortress == null)
		{
			fortress = Bukkit.getWorld(netherName).locateNearestStructure(new Location(Bukkit.getWorld(netherName), 0, 0, 0), StructureType.NETHER_FORTRESS, 100, false);
		}

		insertCuboidWG(netherName, wgNetherId, fortress.getX(), fortress.getZ(), 150);

		try
		{
			Thread.sleep(2000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

		final String playerName = player.getPlayerName();
		final List<String> commands = new ArrayList<>();
		commands.add("rg load -w " + worldName);
		commands.add("rg load -w " + netherName);
		commands.add("mvm set difficulty " + worldDifficulty + " " + worldName);
		commands.add("mvm set portalform none " + worldName);
		commands.add("mvm set difficulty " + netherDifficulty + " " + netherName);
		commands.add("mvm set portalform none " + netherName);
		commands.add("mvrule announceAdvancements false " + worldName);
		commands.add("mvrule announceAdvancements false " + netherName);
		commands.add("mvinv addworld " + worldName + " normal");
		commands.add("mvinv addworld " + netherName + " normal");
		commands.add("mvinv reload");
		commands.add("rg flag " + worldName + " -w " + worldName + " pvp deny");
		commands.add("rg flag " + worldName + " -w " + worldName + " -g all exit deny");
		commands.add("rg flag " + worldName + " -w " + worldName + " creeper-explosion deny");
		commands.add("rg flag " + worldName + " -w " + worldName + " enderman-grief deny");
		commands.add("rg flag " + worldName + " -w " + worldName + " chorus-fruit-teleport deny");
		commands.add("rg flag " + worldName + " -w " + worldName + " enderpearl deny");
		commands.add("rg flag " + worldName + " -w " + worldName + " deny-spawn minecraft:phantom");
		commands.add("rg flag " + worldName + " -w " + worldName + " mob-spawning allow");
		commands.add("rg flag " + netherName + " -w " + netherName + " pvp deny");
		commands.add("rg flag " + netherName + " -w " + netherName + " -g all exit deny");
		commands.add("rg flag " + netherName + " -w " + netherName + " creeper-explosion deny");
		commands.add("rg flag " + netherName + " -w " + netherName + " enderman-grief deny");
		commands.add("rg flag " + netherName + " -w " + netherName + " mob-spawning allow");
		commands.add("rg flag __global__ -w " + worldName + " mob-spawning deny");
		commands.add("rg flag __global__ -w " + netherName + " mob-spawning deny");
		commands.add("rg addowner " + worldName + " -w " + worldName + " " + player.getPlayerUUID());
		commands.add("rg addowner " + netherName + " -w " + netherName + " " + player.getPlayerUUID());
		commands.add("lp user " + player.getPlayerUUID() + " permission set worldguard.region.addmember.own.*");
		commands.add("lp user " + player.getPlayerUUID() + " permission set worldguard.region.removemember..own.*");
		commands.add("lp user " + player.getPlayerUUID() + " permission set worldguard.region.info.own.*");
		commands.add("nk reload");

		for(String cmd : commands)
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
				}
			}.runTask(NKregion.getPlugin());
			try
			{
				Thread.sleep(500);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		try
		{
			Thread.sleep(3000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

		makeWorldsId();

		World world = Bukkit.getWorld(worldName);
		World nether = fortress.getWorld();

		Location worldSpawn = new Location(world, worldX, worldY, worldZ);

		world.getWorldBorder().setCenter(worldSpawn);
		world.getWorldBorder().setSize(1001);

		nether.getWorldBorder().setCenter(fortress);
		nether.getWorldBorder().setSize(301);

		fortress.setY(50);

		offlineAddRegion(player, worldSpawn, regionName, worldX, worldZ, fortress);

		Bukkit.broadcastMessage(ChatColor.RED + "/!\\ " + ChatColor.BLUE + "Une nouvelle région vient d'être créée." + ChatColor.RED + " /!\\");

		reloadRegions();

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "nk reload *");
			}
		}.runTask(NKregion.getPlugin());

	}

	private void offlineAddRegion(NKPlayer player, Location world, String regionName, Double x, Double z, Location nether)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;
		try
		{
			bdd = DatabaseManager.getConnection();
			req = "INSERT INTO " + DatabaseManager.table.REGION + " ( player_id , " + "server_id , " + "world_id , " + "name , " + "world_spawn_x , "
					+ "world_spawn_y , " + "world_spawn_z , " + "nether_id , " + "nether_spawn_x , " + "nether_spawn_y , " + "nether_spawn_z , "
					+ "state " + ") VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? ) ON DUPLICATE KEY UPDATE " + "state = 'enabled'";
			ps = bdd.prepareStatement(req);
			ps.setInt(1, player.getId());
			ps.setInt(2, SERVERID);
			ps.setInt(3, worlds.get(world.getWorld().getName()));
			ps.setString(4, regionName);
			ps.setDouble(5, world.getX());
			ps.setDouble(6, world.getY());
			ps.setDouble(7, world.getZ());
			ps.setInt(8, worlds.get(nether.getWorld().getName()));
			ps.setDouble(9, nether.getX());
			ps.setDouble(10, nether.getY());
			ps.setDouble(11, nether.getZ());
			ps.setString(12, "enabled");
			ps.executeUpdate();
			ps.close();

		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void removeRegion(String regionName)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;
		try
		{
			bdd = DatabaseManager.getConnection();
			req = "UPDATE " + DatabaseManager.table.REGION + " SET state = ? WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setString(1, "removed");
			ps.setString(2, regionName);

			ps.executeUpdate();
			ps.close();

			reloadRegions();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void restoreRegion(String regionName)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;
		try
		{
			bdd = DatabaseManager.getConnection();
			req = "UPDATE " + DatabaseManager.table.REGION + " SET state = ? WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setString(1, "enabled");
			ps.setString(2, regionName);

			ps.executeUpdate();
			ps.close();

			reloadRegions();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void setWorldSpawnRegion(String regionName, Double x, Double y, Double z)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;
		try
		{
			bdd = DatabaseManager.getConnection();
			req = "UPDATE " + DatabaseManager.table.REGION + " SET world_spawn_x = ? , world_spawn_y = ? , world_spawn_z = ? WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setDouble(1, x);
			ps.setDouble(2, y);
			ps.setDouble(3, z);
			ps.setString(4, regionName);

			ps.executeUpdate();
			ps.close();

			reloadRegions();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void setNetherSpawnRegion(String regionName, Double x, Double y, Double z)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;
		try
		{
			bdd = DatabaseManager.getConnection();
			req = "UPDATE " + DatabaseManager.table.REGION + " SET nether_spawn_x = ? , nether_spawn_y = ? , nether_spawn_z = ? WHERE name = ?";
			ps = bdd.prepareStatement(req);
			ps.setDouble(1, x);
			ps.setDouble(2, y);
			ps.setDouble(3, z);
			ps.setString(4, regionName);

			ps.executeUpdate();
			ps.close();

			reloadRegions();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public int checkWorldWG(String worldName)
	{
		int id = -1;
		Connection bdd = null;
		ResultSet resultat = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			for(World world : Bukkit.getWorlds())
			{
				req = "SELECT id FROM " + DatabaseManager.worldguard.WORLD + " WHERE name = ?";
				ps = bdd.prepareStatement(req);
				ps.setString(1, worldName);

				resultat = ps.executeQuery();

				if(resultat.next())
				{
					id = resultat.getInt(1);
				}

				ps.close();
				resultat.close();
			}
		}
		catch(SQLException e1)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while checking world in Worldguard database..");
			e1.printStackTrace();
		}
		return id;
	}

	private void insertRegionWG(String worldName, int worldId)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;
		try
		{
			bdd = DatabaseManager.getConnection();
			req = "INSERT INTO " + DatabaseManager.worldguard.REGION
					+ " ( id , world_id , type , priority , parent ) VALUES ( ? , ? , ? , ? , NULL )";
			ps = bdd.prepareStatement(req);
			ps.setString(1, worldName);
			ps.setInt(2, worldId);
			ps.setString(3, "cuboid");
			ps.setInt(4, 0);
			ps.executeUpdate();

			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void insertCuboidWG(String worldName, int worldId, double x, double z, int size)
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;
		try
		{
			bdd = DatabaseManager.getConnection();
			req = "INSERT INTO " + DatabaseManager.worldguard.CUBOID
					+ " ( region_id , world_id , min_x , min_y , min_z , max_x , max_y , max_z ) VALUES ( ? , ? , ? , ? , ? , ? , ? , ? )";
			ps = bdd.prepareStatement(req);
			ps.setString(1, worldName);
			ps.setInt(2, worldId);
			ps.setString(3, String.valueOf(x - size));
			ps.setString(4, String.valueOf(0));
			ps.setString(5, String.valueOf(z - size));
			ps.setString(6, String.valueOf(x + size));
			ps.setString(7, String.valueOf(255));
			ps.setString(8, String.valueOf(z + size));
			ps.executeUpdate();

			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void loadRegionInterface(Map<String, Region> reg)
	{
		regionInterface.clear();
		for(int i = 0; i <= (int) regions.size() / 36; i++)
		{
			regionInterface.put(i, new RegionInterface(reg, i));
		}
	}

	public Map<Integer, RegionInterface> getRegionInterface()
	{
		return regionInterface;
	}

	public Map<String, Region> getRegions()
	{
		return regions;
	}

	public Map<String, String> getTpRegion()
	{
		return tpRegion;
	}

	public void checkTpRegion(String playerName, String WorldName)
	{
		Player player = Bukkit.getPlayer(playerName);
		if(player != null)
		{
			regions.get(WorldName).teleport(player);
		}
		else
		{
			getTpRegion().put(playerName, WorldName);
		}
	}
}
