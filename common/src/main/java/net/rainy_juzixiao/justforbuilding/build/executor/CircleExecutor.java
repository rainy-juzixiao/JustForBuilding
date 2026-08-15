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
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.OperationType;
import net.rainy_juzixiao.justforbuilding.geo.CircleGeometry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CircleExecutor implements BuildExecutor {

    private int size;
    private boolean useDiameter;
    private boolean hollow;
    private RectAnchor anchor = RectAnchor.CENTER;

    public CircleExecutor(int size, boolean useDiameter, boolean hollow) {
        this.size = size;
        this.useDiameter = useDiameter;
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

        List<BlockPos> positions = new ArrayList<>();
        BuildDirection facing = BuildDirection.fromYRot(player.yRot);

        if (useDiameter) {
            CircleGeometry.fillPositionsDiameter(pos, facing, size, hollow, anchor, positions);
        } else {
            CircleGeometry.fillPositionsRadius(pos, facing, size, hollow, anchor, positions);
        }

        for (BlockPos target : positions) {
            BlockOperations.setBlockWithRecord(level, target, seed, operations);
        }
        return operations;
    }

    public void writePreview(FriendlyByteBuf buf) {
        buf.writeInt(size);
        buf.writeBoolean(useDiameter);
        buf.writeBoolean(hollow);
        buf.writeByte(anchor.ordinal());
    }

    public void readPreview(FriendlyByteBuf buf) {
        this.size = buf.readInt();
        this.useDiameter = buf.readBoolean();
        this.hollow = buf.readBoolean();
        this.anchor = RectAnchor.values()[buf.readByte()];
    }

    public void setAnchor(RectAnchor anchor) {
        this.anchor = anchor;
    }

    public int getSize() {
        return size;
    }

    public boolean isUseDiameter() {
        return useDiameter;
    }

    public boolean isHollow() {
        return hollow;
    }

    public RectAnchor getAnchor() {
        return anchor;
    }
}