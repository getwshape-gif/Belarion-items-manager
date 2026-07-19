package fr.belarion.belarionitems.commands;

import fr.belarion.belarionitems.BelarionItemsManager;
import fr.belarion.belarionitems.model.CustomItemDefinition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BelarionItemsCommand implements CommandExecutor {

private final BelarionItemsManager plugin;

public BelarionItemsCommand(BelarionItemsManager plugin) {
    this.plugin = plugin;
}

@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

    switch (args[0].toLowerCase()) {
        case "give":
            return handleGive(sender, args);
        case "list":
            return handleList(sender);
        case "reload":
            return handleReload(sender);
        case "debug":
            return handleDebug(sender);
        default:
            sendHelp(sender);
            return true;
    }
    }

private boolean handleGive(CommandSender sender, String[] args) {
    if (!sender.hasPermission("belarionitems.admin")) {
        sender.sendMessage(ChatColor.RED + "Tu n'as pas la permission.");
        return true;
    }

    if (args.length < 3) {
        sender.sendMessage(ChatColor.RED + "Usage: /bi give <joueur> <item>");
        return true;
    }

    Player target = Bukkit.getPlayerExact(args[1]);
    if (target == null) {
        sender.sendMessage(ChatColor.RED + "Joueur introuvable : " + args[1]);
        return true;
    }

    String itemId = args[2];
    CustomItemDefinition definition = plugin.getItemsConfig().getItems().get(itemId.toLowerCase());
    if (definition == null) {
        sender.sendMessage(ChatColor.RED + "Item inconnu : " + itemId);
        return true;
    }

    ItemStack item = plugin.getItemRegistry().build(definition);
    target.getInventory().addItem(item);

    sender.sendMessage(ChatColor.GREEN + "Item '" + itemId + "' donne a " + target.getName() + ".");
    if (!sender.equals(target)) {
        target.sendMessage(ChatColor.GREEN + "Tu as recu : " + definition.getDisplayName());
    }
    return true;
}

private boolean handleList(CommandSender sender) {
    Map<String, CustomItemDefinition> items = plugin.getItemsConfig().getItems();
    if (items.isEmpty()) {
        sender.sendMessage(ChatColor.YELLOW + "Aucun Custom Item configure.");
        return true;
    }

    sender.sendMessage(ChatColor.GOLD + "=== Custom Items (" + items.size() + ") ===");
    for (CustomItemDefinition definition : items.values()) {
        sender.sendMessage(ChatColor.GRAY + "- " + definition.getId() + " " + ChatColor.DARK_GRAY
                           + "(" + definition.getMaterial() + ", texture " + definition.getTexture() + ")");
    }
    return true;
}

private boolean handleReload(CommandSender sender) {
    if (!sender.hasPermission("belarionitems.admin")) {
        sender.sendMessage(ChatColor.RED + "Tu n'as pas la permission.");
        return true;
    }

    plugin.getItemsConfig().reload();
    sender.sendMessage(ChatColor.GREEN + "items.yml recharge (" + plugin.getItemsConfig().getItems().size() + " item(s)).");
    return true;
}

private boolean handleDebug(CommandSender sender) {
    if (!(sender instanceof Player)) {
        sender.sendMessage(ChatColor.RED + "Commande reservee aux joueurs.");
        return true;
    }

    Player player = (Player) sender;
    ItemStack itemInHand = player.getItemInHand();

    if (itemInHand == null || itemInHand.getType() == org.bukkit.Material.AIR) {
        sender.sendMessage(ChatColor.RED + "Tu ne tiens aucun item.");
        return true;
    }

    CustomItemDefinition definition = plugin.getItemRegistry().identify(itemInHand);

    sender.sendMessage(ChatColor.GOLD + "=== Debug Item ===");
    sender.sendMessage(ChatColor.GRAY + "Material: " + ChatColor.WHITE + itemInHand.getType());
    sender.sendMessage(ChatColor.GRAY + "Durability actuelle: " + ChatColor.WHITE + itemInHand.getDurability());

    if (definition == null) {
        sender.sendMessage(ChatColor.RED + "Ce n'est pas un Custom Item reconnu.");
        return true;
    }

    sender.sendMessage(ChatColor.GRAY + "ID: " + ChatColor.WHITE + definition.getId());
    sender.sendMessage(ChatColor.GRAY + "Nom: " + ChatColor.WHITE + definition.getDisplayName());
    sender.sendMessage(ChatColor.GRAY + "Texture: " + ChatColor.WHITE + definition.getTexture());
    sender.sendMessage(ChatColor.GRAY + "Tag NBT BelarionItemId: " + ChatColor.WHITE
                       + fr.belarion.belarionitems.nbt.NBTUtil.getCustomItemId(itemInHand));
    return true;
}

private void sendHelp(CommandSender sender) {
    sender.sendMessage(ChatColor.GOLD + "=== Belarion Items Manager ===");
    sender.sendMessage(ChatColor.YELLOW + "/bi give <joueur> <item>" + ChatColor.GRAY + " - Donner un Custom Item");
    sender.sendMessage(ChatColor.YELLOW + "/bi list" + ChatColor.GRAY + " - Lister tous les Custom Items");
    sender.sendMessage(ChatColor.YELLOW + "/bi reload" + ChatColor.GRAY + " - Recharger items.yml");
    sender.sendMessage(ChatColor.YELLOW + "/bi debug" + ChatColor.GRAY + " - Debug de l'item en main");
}
}
