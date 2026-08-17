/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.command.system;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;
import net.rainy_juzixiao.justforbuilding.command.Command;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.rainy_juzixiao.justforbuilding.command.JfbCommand;
import net.rainy_juzixiao.justforbuilding.command.context.CubeContext;
import net.rainy_juzixiao.justforbuilding.command.context.RectContext;
import net.rainy_juzixiao.justforbuilding.preview.rect.RectPreviewSync;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

@Command
public class AnchorCommand implements JfbCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("anchor");
        for (RectAnchor anchor : RectAnchor.values()) {
            command.then(Commands.literal(anchor.name().toLowerCase(Locale.ROOT))
                    .executes(ctx -> execute(ctx.getSource(), anchor)));
        }
        return command;
    }

    private int execute(CommandSourceStack source, RectAnchor anchor) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        BuildState state = CommandUtil.getState(player);
        if (!(state.getContext() instanceof RectContext) || !(state.getContext() instanceof CubeContext)) {
            CommandUtil.sendError(source, CommandUtil.translate("command.jfb.error.no_rect_or_no_cube"));
            return 0;
        }
        RectContext context = (RectContext) state.getContext();
        context.setAnchor(anchor);
        RectPreviewSync.pushSnapshot(player, state);
        CommandUtil.sendMessage(source, CommandUtil.translate("command.jfb.anchor.success",
                CommandUtil.anchorComponent(anchor)));
        return 1;
    }
}
