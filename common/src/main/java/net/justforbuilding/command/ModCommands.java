package net.justforbuilding.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.shedaniel.architectury.event.events.BlockEvent;
import me.shedaniel.architectury.event.events.CommandRegistrationEvent;
import me.shedaniel.architectury.platform.Platform;
import net.justforbuilding.build.BuildDirection;
import net.justforbuilding.build.BuildExecutor;
import net.justforbuilding.build.BuildMode;
import net.justforbuilding.build.BuildState;
import net.justforbuilding.command.system.*;
import net.justforbuilding.command.user.LineCommand;
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
        if (!buildState.isBuilding() || !isPlaceMode(buildState.getMode()) || buildState.getLength() <= 0) {
            return InteractionResult.PASS;
        }
        BuildDirection direction;
        if (buildState.getMode() == BuildMode.PLACE_Y) {
            direction = CommandUtil.verticalDirection(player);
        } else {
            direction = buildState.getDirection() != null
                    ? buildState.getDirection()
                    : CommandUtil.facingDirection(player);
        }
        int placed = BuildExecutor.placeLine(
                (ServerLevel) level, pos, direction,
                buildState.getLength(), buildState.getInterval(), state,
                buildState);
        CommandUtil.sendMessage(player.createCommandSourceStack(),
                CommandUtil.translate("command.jfb.place.triggered", placed));
        if (!buildState.isKeep()) {
            CommandUtil.resetState(buildState);
        }
        return Platform.isFabric() ? InteractionResult.FAIL : InteractionResult.PASS;
    }

    private static boolean isPlaceMode(BuildMode mode) {
        return mode == BuildMode.PLACE || mode == BuildMode.PLACE_Y;
    }
}
