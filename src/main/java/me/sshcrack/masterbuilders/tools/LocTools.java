package me.sshcrack.masterbuilders.tools;

import me.sshcrack.masterbuilders.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LocTools {
    public static Location strToLoc(@NotNull String str) {
        String[] split = str.split("@");

        String rawLoc = split[0];
        String worldName = split[1];

        String[] locSplit = rawLoc.split(",");
        double x, y, z;
        float yaw, pitch;

        x = Double.parseDouble(locSplit[0]);
        y = Double.parseDouble(locSplit[1]);
        z = Double.parseDouble(locSplit[2]);

        yaw = Float.parseFloat(locSplit[3]);
        pitch = Float.parseFloat(locSplit[4]);

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        Location loc = new Location(world, x, y, z);

        loc.setYaw(yaw);
        loc.setPitch(pitch);

        return loc;
    }

    public static String locToStr(@NotNull Location loc) {
        double x, y, z;
        float yaw, pitch;

        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        yaw = loc.getYaw();
        pitch = loc.getPitch();

        return String.format("%,.2f,%,.2f,%,.2f,%f,%f@%s", x, y, z, yaw, pitch, loc.getWorld().getName());
    }
    public static Location getLowest(@NotNull Location start, Location end) {
        double x = Math.min(start.getX(), end.getX());
        double y = Math.min(start.getY(), end.getY());
        double z = Math.min(start.getZ(), end.getZ());

        return new Location(start.getWorld(), x, y, z);
    }

    public static Location lowestToConfigStart(@NotNull Location lowest, @NotNull Location diff) {
        FileConfiguration config = Main.plugin.getConfig();
        Location start = LocTools.strToLoc(Objects.requireNonNull(config.getString("start")));
        Location end = LocTools.strToLoc(Objects.requireNonNull(config.getString("end")));

        assert start != null;
        assert end != null;

        Location configLowest = LocTools.getLowest(start, end);
        Location offset = start.clone().subtract(configLowest);

        return lowest.clone().add(offset);
    }
}
