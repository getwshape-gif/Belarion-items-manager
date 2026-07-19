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

/**
 * Charge, valide et expose le contenu de items.yml.
 * Ajouter un item = ajouter une entree dans items.yml + /bi reload. Aucun code Java requis.
 */
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
                int durabilityData = itemSection.getInt("durability-data", -1);
                if (durabilityData < 0) {
                    plugin.getLogger().warning("Item '" + id + "' ignore : 'durability-data' manquant ou invalide.");
                    continue;
                }

                String rawName = itemSection.getString("name", id);
                String displayName = ChatColor.translateAlternateColorCodes('&', rawName);

                List<String> rawLore = itemSection.getStringList("lore");
                List<String> lore = new ArrayList<>();
                for (String line : rawLore) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }

                CustomItemDefinition definition = new CustomItemDefinition(
                        id, material, texture, durabilityData, displayName, lore);

                if (isDurabilityDataTaken(definition)) {
                    plugin.getLogger().warning("Item '" + id + "' : durability-data " + durabilityData
                            + " deja utilise par un autre item avec le meme material. "
                            + "Conflit possible dans le resource pack.");
                }

                items.put(id.toLowerCase(), definition);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Erreur en chargeant l'item '" + id + "'", ex);
            }
        }

        plugin.getLogger().info(items.size() + " Custom Item(s) charge(s) depuis items.yml");
    }

    private boolean isDurabilityDataTaken(CustomItemDefinition candidate) {
        for (CustomItemDefinition existing : items.values()) {
            if (existing.getMaterial() == candidate.getMaterial()
                    && existing.getDurabilityData() == candidate.getDurabilityData()) {
                return true;
            }
        }
        return false;
    }

    public void reload() {
        load();
    }

    public Map<String, CustomItemDefinition> getItems() {
        return items;
    }
}
