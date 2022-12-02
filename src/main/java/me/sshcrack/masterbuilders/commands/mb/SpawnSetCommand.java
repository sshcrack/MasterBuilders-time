package me.sshcrack.masterbuilders.commands.mb;

import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.tools.LocTools;
import me.sshcrack.masterbuilders.tools.Tools;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnSetCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;
        if(!player.isOp())
            return new CommandResponse("noPerm");

        FileConfiguration config = Main.plugin.getConfig();

        Location spwanSet = player.getLocation();
        config.set("tp-spawn", LocTools.locToStr(spwanSet));


        player.sendMessage("Spawn set.");
        return null;
    }

    @Override
    public String getCommand() {
        return "spawnset";
    }

    @Override
    public String getNode() {
        return "manage";
    }

    @Override
    public String getHelp() {
        return "Set the location of spawn";
    }

    @Override
    public String getArguments() {
        return "";
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
