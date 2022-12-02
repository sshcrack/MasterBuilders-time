package me.sshcrack.masterbuilders.tools;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WG {
    public static WorldGuard getInstance() {
        return WorldGuard.getInstance();
    }

    public static RegionContainer getContainer() {
        return WG.getInstance().getPlatform().getRegionContainer();
    }

    public static RegionManager getOverworldManager() {
        World vanillaWorld = Bukkit.getWorld("world");
        assert vanillaWorld != null;

        return WG.getContainer().get(Tools.vanillaToSketchyWorld(vanillaWorld));
    }

    public static ProtectedRegion getOverworldRegion(String name) {
        return WG.getOverworldManager().getRegion(name);
    }
}
