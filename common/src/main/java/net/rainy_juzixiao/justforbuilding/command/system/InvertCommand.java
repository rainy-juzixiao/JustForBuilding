/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.command.system;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.rainy_juzixiao.justforbuilding.command.JfbCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class InvertCommand implements JfbCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("invert")
                .executes(ctx -> executeToggle(ctx.getSource()))
                .then(Commands.literal("destroy").executes(ctx -> executeSet(ctx.getSource(), true)))
                .then(Commands.literal("build").executes(ctx -> executeSet(ctx.getSource(), false)));
    }

    private int executeToggle(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean newVal = !CommandUtil.getState(player).isDestroy();
        CommandUtil.getState(player).setDestroy(newVal);
        CommandUtil.sendMessage(source, CommandUtil.translate(newVal
                ? "command.jfb.invert.destroy"
                : "command.jfb.invert.build"));
        return 1;
    }

    private int executeSet(CommandSourceStack source, boolean destroy) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CommandUtil.getState(player).setDestroy(destroy);
        CommandUtil.sendMessage(source, CommandUtil.translate(destroy
                ? "command.jfb.invert.destroy"
                : "command.jfb.invert.build"));
        return 1;
    }
}