package net.justforbuilding;

import net.justforbuilding.command.ModCommands;

public class justforbuilding {
    public static final String MOD_ID = "justforbuilding";

    public static void init() {
        ModCommands.register();
    }
}
