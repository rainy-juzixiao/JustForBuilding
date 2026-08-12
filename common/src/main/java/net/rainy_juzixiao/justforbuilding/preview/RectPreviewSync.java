/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview;

import io.netty.buffer.UnpooledByteBufAllocator;
import me.shedaniel.architectury.networking.NetworkManager;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class RectPreviewSync {

    private static final ResourceLocation CHANNEL = new ResourceLocation("justforbuilding", "preview");

    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL, (buf, context) -> {
            boolean active = buf.readBoolean();
            int length = buf.readInt();
            int width = buf.readInt();
            boolean hollow = buf.readBoolean();
            RectAnchor anchor = RectAnchor.values()[buf.readByte()];
            context.queue(() -> RectPreviewClient.update(active, length, width, hollow, anchor));
        });
    }

    public static void pushSnapshot(ServerPlayer player, BuildState state) {
        FriendlyByteBuf buf = new FriendlyByteBuf(new UnpooledByteBufAllocator(false).buffer());
        buf.writeBoolean(state.isBuilding() && state.getMode() == BuildMode.RECT);
        buf.writeInt(state.getLength());
        buf.writeInt(state.getWidth());
        buf.writeBoolean(state.isHollow());
        buf.writeByte(state.getAnchor().ordinal());
        NetworkManager.sendToPlayer(player, CHANNEL, buf);
    }
}
