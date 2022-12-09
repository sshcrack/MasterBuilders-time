package me.sshcrack.masterbuilders.commands.mb;

import me.sshcrack.masterbuilders.CommandResponse;
import me.sshcrack.masterbuilders.commands.SubCommand;
import me.sshcrack.masterbuilders.tools.thread.StartCommandThread;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class StartCommand extends SubCommand {
    ArrayList<CommandSender> executioners = new ArrayList<>();

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if(this.executioners.contains(sender))
            return new CommandResponse("general.already_executing");

        StartCommandThread instance = new StartCommandThread(sender, label, args);
        executioners.add(sender);
        instance.doneCallback(() -> {
            Bukkit.getLogger().info(String.format("Done callback for start %s", sender.getName()));
            executioners.remove(sender);
        });
        Thread thread = new Thread(instance);

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
