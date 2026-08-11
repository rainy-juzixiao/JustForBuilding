package net.justforbuilding.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public interface JfbCommand {
    LiteralArgumentBuilder<CommandSourceStack> register();
}
