package be.noki_senpai.NKregion;

import be.noki_senpai.NKregion.cmd.LandCmd;
import be.noki_senpai.NKregion.cmd.NetherCmd;
import be.noki_senpai.NKregion.listeners.PlayerConnectionListener;
import be.noki_senpai.NKregion.listeners.RegionListener;
import be.noki_senpai.NKregion.managers.Manager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class NKregion extends JavaPlugin implements PluginMessageListener
{
	public final static String PNAME = "[NKregion]";
	private Manager manager = null;
	private ConsoleCommandSender console = null;
	private static NKregion plugin = null;

	// Fired when plugin is first enabled
	@Override public void onEnable()
	{
		plugin = this;
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "WARN");
		this.saveDefaultConfig();

		console = Bukkit.getConsoleSender();
		manager = new Manager(this);

		if(!checkNKmanager())
		{
			console.sendMessage(ChatColor.DARK_RED + PNAME + " NKmanager in not enabled !");
			disablePlugin();
			return;
		}

		// Load configuration
		if(!manager.getConfigManager().loadConfig())
		{
			disablePlugin();
			return;
		}

		// Load database connection (with check)
		if(!manager.getDatabaseManager().loadDatabase())
		{
			disablePlugin();
			return;
		}

		// Load regions
		if(!manager.getRegionManager().loadData())
		{
			disablePlugin();
			return;
		}

		// Register commands
		getCommand("land").setExecutor(new LandCmd(manager.getPlayerManager(), manager.getQueueManager(), manager.getRegionManager()));
		getCommand("nether").setExecutor(new NetherCmd(manager.getPlayerManager(), manager.getQueueManager(), manager.getRegionManager()));

		// Event
		getServer().getPluginManager().registerEvents(new RegionListener(manager.getRegionManager()), this);
		getServer().getPluginManager().registerEvents(new PlayerConnectionListener(manager.getRegionManager()), this);

		// Data exchange between servers
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

		console.sendMessage(ChatColor.WHITE + "     .--. ");
		console.sendMessage(ChatColor.WHITE + "     |   '.   " + ChatColor.GREEN + PNAME + " by NoKi_senpai - successfully enabled !");
		console.sendMessage(ChatColor.WHITE + "'-..____.-'");
	}

	// Fired when plugin is disabled
	@Override public void onDisable()
	{
		manager.getDatabaseManager().unloadDatabase();
		manager.getRegionManager().unloadData();
		console.sendMessage(ChatColor.GREEN + PNAME + " has been disable.");
	}

	// ######################################
	// Getters & Setters
	// ######################################

	// Getter 'plugin'
	public static NKregion getPlugin()
	{
		return plugin;
	}

	// ######################################
	// Data exchange between servers
	// ######################################

	@Override public void onPluginMessageReceived(String channel, Player player, byte[] message)
	{

		if(!channel.equals("BungeeCord"))
		{
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();

		if(subchannel.equals("NKregion"))
		{
			String tmp = in.readUTF();
			tmp = tmp.substring(2, tmp.length());
			console.sendMessage(tmp);
			String[] args = tmp.split("\\|");

			if(args.length >= 3)
			{

				switch(args[0])
				{
					case "tp":
						manager.getRegionManager().checkTpRegion(args[1], args[2]);
						break;
					default:
				}

			}
		}
	}

	// ######################################
	// Disable this plugin
	// ######################################

	public void disablePlugin()
	{
		getServer().getPluginManager().disablePlugin(this);
	}

	// ######################################
	// Check if NKmanager is enabled
	// ######################################

	public boolean checkNKmanager()
	{
		return getServer().getPluginManager().getPlugin("NKmanager").isEnabled();
	}
}
