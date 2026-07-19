package fr.belarion.belarionitems.listeners;

import fr.belarion.belarionitems.BelarionItemsManager;
import fr.belarion.belarionitems.model.CustomItemDefinition;
import fr.belarion.belarionitems.nbt.NBTUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;

/**
 * S'assure qu'un Custom Item garde son identite (material, durability-data,
 * tag NBT) apres reparation a l'enclume, enchantement, ou tout autre
 * traitement vanilla susceptible de reconstruire l'ItemStack.
 */
public class ItemIdentityListener implements Listener {

    private final BelarionItemsManager plugin;

    public ItemIdentityListener(BelarionItemsManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result == null) {
            return;
        }

        ItemStack base = event.getInventory().getItem(0);
        if (base == null) {
            return;
        }

        CustomItemDefinition definition = plugin.getItemRegistry().identify(base);
        if (definition == null) {
            return;
        }

        // Reapplique le material/durability-data + le tag NBT sur le resultat de l'enclume
        // pour que l'identite custom survive a la reparation/renommage/combinaison.
        result.setDurability((short) definition.getDurabilityData());
        result = NBTUtil.setCustomItemId(result, definition.getId());
        event.setResult(result);
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();
        CustomItemDefinition definition = plugin.getItemRegistry().identify(item);
        if (definition == null) {
            return;
        }
        // Hook reserve pour une future logique de compatibilite avec Belarion-Enchants
        // (ex : autoriser/bloquer certains enchantements selon le type de Custom Item).
        // L'ItemStack lui-meme n'est pas modifie par Bukkit a cette etape : le
        // material/durability/NBT sont deja conserves nativement.
    }
}
