/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.item.NBSStaffItem;
import net.rainy_juzixiao.justforbuilding.key.StaffKeyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Inject(method = "startDestroyBlock", at = @At("HEAD"))
    private void jfb$recordBasePosOnBreak(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> ci) {
        if (Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.getMainHandItem().getItem() instanceof NBSStaffItem) {
            // 手持 NBS 手杖开始破坏方块时，确保把客户端基点设为被破坏的方块本身
            StaffKeyClient.basePos = pos;
        }
    }
}
