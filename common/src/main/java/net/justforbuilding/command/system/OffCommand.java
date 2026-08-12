/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.justforbuilding.command.system;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.justforbuilding.command.CommandUtil;
import net.justforbuilding.command.JfbCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class OffCommand implements JfbCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("off").executes(ctx -> execute(ctx.getSource()));
    }

    private int execute(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CommandUtil.resetState(CommandUtil.getState(player));
        CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.off.success"));
        return 1;
    }
}
