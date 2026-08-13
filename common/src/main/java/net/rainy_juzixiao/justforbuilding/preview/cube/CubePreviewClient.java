package net.rainy_juzixiao.justforbuilding.preview.cube;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.command.context.CubeContext;
import net.rainy_juzixiao.justforbuilding.geo.CubeGeometry;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.rainy_juzixiao.justforbuilding.preview.Previewer;
import net.rainy_juzixiao.justforbuilding.preview.RenderPreviewer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class CubePreviewClient extends RenderPreviewer {

    private static final CubePreviewClient INSTANCE = new CubePreviewClient();

    private CubePreviewClient() {
    }

    @Previewer
    public static void register() {
        PreviewFactory.register(BuildMode.CUBE, INSTANCE);
    }

    @Override
    protected void computePositions(BlockPos start, Minecraft minecraft) {
        CubeContext context = context();
        if (context == null) return;

        boolean frameOnly = context.isFrameOnly();
        boolean hollow = context.isHollow();

        // 这里如果方块数量过多，我们就强制使用框架模式
        int totalBlocks = context.getLength() * context.getWidth() * context.getHeight();
        if (!frameOnly && !hollow && totalBlocks > MAX_PREVIEW_BLOCKS) {
            frameOnly = true;
        } else if (!frameOnly && hollow && totalBlocks > MAX_PREVIEW_BLOCKS) {
            frameOnly = true;
        }

        if (minecraft.player != null) {
            CubeGeometry.fillPositions(start, BuildDirection.fromYRot(minecraft.player.yRot),
                    context.getLength(), context.getWidth(), context.getHeight(),
                    frameOnly, context.isHollow(), context.getAnchor(), positions);
        }
    }
}