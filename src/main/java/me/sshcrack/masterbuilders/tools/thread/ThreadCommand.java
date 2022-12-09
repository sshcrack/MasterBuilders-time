package me.sshcrack.masterbuilders.tools.thread;

import me.sshcrack.masterbuilders.CommandResponse;
import org.bukkit.command.CommandSender;

public abstract class ThreadCommand implements Runnable {
    private CommandSender sender;
    private String label;
    private String[] args;
    private Runnable doneCallback;

    public ThreadCommand(CommandSender sender, String label, String[] args) {
        this.sender = sender;
        this.label = label;
        this.args = args;
    }

    @Override
    public void run() {
        try {
            CommandResponse resp = onCommand(sender, label, args);

            if(resp != null)
                resp.sendResponseMessage(sender);
        } finally {
            if(this.doneCallback != null)
                this.doneCallback.run();
        }
    }

    public void doneCallback(Runnable func) {
        this.doneCallback = func;
    }

    abstract public CommandResponse onCommand(CommandSender sender, String label, String[] args);
}
