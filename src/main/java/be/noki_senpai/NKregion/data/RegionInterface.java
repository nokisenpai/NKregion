package be.noki_senpai.NKregion.data;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class RegionInterface implements InventoryHolder {
    public static Map<String, ItemStack[]> saveInv = new HashMap<>();
    public final Inventory inv;

    public int page = 0;

    public RegionInterface(Map<String, Region> regions, int page) {
        this.page = page;
        inv = Bukkit.createInventory(this, 54, "Liste des régions - Page " + (page + 1) + " sur " + (((int) regions.size() / 36) + 1));

        for (int k = 0; k < 9; k++) {
            inv.setItem(k, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        for (int k = 45; k < 54; k++) {
            inv.setItem(k, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        if (page != 0) {
            inv.setItem(0, createMenuItem("http://textures.minecraft.net/texture/74133f6ac3be2e2499a784efadcfffeb9ace025c3646ada67f3414e5ef3394", "Précédent"));
            inv.setItem(45, createMenuItem("http://textures.minecraft.net/texture/74133f6ac3be2e2499a784efadcfffeb9ace025c3646ada67f3414e5ef3394", "Précédent"));

        }

        if (page != (int) regions.size() / 36) {
            inv.setItem(8, createMenuItem("http://textures.minecraft.net/texture/e02fa3b2dcb11c6639cc9b9146bea54fbc6646d855bdde1dc6435124a11215d", "Suivant"));
            inv.setItem(53, createMenuItem("http://textures.minecraft.net/texture/e02fa3b2dcb11c6639cc9b9146bea54fbc6646d855bdde1dc6435124a11215d", "Suivant"));
        }

        int i = 9;
        int j = 0;
        for (Map.Entry<String, Region> region : regions.entrySet()) {
            if (j >= 36 * page && j < 36 * page + 36) {
                inv.setItem(i, createPlayerHead(region.getValue()));
                i++;
            }

            j++;
        }
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    // Nice little method to create a gui item with a custom name, and description
    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> metaLore = new ArrayList<String>();

        Collections.addAll(metaLore, lore);

        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPlayerHead(Region region) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        ArrayList<String> skullLore = new ArrayList<>();
        OfflinePlayer player = Bukkit.getOfflinePlayer(region.uuid);
        skullMeta.setOwningPlayer(player);
        skullLore.add(ChatColor.GREEN + "Région de " + ChatColor.BLUE + player.getName());
        skullLore.add(ChatColor.GRAY + "Cliquez pour vous rendre dans cette région.");
        skullMeta.setLore(skullLore);
        skullMeta.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + region.name);
        playerHead.setItemMeta(skullMeta);

        return playerHead;
    }

    private ItemStack createMenuItem(String url, String name) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        Field profileField = null;
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(data)));

        try {
            if (profileField == null) {
                profileField = skullMeta.getClass().getDeclaredField("profile");
            }

            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);

            playerHead.setItemMeta(skullMeta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        skullMeta.setDisplayName(ChatColor.GREEN + name);
        playerHead.setItemMeta(skullMeta);

        return playerHead;
    }

    // You can open the inventory with this
    public void openInventory(Player p) {
        p.openInventory(inv);
    }
}
