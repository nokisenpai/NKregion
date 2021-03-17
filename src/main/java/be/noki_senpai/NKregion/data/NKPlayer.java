package be.noki_senpai.NKregion.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import be.noki_senpai.NKregion.managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import be.noki_senpai.NKregion.NKregion;

public class NKPlayer
{
	private int id;
	private UUID playerUUID;
	private String playerName;

	public NKPlayer(UUID UUID)
	{
		setPlayerUUID(UUID);

		Connection bdd = null;
		ResultSet resultat = null;
		PreparedStatement ps = null;
		String req = null;

		try
		{
			bdd = DatabaseManager.getConnection();

			// Get 'id', 'uuid', 'name', 'amount' and 'home_tp' from database
			req = "SELECT id, name FROM " + DatabaseManager.common.PLAYERS + " WHERE uuid = ?";
			ps = bdd.prepareStatement(req);
			ps.setString(1, getPlayerUUID().toString());

			resultat = ps.executeQuery();

			// If there is a result account exist
			if(resultat.next())
			{
				setId(resultat.getInt("id"));
				String playerName = resultat.getString("name");
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while setting a player. (#1)");
			}
			ps.close();
			resultat.close();
		}
		catch(SQLException e)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + NKregion.PNAME + " Error while getting a player. (Error#data.Players.000)");
			e.printStackTrace();
		}
	}

	//######################################
	// Getters & Setters
	//######################################

	// Getter & Setter 'id'
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	// Getter & Setter 'playerUUID'
	public UUID getPlayerUUID()
	{
		return playerUUID;
	}

	public void setPlayerUUID(UUID playerUUID)
	{
		this.playerUUID = playerUUID;
	}

	// Getter & Setter 'playerName'
	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}
}
