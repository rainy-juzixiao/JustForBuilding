package net.rainy_juzixiao.justforbuilding.command.user;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.rainy_juzixiao.justforbuilding.command.JfbCommand;
import net.rainy_juzixiao.justforbuilding.command.context.CubeContext;
import net.rainy_juzixiao.justforbuilding.preview.cube.CubePreviewSync;
import net.rainy_juzixiao.justforbuilding.preview.line.LinePreviewSync;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class CubeCommand implements JfbCommand {

    private static final int MAX_SIZE = 1024;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("cube")
                .then(Commands.argument("length", IntegerArgumentType.integer(1, MAX_SIZE))
                        .then(Commands.argument("width", IntegerArgumentType.integer(1, MAX_SIZE))
                                .then(Commands.argument("height", IntegerArgumentType.integer(1, MAX_SIZE))
                                        .executes(ctx -> execute(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "length"),
                                                IntegerArgumentType.getInteger(ctx, "width"),
                                                IntegerArgumentType.getInteger(ctx, "height"),
                                                false, false))
                                        .then(Commands.literal("hollow")
                                                .executes(ctx -> execute(ctx.getSource(),
                                                        IntegerArgumentType.getInteger(ctx, "length"),
                                                        IntegerArgumentType.getInteger(ctx, "width"),
                                                        IntegerArgumentType.getInteger(ctx, "height"),
                                                        false, true)))
                                        .then(Commands.literal("frame")
                                                .executes(ctx -> execute(ctx.getSource(),
                                                        IntegerArgumentType.getInteger(ctx, "length"),
                                                        IntegerArgumentType.getInteger(ctx, "width"),
                                                        IntegerArgumentType.getInteger(ctx, "height"),
                                                        true, false)))
                                        .then(Commands.literal("solid")
                                                .executes(ctx -> execute(ctx.getSource(),
                                                        IntegerArgumentType.getInteger(ctx, "length"),
                                                        IntegerArgumentType.getInteger(ctx, "width"),
                                                        IntegerArgumentType.getInteger(ctx, "height"),
                                                        false, false))))));
    }

    private int execute(CommandSourceStack source, int length, int width, int height,
                        boolean frameOnly, boolean hollow) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        BuildState state = CommandUtil.getState(player);
        if (!state.isBuilding()) {
            CommandUtil.sendError(source, CommandUtil.translate("command.jfb.error.not_active"));
            return 0;
        }
        state.setContext(new CubeContext(length, width, height, frameOnly, hollow));
        CubePreviewSync.pushSnapshot(player, state);
        LinePreviewSync.pushSnapshot(player, state);
        CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.cube.success",
                length, width, height,
                CommandUtil.cubeTypeComponent(frameOnly, hollow),
                CommandUtil.stateModeComponent(state.isKeep())));
        return 1;
    }
}