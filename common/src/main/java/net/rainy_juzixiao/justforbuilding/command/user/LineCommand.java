/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.command.user;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.Command;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.rainy_juzixiao.justforbuilding.command.JfbCommand;
import net.rainy_juzixiao.justforbuilding.command.context.LineContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

@Command
public class LineCommand implements JfbCommand {
    private static final String[] DIRECTIONS = {
            "north",
            "northeast",
            "east",
            "southeast",
            "south",
            "southwest",
            "west",
            "northwest",
            "up",
            "down",
    };

    private static final SuggestionProvider<CommandSourceStack> DIRECTIONS_SUGGESTIONS =
            (context, builder) -> {
                for (String name : DIRECTIONS) {
                    builder.suggest(name);
                }
                return builder.buildFuture();
            };


    private static final int MAX_LENGTH = 1024;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("line")
                .then(Commands.argument("length", IntegerArgumentType.integer(1, MAX_LENGTH))
                        .executes(ctx -> execute(ctx.getSource(),
                                IntegerArgumentType.getInteger(ctx, "length"), 0, null))
                        .then(Commands.argument("interval", IntegerArgumentType.integer(0, 64))
                                .executes(ctx -> execute(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "length"),
                                        IntegerArgumentType.getInteger(ctx, "interval"), null))
                                .then(Commands.argument("direction", StringArgumentType.word())
                                        .suggests(DIRECTIONS_SUGGESTIONS)
                                        .executes(ctx -> execute(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "length"),
                                                IntegerArgumentType.getInteger(ctx, "interval"),
                                                StringArgumentType.getString(ctx, "direction"))))));
    }

    private int execute(CommandSourceStack source, int length, int interval, String directionName)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        BuildState state = CommandUtil.getState(player);
        if (!state.isBuilding()) {
            CommandUtil.sendError(source, CommandUtil.translate("command.jfb.error.not_active"));
            return 0;
        }
        BuildDirection direction = directionName == null ? null : CommandUtil.parseDirection(directionName);
        if (directionName != null && direction == null) {
            CommandUtil.sendError(source, CommandUtil.translate("command.jfb.error.invalid_direction", directionName));
            return 0;
        }
        state.setContext(new LineContext(length, interval, direction));
        CommandUtil.pushPreviewSnapshots(player, state);
        CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.place.success",
                length, interval, CommandUtil.directionComponent(direction),
                CommandUtil.stateModeComponent(state.isKeep())));
        return 1;
    }
}