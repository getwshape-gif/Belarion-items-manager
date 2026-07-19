package fr.belarion.belarionitems.config;

import fr.belarion.belarionitems.BelarionItemsManager;
import fr.belarion.belarionitems.model.CustomItemDefinition;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ItemsConfig {

private final BelarionItemsManager plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<String, CustomItemDefinition> items = new LinkedHashMap<>();

public ItemsConfig(BelarionItemsManager plugin) {
    this.plugin = plugin;
    this.file = new File(plugin.getDataFolder(), "items.yml");
}

public void load() {
    if (!file.exists()) {
        plugin.saveResource("items.yml", false);
    }

    config = YamlConfiguration.loadConfiguration(file);
    items.clear();

    ConfigurationSection section = config.getConfigurationSection("items");
    if (section == null) {
        plugin.getLogger().warning("Aucune section 'items' trouvee dans items.yml");
        return;
    }

    for (String id : section.getKeys(false)) {
        ConfigurationSection itemSection = section.getConfigurationSection(id);
        if (itemSection == null) {
            continue;
        }

    try {
        String materialName = itemSection.getString("material");
        if (materialName == null) {
            plugin.getLogger().warning("Item '" + id + "' ignore : 'material' manquant.");
            continue;
        }

        Material material = Material.matchMaterial(materialName.toUpperCase());
        if (material == null) {
            plugin.getLogger().warning("Item '" + id + "' ignore : material '" + materialName + "' invalide.");
            continue;
        }

        String texture = itemSection.getString("texture", id);

        String rawName = itemSection.getString("name", id);
        String displayName = ChatColor.translateAlternateColorCodes('&', rawName);

        List<String> rawLore = itemSection.getStringList("lore");
        List<String> lore = new ArrayList<>();
        for (String line : rawLore) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        CustomItemDefinition definition = new CustomItemDefinition(
            id, material, texture, displayName, lore);

        items.put(id.toLowerCase(), definition);
    } catch (Exception ex) {
        plugin.getLogger().log(Level.WARNING, "Erreur en chargeant l'item '" + id + "'", ex);
    }
    }

    plugin.getLogger().info(items.size() + " Custom Item(s) charge(s) depuis items.yml");
}

public void reload() {
    load();
}

public Map<String, CustomItemDefinition> getItems() {
    return items;
}
}
