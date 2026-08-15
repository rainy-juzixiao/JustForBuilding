/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.key;

import me.shedaniel.architectury.networking.NetworkManager;
import net.rainy_juzixiao.justforbuilding.build.BuildContext;
import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.rainy_juzixiao.justforbuilding.item.NBSStaffItem;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Ctrl+B 快捷键的服务端逻辑：在 NBS 手杖设置的基点上，按当前视角方向执行放置。
 */
public class StaffKeyHandler {

    public static final ResourceLocation CHANNEL = new ResourceLocation("justforbuilding", "staff_confirm");

    public static void registerServer() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CHANNEL, (buf, context) -> {
            Player player = context.getPlayer();
            if (player instanceof ServerPlayer) {
                executeAtBase((ServerPlayer) player);
            }
        });
    }

    public static void executeAtBase(ServerPlayer player) {
        BuildState state = CommandUtil.getState(player);
        BuildContext buildContext = state.getContext();
        BlockPos base = state.getBasePos();
        if (!state.isBuilding() || buildContext == null || base == null) {
            player.displayClientMessage(CommandUtil.translate("command.jfb.staff.no_base"), true);
            return;
        }
        // 删除模式下以空气填充（删除方块），否则使用手上方块物品
        BlockState seed = state.isDestroy()
                ? Blocks.AIR.defaultBlockState()
                : NBSStaffItem.staffSeed(player);
        int placed = buildContext.executePlace(player.getLevel(), base, seed, player, state);
        player.displayClientMessage(CommandUtil.translate("command.jfb.place.triggered", placed), true);
        if (!state.isKeep()) {
            CommandUtil.resetState(state);
            CommandUtil.pushPreviewSnapshots(player, state);
        }
    }
}
