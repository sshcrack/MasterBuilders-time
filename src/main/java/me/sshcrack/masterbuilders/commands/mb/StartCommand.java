package me.sshcrack.masterbuilders.commands.mb;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.handler.GameModeFlag;
import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.*;
import me.sshcrack.masterbuilders.tools.timer.TeamTimer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.*;

public class StartCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;
        Team playerTeam = Tools.getMainScoreboard().getEntityTeam(player);
        if (playerTeam == null) {
            player.playSound(player.getLocation(), Sound.BLOCK_END_GATEWAY_SPAWN, 1, 0);
            return new CommandResponse("start.not_in_team");
        }

        FileConfiguration config = Main.plugin.getConfig();
        String teamId = playerTeam.getName();
        String topic = Main.plugin.getConfig().getString(String.format("selected.%s", teamId));
        if (topic == null) {
            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1, 0);
            List<String> available = config.getStringList("topics");

            TextComponent standard = new TextComponent(MessageManager.getMessage("start.topic_select"));
            player.sendMessage(standard);

            for (int i = 0; i < available.size(); i++) {
                String s = available.get(i);
                String command = String.format("/mb topic %s", i);

                String msg = ChatColor.translateAlternateColorCodes('&', String.format("&7%s. &6%s", i+1, s));
                TextComponent message = new TextComponent(msg);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Set topic").color(ChatColor.GOLD.asBungee()).create()));

                player.sendMessage(message);
            }
            return null;
        }

        String startStr = config.getString("start");
        String endStr = config.getString("end");
        String innerStartStr = config.getString("inner-start");
        String innerEndStr = config.getString("inner-end");
        String spawnStr = config.getString("spawn");
        String plotStr = config.getString("plot");

        if (startStr == null || endStr == null || spawnStr == null || plotStr == null || innerStartStr == null || innerEndStr == null) {
            player.sendMessage("Not set up yet. Aborting...");
            return null;
        }

        Location start = LocTools.strToLoc(startStr);
        Location end = LocTools.strToLoc(endStr);
        Location innerStart = LocTools.strToLoc(innerStartStr);
        Location innerEnd = LocTools.strToLoc(innerEndStr);
        Location plot = LocTools.strToLoc(plotStr);
        Location spawn = LocTools.strToLoc(spawnStr);

        assert start != null;
        assert end != null;
        assert plot != null;
        assert innerStart != null;
        assert innerEnd != null;
        assert spawn != null;


        RegionManager m = WG.getOverworldManager();
        boolean hasPlot = m.hasRegion(teamId);
        if (!hasPlot) {
            World world = Tools.vanillaToSketchyWorld(start.getWorld());
            BlockVector3 vecStart = Tools.locToBlockVec(start);
            BlockVector3 vecEnd = Tools.locToBlockVec(end);

            CuboidRegion region = new CuboidRegion(vecStart, vecEnd);
            Location diff = end.clone().subtract(start);
            int xSize = Math.abs(diff.getBlockX());
            int zSize = Math.abs(diff.getBlockZ());
            int ySize = Math.abs(diff.getBlockY());
            int maxSize = Math.max(xSize, zSize);

            int offset = config.getInt("offset", 0);

            int blocksAway = maxSize * offset;
            Vector dir = plot.getDirection();
            dir.setY(0);

            Location placeLoc = plot.clone().add(dir.multiply(blocksAway));
            BlockVector3 placeVec = BlockVector3.at(placeLoc.getBlockX(), placeLoc.getBlockY(), placeLoc.getBlockZ());
            Bukkit.getLogger().info(String.format("Placing new area at %s with size %s and offset %s Region start: %s and end: %s", placeVec, maxSize, offset, vecStart, vecEnd));

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                Operation operation = new ForwardExtentCopy(
                        world, region, editSession, placeVec
                );


                Operations.complete(operation);
            } catch (WorldEditException e) {
                player.sendMessage("Could not create plot.");
                e.printStackTrace();
            }
            Location pastedStartLoc = placeLoc.clone().add(xSize, 0, 0);
            Location pastedEndLoc = placeLoc.clone().add(0, ySize, zSize);

            Location pastedStartInnerLoc = pastedStartLoc.clone().subtract(innerEnd);
            Location pastedEndInnerLoc = pastedEndLoc.clone().subtract(innerStart);


            BlockVector3 pastedStartInnerVec = Tools.locToBlockVec(pastedStartInnerLoc);
            BlockVector3 pastedEndInnerVec = Tools.locToBlockVec(pastedEndInnerLoc);


            BlockVector3 pastedStartVec = Tools.locToBlockVec(pastedStartLoc);
            BlockVector3 pastedEndVec = Tools.locToBlockVec(pastedEndLoc);

            ProtectedRegion outerReg = new ProtectedCuboidRegion(teamId, pastedStartVec, pastedEndVec);
            ProtectedRegion innerReg = new ProtectedCuboidRegion(String.format("%s-inner", teamId), pastedStartInnerVec, pastedEndInnerVec);

            innerReg.setFlag(Flags.GAME_MODE, GameModes.CREATIVE);
            innerReg.setFlag(Flags.DESTROY_VEHICLE, StateFlag.State.ALLOW);

            m.addRegion(innerReg);
            m.addRegion(outerReg);
        }

        ProtectedCuboidRegion reg = (ProtectedCuboidRegion) m.getRegion(teamId);
        ProtectedCuboidRegion innerReg = (ProtectedCuboidRegion) m.getRegion(teamId + "-inner");
        assert reg != null;
        assert innerReg != null;
        org.bukkit.World overworld = Bukkit.getWorld("world");


        if (!GlobalVars.timers.containsKey(teamId)) {
            TeamTimer timer = new TeamTimer(teamId);
            GlobalVars.timers.put(teamId, timer);
        }

        TeamTimer timer = GlobalVars.timers.get(teamId);


        DefaultDomain domain = innerReg.getMembers();
        domain.removeAll();
        if(timer.getTimeLeft() > 0) {
            for (OfflinePlayer oPlayer : playerTeam.getPlayers()) {
                domain.addPlayer(oPlayer.getUniqueId());
            }
        }

        innerReg.setMembers(domain);
        try {
            innerReg.setParent(reg);
        } catch (ProtectedRegion.CircularInheritanceException e) {
            e.printStackTrace();
        }


        Vector3 min = reg.getMinimumPoint().toVector3();
        Vector3 max = reg.getMaximumPoint().toVector3();
        Location minLoc = new Location(overworld, min.getX(), min.getY(), min.getZ());
        Location maxLoc = new Location(overworld, max.getX(), max.getY(), max.getZ());
        Location diffLoc = maxLoc.subtract(minLoc);

        int xSize = Math.abs(diffLoc.getBlockX());
        Location startLoc = minLoc.clone().add(xSize, 0, 0);
        Location tpLoc = startLoc.clone().add(spawn);
        tpLoc.setYaw(spawn.getYaw());
        tpLoc.setPitch(spawn.getPitch());

        player.teleport(tpLoc);

        ArrayList<Location> locs = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            double o = .5;
            double y = i * .25;

            Location temp1 = tpLoc.clone().add(o, y, 0);
            Location temp2 = tpLoc.clone().add(0, y, o);
            Location temp3 = tpLoc.clone().add(o, y, o);
            Location temp4 = tpLoc.clone().add(-o, y, o);
            Location temp5 = tpLoc.clone().add(o, y, -o);
            Location temp6 = tpLoc.clone().add(-o, y, -o);
            Location temp7 = tpLoc.clone().add(-o, y, 0);
            Location temp8 = tpLoc.clone().add(0, y, -o);

            locs.add(temp1);
            locs.add(temp2);
            locs.add(temp3);
            locs.add(temp4);
            locs.add(temp5);
            locs.add(temp6);
            locs.add(temp7);
            locs.add(temp8);
        }

        for (Location loc : locs) {
            new ParticleBuilder(ParticleEffect.SNOWFLAKE)
                    .setSpeed(5)
                    .setLocation(loc)
                    .display();
        }
        player.playSound(tpLoc, Sound.BLOCK_BEEHIVE_ENTER, 1, 0);
        return null;
    }

    @Override
    public String getCommand() {
        return "start";
    }

    @Override
    public String getNode() {
        return "standard";
    }

    @Override
    public String getHelp() {
        return "Start a new master builders game";
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
