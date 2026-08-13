/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.shedaniel.architectury.event.events.BlockEvent;
import me.shedaniel.architectury.event.events.CommandRegistrationEvent;
import me.shedaniel.architectury.platform.Platform;
import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.system.*;
import net.rainy_juzixiao.justforbuilding.command.user.LineCommand;
import net.rainy_juzixiao.justforbuilding.command.user.RectCommand;
import net.rainy_juzixiao.justforbuilding.preview.line.LinePreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.rect.RectPreviewSync;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ModCommands {
    private static final JfbCommand[] COMMANDS = {
            new OnCommand(),
            new OffCommand(),
            new LineCommand(),
            new RectCommand(),
            new AnchorCommand(),
            new StateCommand(),
            new StatusCommand(),
            new UndoCommand(),
            new RedoCommand()
    };

    public static void register() {
        CommandRegistrationEvent.EVENT.register(ModCommands::registerCommands);
        BlockEvent.PLACE.register(ModCommands::onBlockPlace);
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher,
                                         Commands.CommandSelection selection) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("jfb");
        for (JfbCommand command : COMMANDS) {
            root.then(command.register());
        }
        dispatcher.register(root);
        dispatcher.register(Commands.literal("justforbuilding").redirect(root.build()));
    }

    private static InteractionResult onBlockPlace(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer)) {
            return InteractionResult.PASS;
        }
        ServerPlayer player = (ServerPlayer) entity;
        BuildState buildState = CommandUtil.getState(player);
        BuildContext context = buildState.getContext();
        if (!buildState.isBuilding() || context == null) {
            return InteractionResult.PASS;
        }
        int placed = context.executePlace((ServerLevel) level, pos, state, player, buildState);
        CommandUtil.sendMessage(player.createCommandSourceStack(),
                CommandUtil.translate("command.jfb.place.triggered", placed));
        if (!buildState.isKeep()) {
            CommandUtil.resetState(buildState);
            RectPreviewSync.pushSnapshot(player, buildState);
            LinePreviewSync.pushSnapshot(player, buildState);
        }
        return Platform.isFabric() ? InteractionResult.FAIL : InteractionResult.PASS;
    }
}
