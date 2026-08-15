/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.command.context;

import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.build.executor.ConfiguredFeatureExecutor;
import net.rainy_juzixiao.justforbuilding.build.executor.TreeExecutor;
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.BulkOperation;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.List;

public class TreeContext implements BuildContext {

    private final TreeExecutor executor;

    private final ConfiguredFeatureExecutor featureExecutor;

    private final ResourceLocation featureId;

    public TreeContext(TreeExecutor.TreeType type) {
        this.executor = new TreeExecutor(type);
        this.featureExecutor = null;
        this.featureId = null;
    }

    public TreeContext(ConfiguredFeature<?, ?> feature, ResourceLocation featureId) {
        this.executor = null;
        this.featureExecutor = new ConfiguredFeatureExecutor(feature);
        this.featureId = featureId;
    }

    public TreeContext(ResourceLocation featureId) {
        this.executor = null;
        this.featureExecutor = null;
        this.featureId = featureId;
    }

    public TreeExecutor getExecutor() {
        return executor;
    }

    public ResourceLocation getFeatureId() {
        return featureId;
    }

    @Override
    public BuildMode mode() {
        return BuildMode.TREE;
    }

    @Override
    public int executePlace(ServerLevel level, BlockPos pos, BlockState seed, ServerPlayer player, BuildState state) {
        List<BuildOperation> operations;
        if (featureExecutor != null) {
            operations = featureExecutor.execute(level, pos, seed, player);
        } else {
            operations = executor.execute(level, pos, seed, player);
        }
        if (!operations.isEmpty()) {
            state.pushOperation(new BulkOperation(operations));
        }
        return operations.size();
    }

    @Override
    public Component statusComponent(Component enabled, boolean keep, int undoSize) {
        if (featureExecutor != null) {
            return CommandUtil.translate("command.jfb.status.tree.feature",
                    enabled, CommandUtil.modeComponent(mode()), featureId,
                    CommandUtil.stateModeComponent(keep), undoSize);
        }
        return CommandUtil.translate("command.jfb.status.tree",
                enabled, CommandUtil.modeComponent(mode()),
                CommandUtil.translate("jfb.tree." + executor.getType().name().toLowerCase()),
                CommandUtil.stateModeComponent(keep),
                undoSize);
    }
}
