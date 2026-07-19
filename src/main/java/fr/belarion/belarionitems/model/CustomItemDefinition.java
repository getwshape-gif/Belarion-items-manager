package fr.belarion.belarionitems.model;

import org.bukkit.Material;

import java.util.Collections;
import java.util.List;

/**
 * Represente la definition d'un Custom Item telle que chargee depuis items.yml.
 * Immuable : une fois chargee, une definition ne change pas (un reload recree les instances).
 */
public class CustomItemDefinition {

    private final String id;
    private final Material material;
    private final String texture;
    private final int durabilityData;
    private final String displayName;
    private final List<String> lore;

    public CustomItemDefinition(String id, Material material, String texture, int durabilityData,
                                 String displayName, List<String> lore) {
        this.id = id;
        this.material = material;
        this.texture = texture;
        this.durabilityData = durabilityData;
        this.displayName = displayName;
        this.lore = lore == null ? Collections.emptyList() : lore;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getTexture() {
        return texture;
    }

    public int getDurabilityData() {
        return durabilityData;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }
}
