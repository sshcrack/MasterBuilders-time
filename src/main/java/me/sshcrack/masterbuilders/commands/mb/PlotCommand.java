package me.sshcrack.masterbuilders.commands.mb;

import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.tools.LocTools;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class PlotCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;
        if(!player.isOp())
            return new CommandResponse("noPerm");

        FileConfiguration config = Main.plugin.getConfig();
        Location loc = player.getLocation();

        config.set("plot", LocTools.locToStr(loc));
        player.sendMessage("Region set.");
        return null;
    }

    @Override
    public String getCommand() {
        return "plot";
    }

    @Override
    public String getNode() {
        return "manage";
    }

    @Override
    public String getHelp() {
        return "Set where plots should spawn";
    }

    @Override
    public String getArguments() {
        return "<xStart> <yStart> <zStart>";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
    }

    @Override
    public int getMaximumArguments() {
        return 0;
    }

}
