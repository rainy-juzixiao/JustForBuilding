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
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;
import net.rainy_juzixiao.justforbuilding.geo.RectGeometry;
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.OperationType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RectExecutor implements BuildExecutor {

    private int length;
    private int width;
    private boolean hollow;
    private RectAnchor anchor = RectAnchor.FRONT_LEFT;

    public RectExecutor(int length, int width, boolean hollow) {
        this.length = length;
        this.width = width;
        this.hollow = hollow;
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
        List<BlockPos> positions = new ArrayList<>(length * width);
        RectGeometry.fillPositions(pos, BuildDirection.fromYRot(player.yRot),
                length, width, hollow, anchor, positions);
        for (BlockPos target : positions) {
            BlockOperations.setBlockWithRecord(level, target, seed, operations);
        }
        return operations;
    }

    public void writePreview(FriendlyByteBuf buf) {
        buf.writeInt(length);
        buf.writeInt(width);
        buf.writeBoolean(hollow);
        buf.writeByte(anchor.ordinal());
    }

    public void readPreview(FriendlyByteBuf buf) {
        this.length = buf.readInt();
        this.width = buf.readInt();
        this.hollow = buf.readBoolean();
        this.anchor = RectAnchor.values()[buf.readByte()];
    }

    public void setAnchor(RectAnchor anchor) {
        this.anchor = anchor;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public boolean isHollow() {
        return hollow;
    }

    public RectAnchor getAnchor() {
        return anchor;
    }
}
