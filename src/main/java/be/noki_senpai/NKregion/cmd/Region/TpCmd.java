package be.noki_senpai.NKregion.cmd.Region;

import be.noki_senpai.NKregion.managers.PlayerManager;
import be.noki_senpai.NKregion.managers.QueueManager;
import be.noki_senpai.NKregion.managers.RegionManager;
import be.noki_senpai.NKregion.utils.CheckType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TpCmd
{
	PlayerManager playerManager = null;
	QueueManager queueManager = null;
	RegionManager regionManager = null;

	public TpCmd(PlayerManager playerManager, QueueManager queueManager, RegionManager regionManager)
	{
		this.playerManager = playerManager;
		this.queueManager = queueManager;
		this.regionManager = regionManager;
	}

	public boolean tp(CommandSender sender, String[] args)
	{
		// Command called by a player
		if(sender instanceof Player)
		{
			int page = 1;
			if(!hasRegionTpPermissions(sender))
			{
				sender.sendMessage(ChatColor.RED + " Vous n'avez pas la permission !");
				return true;
			}

			if(args.length >= 2)
			{
				// Check if Z value is a number
				if(!CheckType.isNumber(args[1]))
				{
					sender.sendMessage(ChatColor.RED + "La page doit être un nombre");
					return true;
				}
				page = Integer.parseInt(args[1]);
				if(page > (int) regionManager.getRegions().size() / 36)
				{
					page = (int) regionManager.getRegions().size() / 36;
				}
				if(page < 0)
				{
					page = 0;
				}
			}
			if(regionManager.getRegionInterface().containsKey(page-1))
			{
				regionManager.getRegionInterface().get(page - 1).openInventory((Player) sender);
			}
			return true;
		}

		// Command called by Console
		if(sender instanceof ConsoleCommandSender)
		{
			sender.sendMessage("Cette commande ne peut pas être effectuée en console.");
			return true;
		}
		return true;
	}

	private boolean hasRegionTpPermissions(CommandSender sender)
	{
		return sender.hasPermission("*") || sender.hasPermission("nkregion.*") || sender.hasPermission("nkregion.land.tp")
				|| sender.hasPermission("nkregion.admin");
	}
}
