package me.sshcrack.masterbuilders.tools.portal;

import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.tools.LocTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class StartPortal {
    private static boolean initialized = false;
    private static BukkitTask task;

    @Nullable
    public static Location getStartLoc() {
        FileConfiguration config = Main.plugin.getConfig();
        String startStr = config.getString("portal-start");
        if(startStr == null)
            return null;

        return LocTools.strToLoc(startStr);
    }

    public static Location getEndLoc() {
        FileConfiguration config = Main.plugin.getConfig();
        String endStr = config.getString("portal-end");
        if(endStr == null)
            return null;

        return LocTools.strToLoc(endStr);
    }

    public static void stop() {
        if(StartPortal.task == null)
            return;

        StartPortal.task.cancel();
    }

    public static void initialize() {
        if (StartPortal.initialized)
            return;

        StartPortal.initialized = true;

        StartPortal.task = Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> {
            Location start = StartPortal.getStartLoc();
            Location end = StartPortal.getEndLoc();

            if(start == null || end == null)
                return;

            Location diff = end.clone().subtract(start);
            double xSize = Math.abs(diff.getX());
            double ySize = Math.abs(diff.getY());
            double zSize = Math.abs(diff.getZ());

            double halfX = xSize /2;
            double halfY = ySize /2;
            double halfZ = zSize /2;

            int maxX = Math.max(start.getBlockX(), end.getBlockX());
            int minX = Math.min(start.getBlockX(), end.getBlockX());
            int minY = Math.min(start.getBlockY(), end.getBlockY());
            int maxY = Math.max(start.getBlockY(), end.getBlockY());
            int maxZ = Math.max(start.getBlockZ(), end.getBlockZ());
            int minZ = Math.min(start.getBlockZ(), end.getBlockZ());

            Location center = start.clone().set(minX + (maxX - minX), minY + (maxY - minY), minZ + (maxZ - minZ));
            new ParticleBuilder(ParticleEffect.DRAGON_BREATH)
                    .setAmount(50)
                    .setLocation(center)
                    .setSpeed(.01f)
                    .setOffset((float)halfX, (float)halfY, (float)halfZ)
                    .display();
        }, 0, 2L);
    }
}
