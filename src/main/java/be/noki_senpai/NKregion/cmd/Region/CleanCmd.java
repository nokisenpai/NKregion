package be.noki_senpai.NKregion.cmd.Region;

import be.noki_senpai.NKregion.managers.PlayerManager;
import be.noki_senpai.NKregion.managers.QueueManager;
import be.noki_senpai.NKregion.managers.RegionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CleanCmd
{
	PlayerManager playerManager = null;
	QueueManager queueManager = null;
	RegionManager regionManager = null;

	public CleanCmd(PlayerManager playerManager, QueueManager queueManager, RegionManager regionManager)
	{
		this.playerManager = playerManager;
		this.queueManager = queueManager;
		this.regionManager = regionManager;
	}

	public boolean clean(CommandSender sender, String[] args)
	{
		if(sender instanceof Player)
		{
			sender.sendMessage("Cette commande ne peut pas être effectuée par un joueur.");
			return true;
		}
		else
		{




			return true;
		}
	}
}
