/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.command;

import net.rainy_juzixiao.justforbuilding.build.BlockOperations;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.preview.circle.CirclePreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.cube.CubePreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.line.LinePreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.rect.RectPreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.sphere.SpherePreviewSync;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.rainy_juzixiao.justforbuilding.preview.tree.TreePreviewSync;

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

    public static Component directionComponent(BuildDirection direction) {
        return translate(direction != null
                ? "jfb.direction." + direction.name().toLowerCase(Locale.ROOT)
                : "jfb.direction.facing");
    }

    public static Component stateModeComponent(boolean keep) {
        return translate(keep ? "jfb.state.keep" : "jfb.state.once");
    }

    public static Component rectTypeComponent(boolean hollow) {
        return translate(hollow ? "jfb.rect.type.hollow" : "jfb.rect.type.solid");
    }

    public static Component cubeTypeComponent(boolean frameOnly, boolean hollow) {
        if (frameOnly) {
            return translate("command.jfb.cube.type.frame");
        } else if (hollow) {
            return translate("command.jfb.cube.type.hollow");
        } else {
            return translate("command.jfb.cube.type.solid");
        }
    }

    public static Component anchorComponent(RectAnchor anchor) {
        return translate("jfb.rect.anchor." + anchor.name().toLowerCase(Locale.ROOT));
    }

    public static Component modeComponent(BuildMode mode) {
        return translate("jfb.mode." + mode.name());
    }

    public static void resetState(BuildState state) {
        state.setContext(null);
        state.setKeep(false);
    }

    public static void pushPreviewSnapshots(ServerPlayer player, BuildState state) {
        LinePreviewSync.pushSnapshot(player, state);
        RectPreviewSync.pushSnapshot(player, state);
        CubePreviewSync.pushSnapshot(player, state);
        CirclePreviewSync.pushSnapshot(player, state);
        SpherePreviewSync.pushSnapshot(player, state);
        TreePreviewSync.pushSnapshot(player, state);
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
            BlockOperations.executeRedo(source.getServer(), operation);
        } else {
            BlockOperations.executeUndo(source.getServer(), operation);
        }
        sendMessage(source, translate(redo
                ? "command.jfb.redo.success"
                : "command.jfb.undo.success"));
        return 1;
    }
}
