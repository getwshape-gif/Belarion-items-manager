package fr.belarion.belarionitems.nbt;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/**
 * Utilitaire NBT par reflexion, compatible avec toutes les revisions NMS 1.8
 * (v1_8_R1, v1_8_R2, v1_8_R3) sans dependance directe a une revision precise.
 * Ecrit un tag "BelarionItemId" qui survit a la reparation a l'enclume, a
 * l'enchantement et au deplacement d'inventaire (contrairement au material +
 * durability seuls, qui peuvent parfois etre ambigus si deux items partagent
 * la meme combinaison).
 */
public final class NBTUtil {

    private static final String NMS_VERSION;
    private static final String NBT_TAG_KEY = "BelarionItemId";

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        NMS_VERSION = packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    private NBTUtil() {
    }

    public static ItemStack setCustomItemId(ItemStack item, String id) {
        try {
            Class<?> craftItemStackClass = nmsClass("org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack", true);
            Class<?> nmsItemStackClass = nmsClass("net.minecraft.server.VERSION.ItemStack", false);
            Class<?> nbtTagCompoundClass = nmsClass("net.minecraft.server.VERSION.NBTTagCompound", false);

            Method asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            Object nmsItem = asNMSCopyMethod.invoke(null, item);

            Method getTagMethod = nmsItemStackClass.getMethod("getTag");
            Object tag = getTagMethod.invoke(nmsItem);
            if (tag == null) {
                tag = nbtTagCompoundClass.newInstance();
            }

            Method setStringMethod = nbtTagCompoundClass.getMethod("setString", String.class, String.class);
            setStringMethod.invoke(tag, NBT_TAG_KEY, id);

            Method setTagMethod = nmsItemStackClass.getMethod("setTag", nbtTagCompoundClass);
            setTagMethod.invoke(nmsItem, tag);

            Method asBukkitCopyMethod = craftItemStackClass.getMethod("asBukkitCopy", nmsItemStackClass);
            return (ItemStack) asBukkitCopyMethod.invoke(null, nmsItem);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("[BelarionItemsManager] Impossible d'ecrire le tag NBT : " + ex);
            return item;
        }
    }

    public static String getCustomItemId(ItemStack item) {
        if (item == null) {
            return null;
        }
        try {
            Class<?> craftItemStackClass = nmsClass("org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack", true);
            Class<?> nmsItemStackClass = nmsClass("net.minecraft.server.VERSION.ItemStack", false);
            Class<?> nbtTagCompoundClass = nmsClass("net.minecraft.server.VERSION.NBTTagCompound", false);

            Method asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            Object nmsItem = asNMSCopyMethod.invoke(null, item);
            if (nmsItem == null) {
                return null;
            }

            Method getTagMethod = nmsItemStackClass.getMethod("getTag");
            Object tag = getTagMethod.invoke(nmsItem);
            if (tag == null) {
                return null;
            }

            Method hasKeyMethod = nbtTagCompoundClass.getMethod("hasKey", String.class);
            boolean hasKey = (boolean) hasKeyMethod.invoke(tag, NBT_TAG_KEY);
            if (!hasKey) {
                return null;
            }

            Method getStringMethod = nbtTagCompoundClass.getMethod("getString", String.class);
            return (String) getStringMethod.invoke(tag, NBT_TAG_KEY);
        } catch (Exception ex) {
            return null;
        }
    }

    private static Class<?> nmsClass(String template, boolean craftbukkit) throws ClassNotFoundException {
        return Class.forName(template.replace("VERSION", NMS_VERSION));
    }
}
