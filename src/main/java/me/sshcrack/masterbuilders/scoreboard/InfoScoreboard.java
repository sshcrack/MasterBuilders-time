package me.sshcrack.masterbuilders.scoreboard;
import com.google.common.base.Joiner;
import com.jonahseguin.absorb.view.EntryBuilder;
import com.jonahseguin.absorb.view.ViewContext;
import com.jonahseguin.absorb.view.ViewProvider;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.timer.TeamTimer;
import me.sshcrack.masterbuilders.tools.timer.TimeFormatter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class InfoScoreboard implements ViewProvider {
    private Team team;
    private TeamTimer timer;
    private double titleAnim = 0;
    public InfoScoreboard(Team team, TeamTimer timer) {
        this.team = team;
        this.timer = timer;
    }

    @Override
    public String getTitle(ViewContext viewContext) {
        String title = "Master Builders";
        titleAnim += .25;

        String[] split = title.split("");
        ArrayList<String> splitTwo = new ArrayList<>();

        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            temp.append(split[i]);
            if(i % 3 == 2) {
                splitTwo.add(temp.toString());
                temp = new StringBuilder();
            }
        }

        if(temp.length() != 0)
            splitTwo.add(temp.toString());

        int c = (int) (Math.floor(titleAnim) % splitTwo.size());
        splitTwo.set(c, ChatColor.BLUE + splitTwo.get(c) + ChatColor.GOLD);

        return ChatColor.GOLD + Joiner.on("").join(splitTwo);
    }

    @Override
    public List<String> getLines(ViewContext viewContext) {
        long onlinePlayers = this.team.getPlayers().stream().filter(OfflinePlayer::isOnline).count();
        long total = this.team.getPlayers().size();
        String topic = Main.plugin.getConfig().getString(String.format("selected.%s", this.team.getName()));

        EntryBuilder entryBuilder = new EntryBuilder();
        entryBuilder.blank();
        entryBuilder.next(ChatColor.GRAY + "Team: " + ChatColor.GOLD + this.team.getName());
        entryBuilder.next(ChatColor.GRAY + "Members: " + ChatColor.GREEN + onlinePlayers + ChatColor.GRAY + "/" + ChatColor.RED + total + ChatColor.GRAY);
        entryBuilder.next(ChatColor.GRAY + MessageManager.getMessage("scoreboard.topic") + ChatColor.GOLD + topic);
        entryBuilder.next(ChatColor.GRAY + MessageManager.getMessage("scoreboard.time_left") +ChatColor.GOLD +  TimeFormatter.formatTime(this.timer.getTimeLeft() * 1000L));
        entryBuilder.blank();
        entryBuilder.next(ChatColor.GOLD + "Powered by " + ChatColor.AQUA + "Clipture");
        return entryBuilder.build();
    }

    @Override
    public void onUpdate(ViewContext context) {
        // Do something after it updates if you want
    }
}
