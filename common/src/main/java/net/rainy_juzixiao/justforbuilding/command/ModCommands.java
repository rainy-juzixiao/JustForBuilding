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
import me.shedaniel.architectury.utils.IntValue;
import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.system.*;
import net.rainy_juzixiao.justforbuilding.command.user.*;
import net.rainy_juzixiao.justforbuilding.item.NBSStaffItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ModCommands {
    private static final JfbCommand[] COMMANDS = {
            new OnCommand(),
            new OffCommand(),
            new LineCommand(),
            new RectCommand(),
            new AnchorCommand(),
            new CubeCommand(),
            new CircleCommand(),
            new SphereCommand(),
            new StateCommand(),
            new StatusCommand(),
            new UndoCommand(),
            new RedoCommand(),
            new InvertCommand(),
            new TreeCommand()
    };

    public static void register() {
        CommandRegistrationEvent.EVENT.register(ModCommands::registerCommands);
        BlockEvent.PLACE.register(ModCommands::onBlockPlace);
        BlockEvent.BREAK.register(ModCommands::onBlockBreak);
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

    private static boolean isInterceptableMode(BuildContext context) {
        if (context == null) return false;
        BuildMode mode = context.mode();
        return mode == BuildMode.PLACE
                || mode == BuildMode.RECT
                || mode == BuildMode.CUBE
                || mode == BuildMode.CIRCLE
                || mode == BuildMode.SPHERE;
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
            CommandUtil.pushPreviewSnapshots(player, buildState);
        }
        return Platform.isFabric() ? InteractionResult.FAIL : InteractionResult.PASS;
    }

    private static InteractionResult onBlockBreak(Level level, BlockPos blockPos, BlockState blockState, ServerPlayer serverPlayer, IntValue intValue) {
        if (level.isClientSide) {
            return InteractionResult.PASS;
        }
        if (serverPlayer.getMainHandItem().getItem() instanceof NBSStaffItem) {
            // 破坏回弹（取消破坏），基点设为被破坏的方块本身（无偏移），并进入删除模式
            BuildState state = CommandUtil.getState(serverPlayer);
            state.setBasePos(blockPos);
            state.setDestroy(true);
            serverPlayer.displayClientMessage(CommandUtil.translate("command.jfb.invert.destroy"), true);
            CommandUtil.pushPreviewSnapshots(serverPlayer, state);
            return InteractionResult.FAIL;
        }
        BuildState buildState = CommandUtil.getState(serverPlayer);
        BuildContext context = buildState.getContext();
        if (!buildState.isBuilding() || !buildState.isDestroy() || !isInterceptableMode(context)) {
            return InteractionResult.PASS;
        }
        int destroyed = context.executePlace((ServerLevel) level, blockPos, Blocks.AIR.defaultBlockState(), serverPlayer, buildState);
        CommandUtil.sendMessage(serverPlayer.createCommandSourceStack(),
                CommandUtil.translate("command.jfb.destroy.triggered", destroyed));
        if (!buildState.isKeep()) {
            CommandUtil.resetState(buildState);
            CommandUtil.pushPreviewSnapshots(serverPlayer, buildState);
        }
        return Platform.isFabric() ? InteractionResult.FAIL : InteractionResult.PASS;
    }
}