/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.command.user;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.rainy_juzixiao.justforbuilding.command.JfbCommand;
import net.rainy_juzixiao.justforbuilding.command.context.RectContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class RectCommand implements JfbCommand {

    private static final int MAX_LENGTH = 1024;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("rect")
                .then(Commands.argument("length", IntegerArgumentType.integer(1, MAX_LENGTH))
                        .then(Commands.argument("width", IntegerArgumentType.integer(1, MAX_LENGTH))
                                .executes(ctx -> execute(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "length"),
                                        IntegerArgumentType.getInteger(ctx, "width"), false))
                                .then(Commands.literal("hollow")
                                        .executes(ctx -> execute(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "length"),
                                                IntegerArgumentType.getInteger(ctx, "width"), true)))
                                .then(Commands.literal("solid")
                                        .executes(ctx -> execute(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "length"),
                                                IntegerArgumentType.getInteger(ctx, "width"), false)))));
    }

    private int execute(CommandSourceStack source, int length, int width, boolean hollow)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        BuildState state = CommandUtil.getState(player);
        if (!state.isBuilding()) {
            CommandUtil.sendError(source, CommandUtil.translate("command.jfb.error.not_active"));
            return 0;
        }
        state.setContext(new RectContext(length, width, hollow));
        CommandUtil.pushPreviewSnapshots(player, state);
        CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.rect.success",
                length, width, CommandUtil.rectTypeComponent(hollow),
                CommandUtil.stateModeComponent(state.isKeep())));
        return 1;
    }
}
