package me.sshcrack.masterbuilders.tools.timer;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.domains.Domain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.Tools;
import me.sshcrack.masterbuilders.tools.WG;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import java.time.Duration;
import java.util.ArrayList;

public class TeamTimer {
    private int timeLeft;
    private final String path;
    private BukkitTask task;
    private final ArrayList<Runnable> onTimeEnd = new ArrayList<>();

    public TeamTimer(String teamName) {
        teamName = teamName.toLowerCase();
        this.path = String.format("time.%s", teamName);
        FileConfiguration config = Main.plugin.getConfig();

        Team team = Tools.getMainScoreboard().getTeam(teamName);
        assert team != null;
        int teamSize = Math.min(team.getPlayers().size() -1, 0);

        int startTime = config.getIntegerList("build_time").get(teamSize);
        this.timeLeft = config.getInt(this.path, startTime);

        String finalTeamName = teamName;
        this.addTimeEndEvent(() -> {
            ProtectedRegion r = WG.getOverworldRegion(finalTeamName + "-inner");
            if(r == null)
                return;

            DefaultDomain domain = r.getMembers();
            domain.removeAll();

            Team innerTeam = Tools.getMainScoreboard().getTeam(finalTeamName);
            if(innerTeam != null) {
                for (OfflinePlayer offlinePlayer : innerTeam.getPlayers()) {
                    if(!offlinePlayer.isOnline())
                        continue;

                    Player p = offlinePlayer.getPlayer();
                    if(p == null)
                        continue;

                    TextComponent title = Component.text(MessageManager.getMessage("time_up.title"));
                    p.showTitle(Title.title(title, Component.text("")));
                    p.playSound(p.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_7, 1,0);
                }
            }
            r.setMembers(domain);
        });
    }

    public int getTimeLeft() {
        if(this.timeLeft <= 0)
            return 0;

        return this.timeLeft;
    }

    public boolean isRunning() {
        return this.task != null;
    }

    public void addTimeEndEvent(Runnable r) {
        this.onTimeEnd.add(r);
    }

    public void sendEndCallback() {
        for (Runnable runnable : this.onTimeEnd) {
            runnable.run();
        }
    }

    public void start() {
        if(this.task != null || this.timeLeft <= 0)
                return;

        this.task = Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> {
            if(this.timeLeft <= 0) {
                this.sendEndCallback();
                this.stop();
            }

            this.timeLeft -= 1;
        }, 0,20L);
    }

    public void stop() {
        if(this.task == null)
                return;

        FileConfiguration config = Main.plugin.getConfig();
        config.set(this.path, this.timeLeft);
        this.task.cancel();
        this.task = null;
    }
}
