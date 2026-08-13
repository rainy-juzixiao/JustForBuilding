/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.command.context;

import net.minecraft.network.FriendlyByteBuf;
import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.build.executor.LineExecutor;
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.BulkOperation;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class LineContext implements BuildContext {

    private final LineExecutor executor;

    public LineContext(int length, int interval, BuildDirection direction) {
        this.executor = new LineExecutor(length, interval, direction);
    }

    @Override
    public BuildMode mode() {
        return BuildMode.PLACE;
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
        return CommandUtil.translate("command.jfb.status.place",
                enabled, CommandUtil.modeComponent(mode()),
                executor.getLength(), executor.getInterval(),
                CommandUtil.directionComponent(executor.getDirection()),
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

    public int getLength() {
        return executor.getLength();
    }

    public int getInterval() {
        return executor.getInterval();
    }

    public BuildDirection getDirection() {
        return executor.getDirection();
    }
}
