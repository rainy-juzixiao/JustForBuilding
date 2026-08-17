/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.command.user;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.build.executor.TreeExecutor;
import net.rainy_juzixiao.justforbuilding.command.Command;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.rainy_juzixiao.justforbuilding.command.JfbCommand;
import net.rainy_juzixiao.justforbuilding.command.context.TreeContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

@Command
public class TreeCommand implements JfbCommand {
    private static final String[] VANILLA_TREES = {
            "oak", "fancy_oak", "birch", "spruce", "pine", "mega_spruce",
            "jungle_tree", "jungle_tree_no_vine", "mega_jungle_tree",
            "acacia", "dark_oak", "swamp_tree", "jungle_bush"
    };

    private static final SuggestionProvider<CommandSourceStack> VANILLA_TREE_SUGGESTIONS =
            (context, builder) -> {
                for (String name : VANILLA_TREES) {
                    builder.suggest(name);
                }
                return builder.buildFuture();
            };

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("tree")
                .then(Commands.literal("rainforest").executes(new com.mojang.brigadier.Command<CommandSourceStack>() {
                    @Override
                    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
                        return execute(context, TreeExecutor.TreeType.RAINFOREST);
                    }
                }))
                .then(Commands.literal("banyan").executes(new com.mojang.brigadier.Command<CommandSourceStack>() {
                    @Override
                    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
                        return execute(context, TreeExecutor.TreeType.BANYAN);
                    }
                }))
                .then(Commands.literal("forked").executes(new com.mojang.brigadier.Command<CommandSourceStack>() {
                    @Override
                    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
                        return execute(context, TreeExecutor.TreeType.FORKED);
                    }
                }))
                .then(Commands.literal("dwarf").executes(new com.mojang.brigadier.Command<CommandSourceStack>() {
                    @Override
                    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
                        return execute(context, TreeExecutor.TreeType.DWARF);
                    }
                }))
                .then(Commands.literal("medium").executes(new com.mojang.brigadier.Command<CommandSourceStack>() {
                    @Override
                    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
                        return execute(context, TreeExecutor.TreeType.MEDIUM);
                    }
                }))
                .then(Commands.literal("pine").executes(new com.mojang.brigadier.Command<CommandSourceStack>() {
                    @Override
                    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
                        return execute(context, TreeExecutor.TreeType.PINE);
                    }
                }))
                .then(Commands.literal("vanilla")
                        .then(Commands.argument("tree", StringArgumentType.word())
                                .suggests(VANILLA_TREE_SUGGESTIONS)
                                .executes(this::executeVanilla)))
                .then(Commands.literal("feature")
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .executes(this::executeFeature)));
    }

    private int executeVanilla(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, "tree");
        return executeWithFeature(ctx, new ResourceLocation("minecraft", name));
    }

    private int executeFeature(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return executeWithFeature(ctx, ResourceLocationArgument.getId(ctx, "id"));
    }

    private int executeWithFeature(CommandContext<CommandSourceStack> ctx, ResourceLocation id) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Optional<ConfiguredFeature<?, ?>> feature = BuiltinRegistries.CONFIGURED_FEATURE.getOptional(id);
        if (!feature.isPresent()) {
            CommandUtil.sendError(ctx.getSource(),
                    CommandUtil.translate("command.jfb.tree.feature.not_found", id));
            return 0;
        }
        BuildState state = CommandUtil.getState(player);
        state.setBuilding(true);
        state.setContext(new TreeContext(feature.get(), id));

        CommandUtil.sendMessage(ctx.getSource(),
                CommandUtil.translate("command.jfb.tree.success", id));
        CommandUtil.pushPreviewSnapshots(player, state);
        return 1;
    }

    private int execute(CommandContext<CommandSourceStack> ctx, TreeExecutor.TreeType type) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        BuildState state = CommandUtil.getState(player);
        state.setBuilding(true);
        state.setContext(new TreeContext(type));

        CommandUtil.sendMessage(ctx.getSource(),
                CommandUtil.translate("command.jfb.tree.success", CommandUtil.translate("jfb.tree." + type.name().toLowerCase())));
        CommandUtil.pushPreviewSnapshots(player, state);
        return 1;
    }
}
