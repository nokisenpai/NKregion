package be.noki_senpai.NKregion.cmd;

import be.noki_senpai.NKregion.cmd.Region.CreateCmd;
import be.noki_senpai.NKregion.cmd.Region.TpCmd;
import be.noki_senpai.NKregion.managers.PlayerManager;
import be.noki_senpai.NKregion.managers.QueueManager;
import be.noki_senpai.NKregion.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class NetherCmd implements CommandExecutor
{

	PlayerManager playerManager = null;
	QueueManager queueManager = null;
	RegionManager regionManager = null;

	public NetherCmd(PlayerManager playerManager, QueueManager queueManager, RegionManager regionManager)
	{
		this.playerManager = playerManager;
		this.queueManager = queueManager;
		this.regionManager = regionManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			String worldName = player.getWorld().getName();
			if(regionManager.getRegions().containsKey(worldName))
			{
				regionManager.getRegions().get(worldName).teleportNether(player);
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Vous n'êtes pas dans une région.");
			}
		}

		if(sender instanceof ConsoleCommandSender)
		{
			sender.sendMessage(ChatColor.RED + "Cette commande ne peut pas être effectuée en console.");
		}

		return true;
	}
}
