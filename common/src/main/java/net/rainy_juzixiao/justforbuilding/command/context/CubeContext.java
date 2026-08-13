package net.rainy_juzixiao.justforbuilding.command.context;

import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;
import net.rainy_juzixiao.justforbuilding.build.executor.CubeExecutor;
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

public class CubeContext implements BuildContext {

    private final CubeExecutor executor;

    public CubeContext(int length, int width, int height, boolean frameOnly, boolean hollow) {
        this.executor = new CubeExecutor(length, width, height, frameOnly, hollow);
    }

    @Override
    public BuildMode mode() {
        return BuildMode.CUBE;
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
        return CommandUtil.translate("command.jfb.status.cube",
                enabled,
                CommandUtil.modeComponent(mode()),
                executor.getLength(),
                executor.getWidth(),
                executor.getHeight(),
                CommandUtil.cubeTypeComponent(executor.isFrameOnly(), executor.isHollow()),
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

    public int getLength() {
        return executor.getLength();
    }

    public int getWidth() {
        return executor.getWidth();
    }

    public int getHeight() {
        return executor.getHeight();
    }

    public boolean isFrameOnly() {
        return executor.isFrameOnly();
    }

    public boolean isHollow() {
        return executor.isHollow();
    }

    public RectAnchor getAnchor() {
        return executor.getAnchor();
    }
}