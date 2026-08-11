package net.justforbuilding.command.system;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.justforbuilding.build.BuildMode;
import net.justforbuilding.build.BuildState;
import net.justforbuilding.command.CommandUtil;
import net.justforbuilding.command.JfbCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class StatusCommand implements JfbCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("status").executes(ctx -> execute(ctx.getSource()));
    }

    private int execute(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        BuildState state = CommandUtil.getState(player);
        Component enabled = CommandUtil.translate(state.isBuilding()
                ? "command.jfb.status.on"
                : "command.jfb.status.off");
        Component mode = CommandUtil.modeComponent(state.getMode());
        if (state.getMode() == BuildMode.PLACE || state.getMode() == BuildMode.PLACE_Y) {
            CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.status.place",
                    enabled, mode,
                    state.getLength(), state.getInterval(),
                    CommandUtil.directionComponent(state.getDirection()),
                    CommandUtil.stateModeComponent(state),
                    state.getUndoSize()));
        } else {
            CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.status",
                    enabled, mode, state.getUndoSize()));
        }
        return 1;
    }
}
