package fr.belarion.belarionitems.listeners;

import fr.belarion.belarionitems.BelarionItemsManager;
import org.bukkit.event.Listener;

/**
 * Reserve pour de futurs hooks de preservation d'identite (ex: reparation a
 * l'enclume). Les evenements PrepareAnvilEvent / PrepareItemEnchantEvent ne
 * sont pas disponibles dans cette version de spigot-api (1.8.8) et ont ete
 * retires pour permettre la compilation. Le tag NBT "BelarionItemId" (voir
 * NBTUtil) survit deja nativement a la plupart des operations d'inventaire
 * (deplacement, renommage, enchantement) sans traitement special ; seule la
 * reparation via enclume necessiterait un hook supplementaire si une perte
 * du tag NBT est constatee en jeu (a verifier : sur la plupart des serveurs
 * 1.8, le resultat d'une reparation a l'enclume conserve les NBT du premier
 * item place).
 */
public class ItemIdentityListener implements Listener {

    private final BelarionItemsManager plugin;

    public ItemIdentityListener(BelarionItemsManager plugin) {
        this.plugin = plugin;
    }
}
