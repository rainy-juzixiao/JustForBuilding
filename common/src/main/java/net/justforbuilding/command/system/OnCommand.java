package net.justforbuilding.command.system;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.justforbuilding.command.CommandUtil;
import net.justforbuilding.command.JfbCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class OnCommand implements JfbCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("on").executes(ctx -> execute(ctx.getSource()));
    }

    private int execute(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CommandUtil.getState(player).setBuilding(true);
        CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.on.success"));
        return 1;
    }
}
