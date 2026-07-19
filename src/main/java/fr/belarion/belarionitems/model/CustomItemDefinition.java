package fr.belarion.belarionitems.model;

import org.bukkit.Material;

import java.util.Collections;
import java.util.List;

public class CustomItemDefinition {

private final String id;
    private final Material material;
    private final String texture;
    private final String displayName;
    private final List<String> lore;

public CustomItemDefinition(String id, Material material, String texture, String displayName, List<String> lore) {
    this.id = id;
    this.material = material;
    this.texture = texture;
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

public String getDisplayName() {
    return displayName;
}

public List<String> getLore() {
    return lore;
}
}
