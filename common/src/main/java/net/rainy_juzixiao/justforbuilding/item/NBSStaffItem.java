/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.item;

import net.rainy_juzixiao.justforbuilding.build.BuildState;
import net.rainy_juzixiao.justforbuilding.command.CommandUtil;
import net.rainy_juzixiao.justforbuilding.key.StaffKeyClient;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class NBSStaffItem extends Item {

    public NBSStaffItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (level.isClientSide) {
            StaffKeyClient.basePos = context.getClickedPos().relative(context.getClickedFace());
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer)) {
            return InteractionResult.SUCCESS;
        }
        // 仅设置基点，不放置；右键视为建造模式（与左键的删除模式对称），
        // 转动视角选择方向后按 Ctrl+B 在基点放置
        ServerPlayer serverPlayer = (ServerPlayer) player;
        BuildState state = CommandUtil.getState(serverPlayer);
        BlockPos placePos = context.getClickedPos().relative(context.getClickedFace());
        state.setBasePos(placePos);
        state.setDestroy(false);
        serverPlayer.displayClientMessage(
                CommandUtil.translate("command.jfb.staff.base_set", formatPos(placePos)), true);
        CommandUtil.pushPreviewSnapshots(serverPlayer, state);
        return InteractionResult.SUCCESS;
    }

    public static BlockState staffSeed(ServerPlayer player) {
        // 手上拿着可放置的方块物品时优先使用该方块（主手优先，其次副手）
        BlockState state = blockStateFrom(player.getMainHandItem());
        if (state != null) {
            return state;
        }
        state = blockStateFrom(player.getOffhandItem());
        if (state != null) {
            return state;
        }
        return Blocks.OAK_LOG.defaultBlockState();
    }

    private static BlockState blockStateFrom(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem) {
            return ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
        }
        return null;
    }

    private static String formatPos(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }
}
