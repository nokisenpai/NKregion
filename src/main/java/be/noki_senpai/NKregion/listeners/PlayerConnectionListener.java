package be.noki_senpai.NKregion.listeners;

import be.noki_senpai.NKregion.managers.RegionManager;
import net.minecraft.server.v1_14_R1.NetworkManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionListener implements Listener
{
	private RegionManager regionManager = null;

	public PlayerConnectionListener(RegionManager regionManager)
	{
		this.regionManager = regionManager;
	}

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent event) 
	{
		Player player = event.getPlayer();
		if(regionManager.getTpRegion().containsKey(player.getName()))
		{
			String regionName = regionManager.getTpRegion().get(player.getName());
			regionManager.getRegions().get(regionName).teleport(player);
			regionManager.getTpRegion().remove(player.getName());
		}
	}
}
