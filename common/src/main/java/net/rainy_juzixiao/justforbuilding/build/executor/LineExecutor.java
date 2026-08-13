/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.build.executor;

import net.rainy_juzixiao.justforbuilding.build.BlockOperations;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildExecutor;
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.OperationType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class LineExecutor implements BuildExecutor {

    private int length;
    private int interval;
    private BuildDirection direction;

    public LineExecutor(int length, int interval, BuildDirection direction) {
        this.length = length;
        this.interval = interval;
        this.direction = direction;
    }

    @Override
    public List<BuildOperation> execute(ServerLevel level, BlockPos pos, BlockState seed, ServerPlayer player) {
        List<BuildOperation> operations = new ArrayList<>();
        if (level.getBlockState(pos).getBlock() == seed.getBlock()) {
            operations.add(new BuildOperation(
                    OperationType.PLACE_BLOCK,
                    level.dimension().location().toString(),
                    pos.asLong(),
                    "minecraft:air",
                    BlockOperations.idOf(seed)
            ));
        }
        BuildDirection dir = direction != null ? direction : BuildDirection.fromYRot(player.yRot);
        int step = interval + 1;
        for (int i = 0; i < length; i += step) {
            BlockOperations.setBlockWithRecord(level, dir.offset(pos, i), seed, operations);
        }
        return operations;
    }

    public void writePreview(FriendlyByteBuf buf) {
        buf.writeInt(length);
        buf.writeInt(interval);
        buf.writeByte(direction != null ? direction.ordinal() : -1);
    }

    public void readPreview(FriendlyByteBuf buf) {
        this.length = buf.readInt();
        this.interval = buf.readInt();
        byte dirOrdinal = buf.readByte();
        this.direction = dirOrdinal >= 0 ? BuildDirection.values()[dirOrdinal] : null;
    }

    public int getLength() {
        return length;
    }

    public int getInterval() {
        return interval;
    }

    public BuildDirection getDirection() {
        return direction;
    }
}