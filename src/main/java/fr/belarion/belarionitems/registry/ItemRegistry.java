package fr.belarion.belarionitems.registry;

import fr.belarion.belarionitems.BelarionItemsManager;
import fr.belarion.belarionitems.model.CustomItemDefinition;
import fr.belarion.belarionitems.nbt.NBTUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemRegistry {

private final BelarionItemsManager plugin;

public ItemRegistry(BelarionItemsManager plugin) {
    this.plugin = plugin;
}

public ItemStack build(CustomItemDefinition definition) {
    ItemStack item = new ItemStack(definition.getMaterial(), 1);

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

    String id = NBTUtil.getCustomItemId(item);
    if (id == null) {
        return null;
    }

    return plugin.getItemsConfig().getItems().get(id.toLowerCase());
}
}
