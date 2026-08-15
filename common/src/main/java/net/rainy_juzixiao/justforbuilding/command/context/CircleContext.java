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
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;
import net.rainy_juzixiao.justforbuilding.build.executor.CircleExecutor;
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.BulkOperation;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CircleContext implements BuildContext {

    private final CircleExecutor executor;

    public CircleContext(int size, boolean useDiameter, boolean hollow) {
        this.executor = new CircleExecutor(size, useDiameter, hollow);
    }

    @Override
    public BuildMode mode() {
        return BuildMode.CIRCLE;
    }

    @Override
    public int executePlace(ServerLevel level, BlockPos pos, BlockState seed,
                            ServerPlayer player, BuildState state) {
        List<BuildOperation> operations = executor.execute(level, pos, seed, player);
        if (!operations.isEmpty()) {
            state.pushOperation(new BulkOperation(operations));
        }
        return operations.size();
    }

    @Override
    public Component statusComponent(Component enabled, boolean keep, int undoSize) {
        return CommandUtil.translate("command.jfb.status.circle",
                enabled,
                CommandUtil.modeComponent(mode()),
                executor.getSize(),
                executor.isUseDiameter() ? "diameter" : "radius",
                executor.isHollow() ? "hollow" : "solid",
                CommandUtil.anchorComponent(executor.getAnchor()),
                CommandUtil.stateModeComponent(keep),
                undoSize);
    }

    @Override
    public void writePreview(FriendlyByteBuf buf) {
        executor.writePreview(buf);
    }

    public void readPreview(FriendlyByteBuf buf) {
        executor.readPreview(buf);
    }

    public void setAnchor(RectAnchor anchor) {
        executor.setAnchor(anchor);
    }

    public int getSize() {
        return executor.getSize();
    }

    public boolean isUseDiameter() {
        return executor.isUseDiameter();
    }

    public boolean isHollow() {
        return executor.isHollow();
    }

    public RectAnchor getAnchor() {
        return executor.getAnchor();
    }
}