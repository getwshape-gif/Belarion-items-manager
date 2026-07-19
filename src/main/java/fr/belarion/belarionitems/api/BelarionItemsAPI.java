package fr.belarion.belarionitems.api;

import fr.belarion.belarionitems.model.CustomItemDefinition;
import org.bukkit.inventory.ItemStack;

/**
 * API publique enregistree via le ServicesManager de Bukkit.
 * Utilisation depuis un autre plugin (ex: Belarion-Enchants) :
 *
 *   RegisteredServiceProvider&lt;BelarionItemsAPI&gt; provider =
 *           Bukkit.getServicesManager().getRegistration(BelarionItemsAPI.class);
 *   if (provider != null) {
 *       BelarionItemsAPI api = provider.getProvider();
 *       boolean custom = api.isCustomItem(item);
 *   }
 */
public interface BelarionItemsAPI {

    boolean isCustomItem(ItemStack item);

    String getCustomItemId(ItemStack item);

    CustomItemDefinition getDefinition(String id);

    CustomItemDefinition getDefinition(ItemStack item);

    ItemStack giveItem(String id);
}
