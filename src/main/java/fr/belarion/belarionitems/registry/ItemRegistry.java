package fr.belarion.belarionitems.registry;

import fr.belarion.belarionitems.BelarionItemsManager;
import fr.belarion.belarionitems.model.CustomItemDefinition;
import fr.belarion.belarionitems.nbt.NBTUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**
 * Construit des ItemStack a partir d'une CustomItemDefinition, et identifie
 * un ItemStack existant (tag NBT en priorite, repli sur material+durability).
 */
public class ItemRegistry {

    private final BelarionItemsManager plugin;

    public ItemRegistry(BelarionItemsManager plugin) {
        this.plugin = plugin;
    }

    public ItemStack build(CustomItemDefinition definition) {
        ItemStack item = new ItemStack(definition.getMaterial(), 1, (short) definition.getDurabilityData());

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(definition.getDisplayName());
            meta.setLore(definition.getLore());
            item.setItemMeta(meta);
        }

        return NBTUtil.setCustomItemId(item, definition.getId());
    }

    public CustomItemDefinition identify(ItemStack item) {
        if (item == null) {
            return null;
        }

        Map<String, CustomItemDefinition> items = plugin.getItemsConfig().getItems();

        String id = NBTUtil.getCustomItemId(item);
        if (id != null) {
            CustomItemDefinition byId = items.get(id.toLowerCase());
            if (byId != null) {
                return byId;
            }
        }

        // Repli si le tag NBT est absent (ex: item cree avant l'ajout du plugin) :
        // identification par material + durability-data.
        for (CustomItemDefinition definition : items.values()) {
            if (definition.getMaterial() == item.getType() && item.getDurability() == definition.getDurabilityData()) {
                return definition;
            }
        }

        return null;
    }
}
