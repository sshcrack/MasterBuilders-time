package me.sshcrack.masterbuilders.commands.mb;

import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.GlobalVars;
import me.sshcrack.masterbuilders.tools.LocTools;
import me.sshcrack.masterbuilders.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StartPortalCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;
        if(!player.isOp())
            return new CommandResponse("noPerm");

        FileConfiguration config = Main.plugin.getConfig();

        World world = player.getWorld();
        Location start = new Location(world, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        Location end = new Location(world, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));

        config.set("portal-start", LocTools.locToStr(start));
        config.set("portal-end", LocTools.locToStr(end));


        player.sendMessage("Portal set.");
        return null;
    }

    @Override
    public String getCommand() {
        return "startportal";
    }

    @Override
    public String getNode() {
        return "manage";
    }

    @Override
    public String getHelp() {
        return "Set Start Portal";
    }

    @Override
    public String getArguments() {
        return "<xStart> <yStart> <zStart> <xEnd> <yEnd> <zEnd>";
    }

    @Override
    public int getMinimumArguments() {
        return 6;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player))
            return;
        Player player = (Player) sender;
        Block block = Tools.getTargetBlock(player, 10);
        String x = String.valueOf(block.getX());
        String y = String.valueOf(block.getY());
        String z = String.valueOf(block.getZ());

        int l = args.length;

        if(l == 1 || l == 4) {
            options.add(String.format("%s %s %s", x, y, z));
        }

        if(l == 2 || l == 5) {
            options.add(String.format("%s %s", y, z));
        }

        if(l == 3 || l == 6) {
            options.add(z);
        }
    }

    @Override
    public int getMaximumArguments() {
        return 6;
    }

}
