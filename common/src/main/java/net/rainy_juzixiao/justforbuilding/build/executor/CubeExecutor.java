package net.rainy_juzixiao.justforbuilding.build.executor;

import net.rainy_juzixiao.justforbuilding.build.BlockOperations;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildExecutor;
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.OperationType;
import net.rainy_juzixiao.justforbuilding.geo.CubeGeometry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CubeExecutor implements BuildExecutor {
    private int length;
    private int width;
    private int height;
    private boolean frameOnly;
    private boolean hollow;
    private RectAnchor anchor = RectAnchor.FRONT_LEFT;

    public CubeExecutor(int length, int width, int height, boolean frameOnly, boolean hollow) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.frameOnly = frameOnly;
        this.hollow = hollow;
    }

    @Override
    public List<BuildOperation> execute(ServerLevel level, BlockPos pos, BlockState seed, ServerPlayer player) {
        List<BuildOperation> operations = new ArrayList<>();
        
        // 如果起始位置已有方块，此处，我们记录为空气
        if (level.getBlockState(pos).getBlock() == seed.getBlock()) {
            operations.add(new BuildOperation(
                    OperationType.PLACE_BLOCK,
                    level.dimension().location().toString(),
                    pos.asLong(),
                    "minecraft:air",
                    BlockOperations.idOf(seed)
            ));
        }
        
        List<BlockPos> positions = new ArrayList<>(length * width * height);
        CubeGeometry.fillPositions(pos, BuildDirection.fromYRot(player.yRot),
                length, width, height, frameOnly, hollow, anchor, positions);
        
        for (BlockPos target : positions) {
            BlockOperations.setBlockWithRecord(level, target, seed, operations);
        }
        return operations;
    }

    public void writePreview(FriendlyByteBuf buf) {
        buf.writeInt(length);
        buf.writeInt(width);
        buf.writeInt(height);
        buf.writeBoolean(frameOnly);
        buf.writeBoolean(hollow);
        buf.writeByte(anchor.ordinal());
    }

    public void readPreview(FriendlyByteBuf buf) {
        this.length = buf.readInt();
        this.width = buf.readInt();
        this.height = buf.readInt();
        this.frameOnly = buf.readBoolean();
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

    public int getHeight() {
        return height;
    }

    public boolean isFrameOnly() {
        return frameOnly;
    }

    public boolean isHollow() {
        return hollow;
    }

    public RectAnchor getAnchor() {
        return anchor;
    }
}