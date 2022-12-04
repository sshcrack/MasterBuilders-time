package me.sshcrack.masterbuilders.commands.mb;

import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.GlobalVars;
import me.sshcrack.masterbuilders.tools.Tools;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TopicCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Scoreboard sc = Tools.getMainScoreboard();
        Player player = (Player) sender;

        FileConfiguration config = Main.plugin.getConfig();

        Team team = sc.getEntityTeam(player);
        if(team == null)
            return new CommandResponse("topic.team_required");

        boolean alreadySelected = config.getString(String.format("selected.%s", team.getName())) != null;
        if(alreadySelected)
            return new CommandResponse("topic.already_set");

        String selectedStr = args[0];
        List<String> available = config.getStringList("topics");

        if(!NumberUtils.isNumber(selectedStr))
            return new CommandResponse("topic.invalid");

        int selected = Integer.parseInt(selectedStr);
        if(selected > available.size() -1)
            return new CommandResponse("topic.invalid");

        String topic = available.get(selected);
        config.set(String.format("selected.%s", team.getName()), topic);
        for (OfflinePlayer offline : team.getPlayers()) {
            if(offline.isOnline()) {
                Player p = offline.getPlayer();
                MessageManager.sendMessageF(p, "topic.set", topic);
            }
        }

        return null;
    }

    @Override
    public String getCommand() {
        return "topic";
    }

    @Override
    public String getNode() {
        return "standard";
    }

    @Override
    public String getHelp() {
        return "Sets the topic for the team";
    }

    @Override
    public String getArguments() {
        return "<Topic>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
    }

    @Override
    public int getMaximumArguments() {
        return 1;
    }

}
