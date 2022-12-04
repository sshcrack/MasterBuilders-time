package me.sshcrack.masterbuilders.commands.mb;

import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.GlobalVars;
import me.sshcrack.masterbuilders.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AcceptCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;

        Scoreboard sc = Tools.getMainScoreboard();
        if(sc.getEntityTeam(player) != null)
            return new CommandResponse("accept.already_in_team");

        String joinPlayer = args[0];
        Player matchingPlayers = Bukkit.getPlayer(joinPlayer);
        if(matchingPlayers == null)
            return new CommandResponse("accept.not_found");

        List<UUID> invitedPlayers = GlobalVars.invitations.get(matchingPlayers.getUniqueId());
        if(invitedPlayers == null)
            return new CommandResponse("accept.not_invited");

        if(!invitedPlayers.contains(player.getUniqueId()))
            return new CommandResponse("accept.not_invited");


        Team team = sc.getEntityTeam(matchingPlayers);
        if(team == null) {
            player.sendMessage("Could not join team, team no longer exists");
            return null;
        }

        team.addPlayer(player);
        MessageManager.sendMessageF(matchingPlayers, "accept.passive_join", player.getName());
        return new CommandResponse("accept.joined");
    }

    @Override
    public String getCommand() {
        return "accept";
    }

    @Override
    public String getNode() {
        return "standard";
    }

    @Override
    public String getHelp() {
        return "Joins the team of the given player";
    }

    @Override
    public String getArguments() {
        return "<Player>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
        if(args.length == 1) {
            if(Bukkit.getOnlinePlayers().size() == 0)
                    options.add("<PlayerName>");
            options.addAll(Bukkit
                    .getOnlinePlayers()
                    .stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList())
            );
        }
    }

    @Override
    public int getMaximumArguments() {
        return 1;
    }

}
