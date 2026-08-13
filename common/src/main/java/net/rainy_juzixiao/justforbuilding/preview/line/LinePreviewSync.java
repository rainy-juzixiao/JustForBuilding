/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview.line;

import io.netty.buffer.UnpooledByteBufAllocator;
import me.shedaniel.architectury.networking.NetworkManager;
import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.context.LineContext;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class LinePreviewSync {

    private static final ResourceLocation CHANNEL = new ResourceLocation("justforbuilding", "line_preview");

    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL, (buf, context) -> {
            boolean active = buf.readBoolean();
            LineContext line = null;
            if (active) {
                line = new LineContext(0, 0, BuildDirection.NORTH);
                line.readPreview(buf);
            }
            LineContext snapshot = line;
            context.queue(() -> PreviewFactory.get(BuildMode.PLACE).update(active, snapshot));
        });
    }

    public static void pushSnapshot(ServerPlayer player, BuildState state) {
        FriendlyByteBuf buf = new FriendlyByteBuf(new UnpooledByteBufAllocator(false).buffer());
        BuildContext context = state.getContext();
        boolean active = state.isBuilding() && context != null && context.mode() == BuildMode.PLACE && context instanceof LineContext;
        buf.writeBoolean(active);
        if (active) {
            LineContext line = (LineContext) context;
            buf.writeInt(line.getLength());
            buf.writeInt(line.getInterval());
            BuildDirection dir = line.getDirection();
            buf.writeByte(dir != null ? (byte) dir.ordinal() : -1);
        }
        NetworkManager.sendToPlayer(player, CHANNEL, buf);
    }
}