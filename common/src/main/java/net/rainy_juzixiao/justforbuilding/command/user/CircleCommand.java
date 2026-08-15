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
import net.rainy_juzixiao.justforbuilding.command.context.CircleContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class CircleCommand implements JfbCommand {

    private static final int MAX_SIZE = 256;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("circle")
                .then(Commands.literal("radius")
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, MAX_SIZE))
                                .executes(ctx -> execute(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "radius"), false, false))
                                .then(Commands.literal("hollow")
                                        .executes(ctx -> execute(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "radius"), false, true)))
                                .then(Commands.literal("solid")
                                        .executes(ctx -> execute(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "radius"), false, false)))))
                .then(Commands.literal("diameter")
                        .then(Commands.argument("diameter", IntegerArgumentType.integer(2, MAX_SIZE * 2))
                                .executes(ctx -> execute(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "diameter"), true, false))
                                .then(Commands.literal("hollow")
                                        .executes(ctx -> execute(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "diameter"), true, true)))
                                .then(Commands.literal("solid")
                                        .executes(ctx -> execute(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "diameter"), true, false)))));
    }

    private int execute(CommandSourceStack source, int size, boolean useDiameter, boolean hollow)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        BuildState state = CommandUtil.getState(player);
        if (!state.isBuilding()) {
            CommandUtil.sendError(source, CommandUtil.translate("command.jfb.error.not_active"));
            return 0;
        }
        state.setContext(new CircleContext(size, useDiameter, hollow));
        CommandUtil.pushPreviewSnapshots(player, state);
        CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.circle.success",
                size, useDiameter ? "diameter" : "radius",
                hollow ? "hollow" : "solid",
                CommandUtil.stateModeComponent(state.isKeep())));
        return 1;
    }
}