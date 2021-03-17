package be.noki_senpai.NKregion.cmd.Region;

import be.noki_senpai.NKregion.data.NKPlayer;
import be.noki_senpai.NKregion.managers.PlayerManager;
import be.noki_senpai.NKregion.managers.QueueManager;
import be.noki_senpai.NKregion.managers.RegionManager;
import be.noki_senpai.NKregion.utils.CheckType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class CreateCmd
{
	PlayerManager playerManager = null;
	QueueManager queueManager = null;
	RegionManager regionManager = null;

	public CreateCmd(PlayerManager playerManager, QueueManager queueManager, RegionManager regionManager)
	{
		this.playerManager = playerManager;
		this.queueManager = queueManager;
		this.regionManager = regionManager;
	}

	public boolean create(CommandSender sender, String[] args)
	{
		// regionrequest playerName worldName seed x z worldDifficulty netherDifficulty regionName...
		// Command called by a player
		if(sender instanceof Player)
		{
			sender.sendMessage("Cette commande ne peut pas être effectuée par un joueur.");
			return true;
		}
		else
		{
			if(args.length < 8)
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Arguments manquant. Vérifiez la syntaxe ( /regionrequest <playerName> <seed> <x> <z> <worldDifficulty> <netherDifficulty> <regionName...>)");
				return true;
			}

			String targetName = args[1];
			String seed = args[2];

			// Check if X value is a number
			if(!CheckType.isNumber(args[3]))
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "La valeur de X doit être un nombre");
				return true;
			}
			Double x = Double.valueOf(args[3]);

			// Check if Z value is a number
			if(!CheckType.isNumber(args[4]))
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "La valeur de Z doit être un nombre");
				return true;
			}
			Double z = Double.valueOf(args[4]);

			String worldDifficulty = args[5];
			String netherDifficulty = args[6];

			String regionName = "";

			for(int i = 7; i < args.length; i++)
			{
				regionName += args[i] + " ";
			}
			regionName = regionName.substring(0, regionName.length() - 1);
			String worldName = regionName.replaceAll("[^a-zA-Z0-9]", "");

			String finalRegionName = regionName;
			queueManager.addToQueue(new Function()
			{
				@Override
				public Object apply(Object o)
				{
					// Get the player
					NKPlayer player = playerManager.getPlayer(targetName);

					// Check if player exist
					if(player == null)
					{
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " Le joueur '" + targetName + "' n'existe pas");
						return null;
					}

					regionManager.addRegion(player, worldName, seed, x, z, worldDifficulty, netherDifficulty, finalRegionName);

					return null;
				}
			});
		}
		return true;
	}
}

