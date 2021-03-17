package be.noki_senpai.NKregion.data;

import be.noki_senpai.NKregion.NKregion;
import be.noki_senpai.NKregion.managers.ConfigManager;
import be.noki_senpai.NKregion.utils.CoordTask;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Region
{
	int id = -1;
	String owner = null;
	UUID uuid = null;
	String name = null;
	String server = null;
	Location worldSpawn = null;
	Location netherSpawn = null;
	Boolean locked = false;

	public Region(int id, String owner, UUID uuid, String name, String server, Location worldSpawn, Location netherSpawn, Boolean locked)
	{
		this.id = id;
		this.owner = owner;
		this.uuid = uuid;
		this.name = name;
		this.server = server;
		this.worldSpawn = worldSpawn;
		this.netherSpawn = netherSpawn;
		this.locked = locked;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}

	public Location getWorldSpawn()
	{
		return worldSpawn;
	}

	public void setWorldSpawn(Location worldSpawn)
	{
		this.worldSpawn = worldSpawn;
	}

	public Location getNetherSpawn()
	{
		return netherSpawn;
	}

	public void setNetherSpawn(Location netherSpawn)
	{
		this.netherSpawn = netherSpawn;
	}

	public Boolean getLocked()
	{
		return locked;
	}

	public void teleport(Player player)
	{
		if(!server.equals(ConfigManager.SERVERNAME))
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Forward"); // So BungeeCord knows to forward it
					out.writeUTF(server.toUpperCase());
					out.writeUTF("NKregion");

					ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
					DataOutputStream msgout = new DataOutputStream(msgbytes);
					try
					{
						msgout.writeUTF("tp|" + player.getName() + "|" + name.replaceAll("[^a-zA-Z0-9]", "")); // You can do
					}
					catch(IOException exception)
					{
						exception.printStackTrace();
					}

					out.writeShort(msgbytes.toByteArray().length);
					out.write(msgbytes.toByteArray());

					player.sendPluginMessage(NKregion.getPlugin(), "BungeeCord", out.toByteArray());

					ByteArrayDataOutput out2 = ByteStreams.newDataOutput();
					out2.writeUTF("Connect");
					out2.writeUTF(server.toUpperCase());

					player.sendPluginMessage(NKregion.getPlugin(), "BungeeCord", out2.toByteArray());
				}
			}.runTask(NKregion.getPlugin());
		}
		else
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					Location safeLocation = null;

					safeLocation = CoordTask.safeLocation(worldSpawn);

					player.teleport(safeLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
				}
			}.runTask(NKregion.getPlugin());
			if(locked)
			{
				player.sendMessage(
						ChatColor.RED + "/!\\ Cette région a été verrouillée. " + ChatColor.GOLD + owner + ChatColor.RED + " ne l'a pas renouvelé.");
			}
		}
	}

	public void teleportLand(Player player)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				Location safeLocation = null;

				safeLocation = CoordTask.safeLocation(worldSpawn);

				player.teleport(safeLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
			}
		}.runTask(NKregion.getPlugin());
		if(locked)
		{
			player.sendMessage(
					ChatColor.RED + "/!\\ Cette région a été verrouillée. " + ChatColor.GOLD + owner + ChatColor.RED + " ne l'a pas renouvelé.");
		}
	}

	public void teleportNether(Player player)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				Location safeLocation = null;

				safeLocation = CoordTask.safeNetherLocation(netherSpawn);

				player.teleport(safeLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
			}
		}.runTask(NKregion.getPlugin());
		if(locked)
		{
			player.sendMessage(
					ChatColor.RED + "/!\\ Cette région a été verrouillée. " + ChatColor.GOLD + owner + ChatColor.RED + " ne l'a pas renouvelé.");
		}
	}
}
