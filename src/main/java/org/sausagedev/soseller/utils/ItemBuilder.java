package org.sausagedev.soseller.utils;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemBuilder {
    private ItemStack item;
    private String function;
    private Material material;
    private int amount;
    private final ItemMeta meta;
    private int model_data;
    private String name;
    private List<String> lore = new ArrayList<>();
    private List<ItemFlag> flags = new ArrayList<>();
    private Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();

    public ItemBuilder(ItemStack item) {
        this.item = new ItemStack(item);
        material = item.getType();
        amount = item.getAmount();
        enchants.putAll(item.getEnchantments());
        meta = item.getItemMeta();

        if (meta != null) {
            if (meta.hasDisplayName()) {
                name = meta.getDisplayName();
            }
            if (meta.hasLore()) {
                lore.addAll(Objects.requireNonNull(meta.getLore()));
            }
            flags.addAll(meta.getItemFlags());
        }
    }

    public ItemBuilder function(String function) {
        this.function = function;
        NBTItem icon = new NBTItem(item);
        icon.setString("SoSeller", function);
        item = icon.getItem();
        return this;
    }

    public ItemBuilder material(Material material) {
        this.material = material;
        item.setType(material);
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder modelData(int model_data) {
        this.model_data = model_data;
        meta.setCustomModelData(model_data);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder name(String name) {
        this.name = name;
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = lore;
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder flags(List<ItemFlag> flags) {
        this.flags = flags;
        flags.forEach(meta::addItemFlags);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder enchants(Map<Enchantment, Integer> enchants) {
        this.enchants = enchants;
        enchants.forEach((enchant, lvl) -> {if (enchant != null) meta.addEnchant(enchant, lvl, true);});
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack item() {
        return item;
    }

    public String function() {
        return function;
    }

    public Material material() {
        return material;
    }

    public int amount() {
        return amount;
    }

    public ItemMeta meta() {
        return meta;
    }

    public int modelData() {
        return model_data;
    }

    public String name() {
        return name;
    }

    public List<String> lore() {
        return lore;
    }

    public List<ItemFlag> flags() {
        return flags;
    }

    public Map<Enchantment, Integer> enchants() {
        return enchants;
    }
}
