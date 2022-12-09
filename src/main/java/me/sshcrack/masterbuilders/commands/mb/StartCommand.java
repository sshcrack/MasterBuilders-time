package me.sshcrack.masterbuilders.commands.mb;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.handler.GameModeFlag;
import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.message.MessageManager;
import me.sshcrack.masterbuilders.tools.*;
import me.sshcrack.masterbuilders.tools.timer.TeamTimer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.*;

public class StartCommand extends SubCommand {
    ArrayList<CommandSender> executioners = new ArrayList<>();

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if(this.executioners.contains(sender))
            return new CommandResponse("general.already_executing");

        StartCommandThread instance = new StartCommandThread(sender, label, args);
        instance.doneCallback(() -> executioners.remove(sender));
        Thread thread = new Thread(instance);

        executioners.add(sender);
        thread.start();

        return new CommandResponse("general.running");
    }

    @Override
    public String getCommand() {
        return "start";
    }

    @Override
    public String getNode() {
        return "standard";
    }

    @Override
    public String getHelp() {
        return "Start a new master builders game";
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
