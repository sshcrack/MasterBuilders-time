package me.sshcrack.masterbuilders.commands.mb;

import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.Tools;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Locale;

public class CreateCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;
        Scoreboard sc = Tools.getMainScoreboard();
        Team playerTeam = sc.getEntityTeam(player);
        if(playerTeam != null)
            return new CommandResponse("create.team_leave");

        String name = args[0].toLowerCase();
        if(sc.getTeam(name) != null)
            return new CommandResponse("create.exists");

        Team team = sc.registerNewTeam(name);
        team.addPlayer(player);

        MessageManager.sendMessageF(player, "create.created", name);
        return null;
    }

    @Override
    public String getCommand() {
        return "create";
    }

    @Override
    public String getNode() {
        return "create";
    }

    @Override
    public String getHelp() {
        return "Create a team with given name";
    }

    @Override
    public String getArguments() {
        return "<Team Name>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
        if(args.length == 1) {
            options.add("<Name>");
        }
    }

    @Override
    public int getMaximumArguments() {
        return 1;
    }

}
