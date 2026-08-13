package net.rainy_juzixiao.justforbuilding.preview.cube;

import io.netty.buffer.UnpooledByteBufAllocator;
import me.shedaniel.architectury.networking.NetworkManager;
import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.context.CubeContext;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class CubePreviewSync {

    private static final ResourceLocation CHANNEL = new ResourceLocation("justforbuilding", "preview");

    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL, (buf, context) -> {
            boolean active = buf.readBoolean();
            CubeContext cube = null;
            if (active) {
                cube = new CubeContext(0, 0, 0, false, false);
                cube.readPreview(buf);
            }
            CubeContext snapshot = cube;
            context.queue(() -> PreviewFactory.get(BuildMode.CUBE).update(active, snapshot));
        });
    }

    public static void pushSnapshot(ServerPlayer player, BuildState state) {
        FriendlyByteBuf buf = new FriendlyByteBuf(new UnpooledByteBufAllocator(false).buffer());
        BuildContext context = state.getContext();
        buf.writeBoolean(state.isBuilding() && context != null && context.mode() == BuildMode.CUBE);
        if (context != null) {
            context.writePreview(buf);
        }
        NetworkManager.sendToPlayer(player, CHANNEL, buf);
    }
}