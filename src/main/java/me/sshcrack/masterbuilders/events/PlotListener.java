package me.sshcrack.masterbuilders.events;

import com.jonahseguin.absorb.scoreboard.Absorb;
import com.jonahseguin.absorb.view.View;
import com.jonahseguin.absorb.view.ViewUpdater;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.scoreboard.InfoScoreboard;
import me.sshcrack.masterbuilders.tools.GlobalVars;
import me.sshcrack.masterbuilders.tools.LocTools;
import me.sshcrack.masterbuilders.tools.portal.StartPortal;
import me.sshcrack.masterbuilders.tools.timer.TeamTimer;
import me.sshcrack.masterbuilders.tools.Tools;
import me.sshcrack.masterbuilders.tools.WG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlotListener implements Listener {
    Main plugin;
    ArrayList<UUID> teleporting = new ArrayList<>();


    public PlotListener(Main plugin) {
        this.plugin = plugin;
    }

    public void save() {
        GlobalVars.timers.forEach((k, e) -> e.stop());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Team t = Tools.getMainScoreboard().getPlayerTeam(p);

        if (t == null)
            return;

        ProtectedCuboidRegion r = (ProtectedCuboidRegion) WG.getOverworldManager().getRegion(t.getName().toLowerCase());
        if (r == null)
            return;

        Location min = Tools.blockVecToLoc(r.getMinimumPoint());
        Location max = Tools.blockVecToLoc(r.getMaximumPoint());

        UUID uuid = p.getUniqueId();
        Location l = p.getLocation();
        boolean inReg = Tools.isBetween(min, l, max);


        String tN = t.getName().toLowerCase();
        ArrayList<UUID> list = GlobalVars.inRegions.getOrDefault(tN, new ArrayList<>());
        if (inReg) {
            if (!list.contains(uuid))
                list.add(uuid);
        } else {
            list.remove(uuid);
        }

        GlobalVars.inRegions.put(tN, list);

        boolean someInPlot = list.size() != 0;
        if (!GlobalVars.timers.containsKey(tN)) {
            TeamTimer timer = new TeamTimer(tN);
            GlobalVars.timers.put(tN, timer);
        }

        TeamTimer timer = GlobalVars.timers.get(tN);
        if (someInPlot) {
            if (!timer.isRunning())
                timer.start();
        } else {
            if (timer.isRunning())
                timer.stop();
        }
/*
        if (!inReg) {
            if (this.scoreboards.containsKey(uuid))
                this.scoreboards.get(uuid).hide();
        } else {*/
        if (!GlobalVars.scoreboards.containsKey(uuid)) {
            Absorb absorb = new Absorb(plugin, p, true);
            View view = absorb.view(uuid.toString());
            view.provider(new InfoScoreboard(t, timer));
            absorb.activate(uuid.toString());
            absorb.show();

            ViewUpdater viewUpdater = new ViewUpdater(plugin, 2L); // Update every 2 ticks
            if (!viewUpdater.isRunning())
                viewUpdater.start();

            viewUpdater.registerBoard(absorb);

            GlobalVars.scoreboards.put(uuid, absorb);
        } else {
            GlobalVars.scoreboards.get(uuid).show();
        }
        /*}*/
    }

    @EventHandler
    public void onPortalEnter(PlayerTeleportEvent e) {
        Location min = StartPortal.getStartLoc();
        Location max = StartPortal.getEndLoc();
        UUID uuid = e.getPlayer().getUniqueId();

        if(teleporting.contains(uuid)) {
            Bukkit.getLogger().info("Already in teleporting");
            return;
        }

        if (min == null || max == null)
            return;

        if (e.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL)
            return;

        Location from = e.getFrom();
        from.setY(min.getY());
        from.setX(Math.floor(from.getX()));
        from.setZ(Math.floor(from.getZ()));

        Bukkit.getLogger().info(String.format("%s", from));
        boolean between = Tools.isBetween(min, from, max, 1);
        if (!between)
            return;

        if(!teleporting.contains(uuid)) {
            teleporting.add(uuid);
            Bukkit.getLogger().info("Added to teleporting");
        }

        e.setCancelled(true);
        FileConfiguration config = Main.plugin.getConfig();
        String spawnStr = config.getString("tp-spawn");
        if (spawnStr != null) {
            Location spawn = LocTools.strToLoc(spawnStr);
            if (spawn != null)
                e.getPlayer().teleport(spawn);
        }

        teleporting.remove(uuid);
        e.getPlayer().performCommand("mb start");
    }
}