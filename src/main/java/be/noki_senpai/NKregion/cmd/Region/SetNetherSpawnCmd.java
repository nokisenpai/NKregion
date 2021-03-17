package be.noki_senpai.NKregion.cmd.Region;

import be.noki_senpai.NKregion.managers.PlayerManager;
import be.noki_senpai.NKregion.managers.QueueManager;
import be.noki_senpai.NKregion.managers.RegionManager;
import be.noki_senpai.NKregion.utils.CheckType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetNetherSpawnCmd
{
	PlayerManager playerManager = null;
	QueueManager queueManager = null;
	RegionManager regionManager = null;

	public SetNetherSpawnCmd(PlayerManager playerManager, QueueManager queueManager, RegionManager regionManager)
	{
		this.playerManager = playerManager;
		this.queueManager = queueManager;
		this.regionManager = regionManager;
	}

	public boolean setNetherSpawn(CommandSender sender, String[] args)
	{
		//land setNetherSpawn <regionName> <x> <y> <z>
		//land       0             1        2   3   4
		if(args.length < 5)
		{
			sender.sendMessage(ChatColor.GREEN + "/land setNetherSpawn <regionName> <x> <y> <z>");
			return true;
		}

		if(!CheckType.isNumber(args[2]))
		{
			sender.sendMessage(ChatColor.GREEN + "La valeur de X doit être un nombre.");
			return true;
		}
		Double x = Double.valueOf(args[2]);

		if(!CheckType.isNumber(args[3]))
		{
			sender.sendMessage(ChatColor.GREEN + "La valeur de Y doit être un nombre.");
			return true;
		}
		Double y = Double.valueOf(args[3]);

		if(!CheckType.isNumber(args[4]))
		{
			sender.sendMessage(ChatColor.GREEN + "La valeur de Z doit être un nombre.");
			return true;
		}
		Double z = Double.valueOf(args[4]);

		regionManager.setNetherSpawnRegion(args[1] , x , y , z);

		sender.sendMessage(ChatColor.GREEN + "Spawn pour nether de la region " + ChatColor.AQUA + args[1] + ChatColor.GREEN + " : " + x + " | " + y + " | " + z);

		return true;
	}
}
