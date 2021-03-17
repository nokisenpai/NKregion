package be.noki_senpai.NKregion.cmd;

import be.noki_senpai.NKregion.cmd.Region.*;
import be.noki_senpai.NKregion.managers.PlayerManager;
import be.noki_senpai.NKregion.managers.QueueManager;
import be.noki_senpai.NKregion.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class LandCmd implements CommandExecutor
{

	PlayerManager playerManager = null;
	QueueManager queueManager = null;
	RegionManager regionManager = null;

	public LandCmd(PlayerManager playerManager, QueueManager queueManager, RegionManager regionManager)
	{
		this.playerManager = playerManager;
		this.queueManager = queueManager;
		this.regionManager = regionManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
	{
		// if no argument
		if (args.length == 0)
		{
			if(sender instanceof Player)
			{
				tpLand((Player) sender);
			}

			if(sender instanceof ConsoleCommandSender)
			{
				sender.sendMessage(ChatColor.RED + "Cette commande ne peut pas être effectuée en console.");
			}
			return true;
		}

		args[0] = args[0].toLowerCase();
		switch (args[0])
		{
			case "create":
				return new CreateCmd(playerManager, queueManager, regionManager).create(sender, args);
			case "tp":
				return new TpCmd(playerManager, queueManager, regionManager).tp(sender, args);
			case "lock":
				return new LockCmd(playerManager, queueManager, regionManager).lock(sender, args);
			case "remove":
				return new RemoveCmd(playerManager, queueManager, regionManager).remove(sender, args);
			case "clean":
				return new CleanCmd(playerManager, queueManager, regionManager).clean(sender, args);
			case "restore":
				return new RestoreCmd(playerManager, queueManager, regionManager).restore(sender, args);
			case "setworldspawn":
				return new SetWorldSpawnCmd(playerManager, queueManager, regionManager).setWorldSpawn(sender, args);
			case "setnetherspawn":
				return new SetNetherSpawnCmd(playerManager, queueManager, regionManager).setNetherSpawn(sender, args);
			default:
				sender.sendMessage(ChatColor.RED + "Cette commande n'existe pas.");
				return true;
		}
	}

	public void tpLand(Player player)
	{
		String netherName = player.getWorld().getName();
		String worldName = netherName.substring(0, netherName.length() - 2);
		if(regionManager.getRegions().containsKey(worldName))
		{
			regionManager.getRegions().get(worldName).teleportLand(player);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Vous n'êtes pas dans le nether d'une région.");
		}
	}
}
