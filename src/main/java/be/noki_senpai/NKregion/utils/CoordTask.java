package be.noki_senpai.NKregion.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;

public class CoordTask
{
	public static double roundFive(double number)
	{
		if(number >= 0)
		{
			return ((int) number) + 0.5;
		}
		else
		{
			return ((int) number) - 0.5;
		}
	}

	public static int BlockCoord(double number)
	{
		if(number < 0)
		{
			return ((int) number) - 1;
		}
		else
		{
			return (int) number;
		}
	}

	public static Location safeLocation(Location location)
	{
		Location safeLocation = null;
		if(location.getBlock().getType().isTransparent())
		{
			if(location.getBlock().getRelative(BlockFace.UP).getType().isTransparent() && location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType().isTransparent())
			{
				safeLocation = location;
			}
		}
		else
		{
			safeLocation = location.getWorld().getHighestBlockAt(location).getLocation();
		}
		return safeLocation;
	}

	public static Location safeNetherLocation(Location location)
	{
		if(!location.getBlock().getType().isTransparent())
		{
			location.getBlock().setType(Material.AIR);
		}
		if(!location.getBlock().getRelative(BlockFace.DOWN).getType().isTransparent())
		{
			location.getBlock().getRelative(BlockFace.UP).setType(Material.OBSIDIAN);
		}
		if(!location.getBlock().getRelative(BlockFace.UP).getType().isTransparent())
		{
			location.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
		}
		if(!location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType().isTransparent())
		{
			location.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.AIR);
		}
		return location;
	}
}
