/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.command.system;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.rainy_juzixiao.justforbuilding.command.JfbCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class StatusCommand implements JfbCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("status").executes(ctx -> execute(ctx.getSource()));
    }

    private int execute(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        BuildState state = CommandUtil.getState(player);
        Component enabled = CommandUtil.translate(state.isBuilding()
                ? "command.jfb.status.on"
                : "command.jfb.status.off");
        Component mode = CommandUtil.modeComponent(state.getMode());
        if (state.getMode() == BuildMode.PLACE || state.getMode() == BuildMode.PLACE_Y) {
            CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.status.place",
                    enabled, mode,
                    state.getLength(), state.getInterval(),
                    CommandUtil.directionComponent(state.getDirection()),
                    CommandUtil.stateModeComponent(state),
                    state.getUndoSize()));
        } else {
            CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.status",
                    enabled, mode, state.getUndoSize()));
        }
        return 1;
    }
}
