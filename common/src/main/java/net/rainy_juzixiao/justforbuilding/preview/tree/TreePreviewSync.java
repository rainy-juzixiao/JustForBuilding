/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview.tree;

import io.netty.buffer.UnpooledByteBufAllocator;
import me.shedaniel.architectury.networking.NetworkManager;
import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.build.executor.TreeExecutor;
import net.rainy_juzixiao.justforbuilding.command.context.TreeContext;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class TreePreviewSync {

    private static final ResourceLocation CHANNEL = new ResourceLocation("justforbuilding", "tree_preview");

    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL, (buf, context) -> {
            boolean active = buf.readBoolean();
            boolean destroy = buf.readBoolean();
            TreeContext tree = null;
            if (active) {
                if (buf.readBoolean()) {
                    tree = new TreeContext(new ResourceLocation(buf.readUtf(32767)));
                } else {
                    TreeExecutor.TreeType type = TreeExecutor.TreeType.values()[buf.readByte()];
                    tree = new TreeContext(type);
                }
            }
            final TreeContext snapshot = tree;
            final boolean isDestroy = destroy;
            context.queue(new Runnable() {
                @Override
                public void run() {
                    PreviewFactory.get(BuildMode.TREE).update(active, snapshot, isDestroy);
                }
            });
        });
    }

    public static void pushSnapshot(ServerPlayer player, BuildState state) {
        FriendlyByteBuf buf = new FriendlyByteBuf(new UnpooledByteBufAllocator(false).buffer());
        BuildContext context = state.getContext();
        boolean active = state.isBuilding() && context != null && context.mode() == BuildMode.TREE && context instanceof TreeContext;
        buf.writeBoolean(active);
        buf.writeBoolean(state.isDestroy());
        if (active) {
            TreeContext treeContext = (TreeContext) context;
            if (treeContext.getExecutor() != null) {
                buf.writeBoolean(false);
                buf.writeByte(treeContext.getExecutor().getType().ordinal());
            } else {
                buf.writeBoolean(true);
                buf.writeUtf(treeContext.getFeatureId().toString());
            }
        }
        NetworkManager.sendToPlayer(player, CHANNEL, buf);
    }
}