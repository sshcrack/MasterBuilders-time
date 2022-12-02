package me.sshcrack.masterbuilders.tools.timer;

import me.sshcrack.masterbuilders.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

public class TeamTimer {
    private int timeLeft;
    private String path;
    private BukkitTask task;

    public TeamTimer(String teamName) {
        this.path = String.format("time.%s", teamName);
        FileConfiguration config = Main.plugin.getConfig();

        int startTime = config.getInt("build_time");
        this.timeLeft = config.getInt(this.path, startTime);
    }

    public int getTimeLeft() {
        return this.timeLeft;
    }

    public boolean isRunning() {
        return this.task != null;
    }

    public void start() {
        if(this.task != null)
                return;

        this.task = Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> {
            this.timeLeft -= 1;
            if(this.timeLeft <= 0)
                this.stop();
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
