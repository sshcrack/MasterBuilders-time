package me.sshcrack.masterbuilders.tools;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockIterator;

public class Tools {
    public static Scoreboard getMainScoreboard() {
        return Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public static BlockVector3 locToBlockVec(Location loc) {
        return BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static Location blockVecToLoc(BlockVector3 vec) {
        World world = Bukkit.getWorld("world");
        return new Location(world, vec.getX(), vec.getY(), vec.getZ());
    }

    public static com.sk89q.worldedit.world.World vanillaToSketchyWorld(World vanillaWorld) {
        return BukkitAdapter.adapt(vanillaWorld);
    }

    public static Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }

    public static boolean isBetween(Location l1, Location loc, Location l2) {
        return Tools.isBetween(l1, loc, l2, 0);
    }

    public static boolean isBetween(Location l1, Location loc, Location l2, int distance) {
        double x1 = Math.min(l1.getX(), l2.getX()) -distance;
        double y1 = Math.min(l1.getY(), l2.getY()) - distance;
        double z1 = Math.min(l1.getZ(), l2.getZ()) - distance;
        double x2 = Math.max(l1.getX(), l2.getX()) + distance;
        double y2 = Math.max(l1.getY(), l2.getY()) + distance;
        double z2 = Math.max(l1.getZ(), l2.getZ()) + distance;

        return loc.getX() >= x1 && loc.getX() <= x2
                && loc.getY() >= y1 && loc.getY() <= y2
                && loc.getZ() >= z1 && loc.getZ() <= z2;
    }
}
