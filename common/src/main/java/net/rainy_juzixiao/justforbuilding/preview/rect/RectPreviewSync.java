/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview.rect;

import io.netty.buffer.UnpooledByteBufAllocator;
import me.shedaniel.architectury.networking.NetworkManager;
import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.context.RectContext;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class RectPreviewSync {

    private static final ResourceLocation CHANNEL = new ResourceLocation("justforbuilding", "rect_preview");

    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL, (buf, context) -> {
            boolean active = buf.readBoolean();
            boolean destroy = buf.readBoolean();
            RectContext rect = null;
            if (active) {
                rect = new RectContext(0, 0, false);
                rect.readPreview(buf);
            }
            RectContext snapshot = rect;
            context.queue(() -> PreviewFactory.get(BuildMode.RECT).update(active, snapshot, destroy));
        });
    }

    public static void pushSnapshot(ServerPlayer player, BuildState state) {
        FriendlyByteBuf buf = new FriendlyByteBuf(new UnpooledByteBufAllocator(false).buffer());
        BuildContext context = state.getContext();
        buf.writeBoolean(state.isBuilding() && context != null && context.mode() == BuildMode.RECT);
        buf.writeBoolean(state.isDestroy());
        if (context != null) {
            context.writePreview(buf);
        }
        NetworkManager.sendToPlayer(player, CHANNEL, buf);
    }
}
