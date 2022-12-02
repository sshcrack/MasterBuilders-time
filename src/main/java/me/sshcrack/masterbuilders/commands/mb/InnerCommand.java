package me.sshcrack.masterbuilders.commands.mb;

import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.tools.LocTools;
import me.sshcrack.masterbuilders.tools.Tools;
import org.antlr.v4.runtime.tree.xpath.XPathLexerErrorListener;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class InnerCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;
        if(!player.isOp())
            return new CommandResponse("noPerm");

        FileConfiguration config = Main.plugin.getConfig();
        String startOuterStr = config.getString("start");
        String endOuterStr = config.getString("end");

        if(startOuterStr == null || endOuterStr == null) {
            player.sendMessage("Region not set.");
            return null;
        }

        Location startOuter = LocTools.strToLoc(startOuterStr);
        Location endOuter = LocTools.strToLoc(endOuterStr);

        assert startOuter != null;
        assert endOuter != null;

        World world = player.getWorld();
        Location start = new Location(world, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        Location end = new Location(world, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));

        start = startOuter.clone().subtract(start);
        end = endOuter.clone().subtract(end);

        config.set("inner-start", LocTools.locToStr(start));
        config.set("inner-end", LocTools.locToStr(end));

        player.sendMessage(String.format("Start %s End %s", start, end));
        player.sendMessage(String.format("Outer %s OuterE %s", startOuter, endOuter));
        player.sendMessage("Inner set..");
        return null;
    }

    @Override
    public String getCommand() {
        return "inner";
    }

    @Override
    public String getNode() {
        return "region";
    }

    @Override
    public String getHelp() {
        return "Set the build region of the plot";
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
