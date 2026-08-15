/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview.tree;

import io.netty.buffer.UnpooledByteBufAllocator;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.build.executor.TreeExecutor;
import net.rainy_juzixiao.justforbuilding.command.context.TreeContext;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class TreePreviewSync {

    private static final ResourceLocation CHANNEL = new ResourceLocation("justforbuilding", "tree_preview");

    private static final int RANGE_XZ = 12;
    private static final int RANGE_HEIGHT = 48;

    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL, (buf, context) -> {
            boolean active = buf.readBoolean();
            boolean destroy = buf.readBoolean();
            TreeContext tree = null;
            if (active) {
                if (buf.readBoolean()) {
                    ResourceLocation id = new ResourceLocation(buf.readUtf(32767));
                    int rangeXZ = buf.readVarInt();
                    int rangeHeight = buf.readVarInt();
                    tree = new TreeContext(id, rangeXZ, rangeHeight);
                } else {
                    TreeExecutor.TreeType type = TreeExecutor.TreeType.values()[buf.readByte()];
                    int count = buf.readVarInt();
                    List<BlockPos> shape = new ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        shape.add(new BlockPos(buf.readVarInt(), buf.readVarInt(), buf.readVarInt()));
                    }
                    tree = new TreeContext(type, shape);
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
                List<BlockPos> shape = treeContext.getExecutor().computeShape(BlockPos.ZERO);
                buf.writeVarInt(shape.size());
                for (BlockPos offset : shape) {
                    buf.writeVarInt(offset.getX());
                    buf.writeVarInt(offset.getY());
                    buf.writeVarInt(offset.getZ());
                }
            } else {
                buf.writeBoolean(true);
                buf.writeUtf(treeContext.getFeatureId().toString());
                buf.writeVarInt(RANGE_XZ);
                buf.writeVarInt(RANGE_HEIGHT);
            }
        }
        NetworkManager.sendToPlayer(player, CHANNEL, buf);
    }
}