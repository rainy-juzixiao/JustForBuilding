package net.justforbuilding.command;

import net.justforbuilding.build.BuildDirection;
import net.justforbuilding.build.BuildExecutor;
import net.justforbuilding.build.BuildMode;
import net.justforbuilding.build.BuildState;
import net.justforbuilding.build.operation.BuildOperation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CommandUtil {

    private static final Map<UUID, BuildState> STATES = new HashMap<>();

    public static BuildState getState(ServerPlayer player) {
        return STATES.computeIfAbsent(player.getUUID(), uuid -> new BuildState());
    }

    public static void sendMessage(CommandSourceStack source, Component component) {
        source.sendSuccess(component, false);
    }

    public static void sendError(CommandSourceStack source, Component component) {
        source.sendFailure(component);
    }

    public static Component translate(String key, Object... args) {
        return new TranslatableComponent(key, args);
    }

    public static BuildDirection parseDirection(String name) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "up":
                return BuildDirection.UP;
            case "down":
                return BuildDirection.DOWN;
            case "north":
                return BuildDirection.NORTH;
            case "northeast":
                return BuildDirection.NORTHEAST;
            case "east":
                return BuildDirection.EAST;
            case "southeast":
                return BuildDirection.SOUTHEAST;
            case "south":
                return BuildDirection.SOUTH;
            case "southwest":
                return BuildDirection.SOUTHWEST;
            case "west":
                return BuildDirection.WEST;
            case "northwest":
                return BuildDirection.NORTHWEST;
            default:
                return null;
        }
    }

    public static BuildDirection facingDirection(ServerPlayer player) {
        switch (Math.floorMod(Math.round(player.yRot / 45.0F), 8)) {
            case 0:
                return BuildDirection.SOUTH;
            case 1:
                return BuildDirection.SOUTHWEST;
            case 2:
                return BuildDirection.WEST;
            case 3:
                return BuildDirection.NORTHWEST;
            case 4:
                return BuildDirection.NORTH;
            case 5:
                return BuildDirection.NORTHEAST;
            case 6:
                return BuildDirection.EAST;
            default:
                return BuildDirection.SOUTHEAST;
        }
    }

    public static BuildDirection verticalDirection(ServerPlayer player) {
        return player.xRot > 45.0F ? BuildDirection.DOWN : BuildDirection.UP;
    }

    public static Component directionComponent(BuildDirection direction) {
        return translate(direction != null
                ? "jfb.direction." + direction.name().toLowerCase(Locale.ROOT)
                : "jfb.direction.facing");
    }

    public static Component stateModeComponent(BuildState state) {
        return translate(state.isKeep() ? "jfb.state.keep" : "jfb.state.once");
    }

    public static Component modeComponent(BuildMode mode) {
        return translate("jfb.mode." + mode.name());
    }

    public static void resetState(BuildState state) {
        state.setMode(BuildMode.NONE);
        state.setLength(0);
        state.setInterval(0);
        state.setDirection(null);
        state.setKeep(false);
    }

    public static int executeUndoRedo(CommandSourceStack source, BuildState state, boolean redo) {
        BuildOperation operation = redo ? state.redo() : state.undo();
        if (operation == null) {
            sendError(source, translate(redo
                    ? "command.jfb.error.no_redo"
                    : "command.jfb.error.no_undo"));
            return 0;
        }
        if (redo) {
            BuildExecutor.executeRedo(source.getServer(), operation);
        } else {
            BuildExecutor.executeUndo(source.getServer(), operation);
        }
        sendMessage(source, translate(redo
                ? "command.jfb.redo.success"
                : "command.jfb.undo.success"));
        return 1;
    }
}
