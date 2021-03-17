package be.noki_senpai.NKregion.cmd.Region;

import be.noki_senpai.NKregion.NKregion;
import be.noki_senpai.NKregion.managers.PlayerManager;
import be.noki_senpai.NKregion.managers.QueueManager;
import be.noki_senpai.NKregion.managers.RegionManager;
import be.noki_senpai.NKregion.utils.CheckType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LockCmd
{
	PlayerManager playerManager = null;
	QueueManager queueManager = null;
	RegionManager regionManager = null;

	public LockCmd(PlayerManager playerManager, QueueManager queueManager, RegionManager regionManager)
	{
		this.playerManager = playerManager;
		this.queueManager = queueManager;
		this.regionManager = regionManager;
	}

	public boolean lock(CommandSender sender, String[] args)
	{
		// land lock <region_name>
		if(sender instanceof Player)
		{
			sender.sendMessage("Cette commande ne peut pas être effectuée par un joueur.");
			return true;
		}
		else
		{
			final List<String> commands = new ArrayList<>();
			commands.add("rg flag " + args[1] + " -w " + args[1] + " build -g ALL deny");
			commands.add("rg flag " + args[1] + "_n -w " + args[1] + "_n build -g ALL deny");

			for(String cmd : commands)
			{
				new BukkitRunnable()
				{
					@Override public void run()
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

			return true;
		}
	}
}
