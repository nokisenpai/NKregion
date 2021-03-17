package be.noki_senpai.NKregion.listeners;

import be.noki_senpai.NKregion.data.Region;
import be.noki_senpai.NKregion.data.RegionInterface;
import be.noki_senpai.NKregion.managers.RegionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class RegionListener implements Listener
{
	private RegionManager regionManager = null;

	public RegionListener(RegionManager regionManager)
	{
		this.regionManager = regionManager;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{

		if(!(e.getInventory().getHolder() instanceof RegionInterface))
		{
			return;
		}

		RegionInterface regionInterface = (RegionInterface) e.getInventory().getHolder();
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();

		ItemStack clickedItem = e.getCurrentItem();

		if(clickedItem == null || clickedItem.getType() == Material.AIR)
		{
			return;
		}

		if(e.getRawSlot() == 0 || e.getRawSlot() == 45)
		{
			if(regionInterface.page != 0)
			{
				regionManager.getRegionInterface().get(regionInterface.page - 1).openInventory(p);
			}
			return;
		}

		if(e.getRawSlot() == 8 || e.getRawSlot() == 53)
		{
			if(regionInterface.page != (int) regionManager.getRegions().size() / 36)
			{
				regionManager.getRegionInterface().get(regionInterface.page + 1).openInventory(p);
			}
			return;
		}

		int regionId = (e.getRawSlot() - 9) + (regionInterface.page * 36);
		Region region = (new ArrayList<Region>(regionManager.getRegions().values())).get(regionId);
		(new ArrayList<Region>(regionManager.getRegions().values())).get(regionId).teleport(p);
	}
}