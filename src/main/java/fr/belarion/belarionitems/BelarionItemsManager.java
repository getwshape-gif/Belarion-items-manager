package fr.belarion.belarionitems;

import fr.belarion.belarionitems.api.BelarionItemsAPI;
import fr.belarion.belarionitems.api.BelarionItemsAPIImpl;
import fr.belarion.belarionitems.commands.BelarionItemsCommand;
import fr.belarion.belarionitems.config.ItemsConfig;
import fr.belarion.belarionitems.listeners.ItemIdentityListener;
import fr.belarion.belarionitems.registry.ItemRegistry;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class BelarionItemsManager extends JavaPlugin {

    private ItemsConfig itemsConfig;
    private ItemRegistry itemRegistry;
    private BelarionItemsAPI api;

    @Override
    public void onEnable() {
        this.itemRegistry = new ItemRegistry(this);
        this.itemsConfig = new ItemsConfig(this);
        this.itemsConfig.load();

        this.api = new BelarionItemsAPIImpl(this);
        getServer().getServicesManager().register(BelarionItemsAPI.class, api, this, ServicePriority.Normal);

        getCommand("belarionitems").setExecutor(new BelarionItemsCommand(this));
        getServer().getPluginManager().registerEvents(new ItemIdentityListener(this), this);

        getLogger().info("Belarion Items Manager active - " + itemsConfig.getItems().size() + " Custom Item(s).");
    }

    @Override
    public void onDisable() {
        getServer().getServicesManager().unregisterAll(this);
    }

    public ItemsConfig getItemsConfig() {
        return itemsConfig;
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public BelarionItemsAPI getApi() {
        return api;
    }
}
