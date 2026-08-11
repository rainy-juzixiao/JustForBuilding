package net.justforbuilding.command.system;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.justforbuilding.command.CommandUtil;
import net.justforbuilding.command.JfbCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class StateCommand implements JfbCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("state")
                .then(Commands.literal("keep").executes(ctx -> execute(ctx.getSource(), true)))
                .then(Commands.literal("once").executes(ctx -> execute(ctx.getSource(), false)));
    }

    private int execute(CommandSourceStack source, boolean keep) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CommandUtil.getState(player).setKeep(keep);
        CommandUtil.sendMessage(source, CommandUtil.translate(keep
                ? "command.jfb.state.keep"
                : "command.jfb.state.once"));
        return 1;
    }
}
