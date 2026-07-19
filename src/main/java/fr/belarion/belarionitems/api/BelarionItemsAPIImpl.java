package fr.belarion.belarionitems.api;

import fr.belarion.belarionitems.BelarionItemsManager;
import fr.belarion.belarionitems.model.CustomItemDefinition;
import fr.belarion.belarionitems.nbt.NBTUtil;
import org.bukkit.inventory.ItemStack;

public class BelarionItemsAPIImpl implements BelarionItemsAPI {

    private final BelarionItemsManager plugin;

    public BelarionItemsAPIImpl(BelarionItemsManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        return getDefinition(item) != null;
    }

    @Override
    public String getCustomItemId(ItemStack item) {
        return NBTUtil.getCustomItemId(item);
    }

    @Override
    public CustomItemDefinition getDefinition(String id) {
        if (id == null) {
            return null;
        }
        return plugin.getItemsConfig().getItems().get(id.toLowerCase());
    }

    @Override
    public CustomItemDefinition getDefinition(ItemStack item) {
        return plugin.getItemRegistry().identify(item);
    }

    @Override
    public ItemStack giveItem(String id) {
        CustomItemDefinition definition = getDefinition(id);
        if (definition == null) {
            return null;
        }
        return plugin.getItemRegistry().build(definition);
    }
}
