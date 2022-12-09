package me.sshcrack.masterbuilders.commands.mb;

import com.jonahseguin.absorb.scoreboard.Absorb;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.GlobalVars;
import me.sshcrack.masterbuilders.tools.LocTools;
import me.sshcrack.masterbuilders.tools.Tools;
import me.sshcrack.masterbuilders.tools.WG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Locale;

public class LeaveCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;
        Team playerTeam = Tools.getMainScoreboard().getEntityTeam(player);
        if (playerTeam == null)
            return new CommandResponse("leave.already");


        String teamName = playerTeam.getName().toLowerCase();
        playerTeam.removePlayer(player);
        for (OfflinePlayer offlineP : playerTeam.getPlayers()) {
            if (!offlineP.isOnline())
                continue;

            Player p = Bukkit.getPlayer(offlineP.getUniqueId());
            MessageManager.sendMessageF(p, "leave.left_passive", player.getName());
        }
        if (playerTeam.getEntries().size() == 0) {
            playerTeam.unregister();
            ProtectedRegion region = WG.getOverworldRegion(teamName);
            if (region != null) {
                BlockVector3 start = region.getMinimumPoint();
                BlockVector3 end = region.getMaximumPoint();
                World world = Tools.vanillaToSketchyWorld(Bukkit.getWorld("world"));

                Bukkit.getLogger().info(String.format("Removing plot from %s to %s", start, end));
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                    CuboidRegion editRegion = new CuboidRegion(start, end);
                    BlockState air = BukkitAdapter.adapt(Material.AIR.createBlockData());

                    editSession.setBlocks(editRegion, air);
                } catch (WorldEditException e) {
                    player.sendMessage("Could not create plot.");
                    e.printStackTrace();
                }

                WG.getOverworldManager().removeRegion(teamName, RemovalStrategy.REMOVE_CHILDREN);
            }

            FileConfiguration config = Main.plugin.getConfig();
            if (GlobalVars.timers.containsKey(teamName)) {
                GlobalVars.timers.get(teamName).stop();
            }
            GlobalVars.timers.remove(teamName);

            config.set("selected." + teamName, null);
            config.set("time." + teamName, null);
            if (GlobalVars.scoreboards.containsKey(player.getUniqueId())) {
                Absorb a = GlobalVars.scoreboards.get(player.getUniqueId());
                a.getViews().forEach((k, v) -> {
                    a.unregisterView(v);
                });
                a.hide();
                GlobalVars.scoreboards.remove(player.getUniqueId());
            }

            String spawnStr = config.getString("tp-spawn");
            if (spawnStr != null) {
                Location spawn = LocTools.strToLoc(spawnStr);
                if (spawn != null)
                    player.teleport(spawn);
            }
        }

        MessageManager.sendMessageF(player, "leave.left");
        return null;
    }

    @Override
    public String getCommand() {
        return "leave";
    }

    @Override
    public String getNode() {
        return "standard";
    }

    @Override
    public String getHelp() {
        return "Leaves the team you are currently in.";
    }

    @Override
    public String getArguments() {
        return "";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
    }

    @Override
    public int getMaximumArguments() {
        return 0;
    }

}
