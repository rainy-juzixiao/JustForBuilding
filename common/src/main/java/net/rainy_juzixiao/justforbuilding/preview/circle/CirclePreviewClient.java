package net.rainy_juzixiao.justforbuilding.preview.circle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.command.context.CircleContext;
import net.rainy_juzixiao.justforbuilding.geo.CircleGeometry;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.rainy_juzixiao.justforbuilding.preview.Previewer;
import net.rainy_juzixiao.justforbuilding.preview.RenderPreviewer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class CirclePreviewClient extends RenderPreviewer {

    private static final CirclePreviewClient INSTANCE = new CirclePreviewClient();

    private CirclePreviewClient() {
    }

    @Previewer
    public static void register() {
        PreviewFactory.register(BuildMode.CIRCLE, INSTANCE);
    }

    @Override
    protected void computePositions(BlockPos start, Minecraft minecraft) {
        CircleContext context = context();
        if (context == null) {
            return;
        }

        BlockPos center = new BlockPos(start.getX(), start.getY(), start.getZ());

        boolean hollow = context.isHollow();
        int size = context.getSize();

        int totalBlocks = size * size;
        if (!hollow && totalBlocks > MAX_PREVIEW_BLOCKS) {
            hollow = true;
        }

        if (minecraft.player != null) {
            BuildDirection facing = BuildDirection.fromYRot(minecraft.player.yRot);
            if (context.isUseDiameter()) {
                CircleGeometry.fillPositionsDiameter(center, facing, size, hollow, context.getAnchor(), positions);
            } else {
                CircleGeometry.fillPositionsRadius(center, facing, size, hollow, context.getAnchor(), positions);
            }
        }
    }
}