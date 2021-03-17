package be.noki_senpai.NKregion.cmd.Region;

import be.noki_senpai.NKregion.NKregion;
import be.noki_senpai.NKregion.managers.PlayerManager;
import be.noki_senpai.NKregion.managers.QueueManager;
import be.noki_senpai.NKregion.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveCmd
{
	PlayerManager playerManager = null;
	QueueManager queueManager = null;
	RegionManager regionManager = null;

	public RemoveCmd(PlayerManager playerManager, QueueManager queueManager, RegionManager regionManager)
	{
		this.playerManager = playerManager;
		this.queueManager = queueManager;
		this.regionManager = regionManager;
	}

	public boolean remove(CommandSender sender, String[] args)
	{
		if(sender instanceof Player)
		{
			sender.sendMessage("Cette commande ne peut pas être effectuée par un joueur.");
		}
		else
		{
			regionManager.removeRegion(args[1]);
		}
		return true;
	}
}
