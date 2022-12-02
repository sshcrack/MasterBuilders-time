package me.sshcrack.masterbuilders.commands.mb;

import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.GlobalVars;
import me.sshcrack.masterbuilders.tools.Tools;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InviteCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;
        String toMatch = args[0];
        List<Player> matchingPlayers = Bukkit
                .getOnlinePlayers()
                .stream()
                .filter(e -> e.getName().equals(toMatch))
                .collect(Collectors.toList());

        if (matchingPlayers.size() == 0)
            return new CommandResponse("invite.not_matching");

        boolean inTeam = Tools.getMainScoreboard().getEntityTeam(player) != null;
        if (!inTeam)
            return new CommandResponse("invite.team_required");

        Player matchedPlayer = matchingPlayers.get(0);
        if (matchedPlayer == player)
            return new CommandResponse("invite.invite_self");

        MessageManager.sendMessageF(player, "invite.invited", toMatch);
        String msg = MessageManager.getMessageF("invite.invite_message", player.getName());

        String command = String.format("/mb accept %s", player.getName());
        TextComponent message = new TextComponent(msg);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join").color(ChatColor.GOLD.asBungee()).create()));
        matchedPlayer.sendMessage(message);

        List<UUID> newList = GlobalVars.invitations.get(player.getUniqueId());
        if (newList == null)
            newList = new ArrayList<>();

        if (!newList.contains(matchedPlayer.getUniqueId()))
            newList.add(matchedPlayer.getUniqueId());

        new ParticleBuilder(ParticleEffect.SCULK_CHARGE_POP,
                matchedPlayer
                        .getLocation()
                        .add(0, 1, 0)
        )
                .setSpeed(0.1f)
                .setAmount(20)
                .display();
        matchedPlayer.playSound(matchedPlayer.getLocation(), Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM, 1, 0);
        GlobalVars.invitations.put(player.getUniqueId(), newList);
        return null;
    }

    @Override
    public String getCommand() {
        return "invite";
    }

    @Override
    public String getNode() {
        return "standard";
    }

    @Override
    public String getHelp() {
        return "Invite a player to your team to build some stuff";
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
        if (args.length == 1) {
            boolean isPlayer = sender instanceof Player;

            if (Bukkit.getOnlinePlayers().size() == 0)
                options.add("<PlayerName>");
            options.addAll(Bukkit
                    .getOnlinePlayers()
                    .stream()
                            .filter(e -> {
                                if(sender instanceof Player) {
                                    Player p = (Player) sender;
                                    if(e.getUniqueId() == p.getUniqueId())
                                        return false;
                                }
                                return true;
                            })
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
