package me.sshcrack.masterbuilders;

import fr.minuskube.inv.InventoryManager;
import me.sshcrack.masterbuilders.commands.ParentCommand;
import me.sshcrack.masterbuilders.commands.mb.*;
import me.sshcrack.masterbuilders.events.PlotListener;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.portal.StartPortal;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    public static Main plugin;
    public InventoryManager invManager;
    private PlotListener listener;
    public static String name = "MasterBuilders";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;

        MessageManager.lang = getConfig().getString("language");
        assert MessageManager.lang != null;
        if (MessageManager.lang.equals("en") || MessageManager.lang.equals("")) {
            MessageManager.lang = "messages";
        }

        setupInv();

        loadCustomConfigs();
        loadConfig();

        setupListeners();
        setupCommands();
        StartPortal.initialize();
    }

    @Override
    public void onDisable() {
        StartPortal.stop();
        save();
    }

    public void setupInv() {
        this.invManager = new InventoryManager(this);
        this.invManager.init();
    }

    public void reload() {
        onDisable();
        reloadConfig();
        onEnable();
    }

    public void save() {
        FileConfiguration config = this.getConfig();
        this.listener.save();

        this.saveConfig();
    }

    public void loadConfig() {
        FileConfiguration config = this.getConfig();

        String template = config.getString("TEMPLATE");
    }

    public void setupCommands() {
        ParentCommand mbParent = new ParentCommand("mb");

        mbParent.addSubCommand(new InviteCommand());
        mbParent.addSubCommand(new AcceptCommand());
        mbParent.addSubCommand(new LeaveCommand());
        mbParent.addSubCommand(new CreateCommand());
        mbParent.addSubCommand(new StartCommand());
        mbParent.addSubCommand(new PlotCommand());
        mbParent.addSubCommand(new RegionCommand());
        mbParent.addSubCommand(new InnerCommand());
        mbParent.addSubCommand(new StartPortalCommand());
        mbParent.addSubCommand(new SpawnCommand());
        mbParent.addSubCommand(new SpawnSetCommand());

        new CustomCommand("mb", mbParent, "masterbuilders.standard", "Manage teams with this command",
                getConfig().getStringList("command.template"));
    }

    public void setupListeners() {
        Bukkit.getLogger().info("Setting up listeners...");
        this.listener = new PlotListener(this);
        getServer().getPluginManager().registerEvents(listener, this);

    }
    /**
     * This method is used to add any config values which are required post 3.0
     *
     * @param messages
     */
    private void addDefaults(YamlConfiguration messages) {
        File f = MessageManager.getFile();

        // if something has been changed, saving the new config
        if (!f.exists()) {
            Bukkit.getLogger().info("Saving new messages to messages.yml");

            try {
                messages.save(f);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
            }
        }
    }

    public void loadCustomConfigs() {

        File f = MessageManager.getFile();

        try {
            if (!f.exists()) {
                saveResource(MessageManager.lang + ".yml", false);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Could not load selected language: " + MessageManager.lang);
            MessageManager.lang = "messages";
            loadCustomConfigs();
            return;
        }

        YamlConfiguration messages = YamlConfiguration.loadConfiguration(f);
        addDefaults(messages);

        MessageManager.addMessages(messages);
    }
}

