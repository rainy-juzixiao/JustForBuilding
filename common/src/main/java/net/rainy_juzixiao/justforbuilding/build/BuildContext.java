/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.build;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public interface BuildContext {

    BuildMode mode();

    int executePlace(ServerLevel level, BlockPos pos, BlockState seed, ServerPlayer player, BuildState state);

    Component statusComponent(Component enabled, boolean keep, int undoSize);

    default void writePreview(FriendlyByteBuf buf) {
    }
}
