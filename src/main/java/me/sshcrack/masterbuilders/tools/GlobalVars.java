package me.sshcrack.masterbuilders.tools;

import com.jonahseguin.absorb.scoreboard.Absorb;
import me.sshcrack.masterbuilders.tools.timer.TeamTimer;

import java.util.*;

public class GlobalVars {
    public static Map<UUID, List<UUID>> invitations = new HashMap<>();
    public static Map<String, ArrayList<UUID>> inRegions = new HashMap<>();
    public static Map<String, TeamTimer> timers = new HashMap<>();
    public static Map<UUID, Absorb> scoreboards = new HashMap<>();
}
