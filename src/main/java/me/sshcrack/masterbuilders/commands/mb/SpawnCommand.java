package me.sshcrack.masterbuilders.commands.mb;

import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.tools.LocTools;
import me.sshcrack.masterbuilders.tools.Tools;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;
        FileConfiguration config = Main.plugin.getConfig();
        String spawnStr = config.getString("tp-spawn");

        if(spawnStr == null)
            return null;

        Location spawn = LocTools.strToLoc(spawnStr);
        assert spawn != null;
        player.teleport(spawn);

        return null;
    }

    @Override
    public String getCommand() {
        return "spawn";
    }

    @Override
    public String getNode() {
        return "standard";
    }

    @Override
    public String getHelp() {
        return "Teleports the player to spawn";
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
