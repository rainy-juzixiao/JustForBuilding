package net.justforbuilding.command.user;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.justforbuilding.build.BuildMode;
import net.justforbuilding.build.BuildState;
import net.justforbuilding.command.CommandUtil;
import net.justforbuilding.command.JfbCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class PlaceyCommand implements JfbCommand {

    private static final int MAX_LENGTH = 1024;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("placey")
                .then(Commands.argument("length", IntegerArgumentType.integer(1, MAX_LENGTH))
                        .executes(ctx -> execute(ctx.getSource(),
                                IntegerArgumentType.getInteger(ctx, "length"), 0))
                        .then(Commands.argument("interval", IntegerArgumentType.integer(0, 64))
                                .executes(ctx -> execute(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "length"),
                                        IntegerArgumentType.getInteger(ctx, "interval")))));
    }

    private int execute(CommandSourceStack source, int length, int interval) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        BuildState state = CommandUtil.getState(player);
        if (!state.isBuilding()) {
            CommandUtil.sendError(source, CommandUtil.translate("command.jfb.error.not_active"));
            return 0;
        }
        state.setMode(BuildMode.PLACE_Y);
        state.setLength(length);
        state.setInterval(interval);
        state.setDirection(null);
        CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.placey.success",
                length, interval, CommandUtil.stateModeComponent(state)));
        return 1;
    }
}
