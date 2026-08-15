/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.key;

import io.netty.buffer.UnpooledByteBufAllocator;
import me.shedaniel.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

@Environment(EnvType.CLIENT)
public class StaffKeyClient {

    public static KeyMapping keyBinding;

    /** 手杖右键设置的基点：非 null 时预览锚定在该点（而非准星） */
    public static BlockPos basePos;

    public static void onClientTick(Minecraft minecraft) {
        if (keyBinding != null && keyBinding.consumeClick() && Screen.hasControlDown()) {
            FriendlyByteBuf buf = new FriendlyByteBuf(UnpooledByteBufAllocator.DEFAULT.buffer());
            NetworkManager.sendToServer(StaffKeyHandler.CHANNEL, buf);
            basePos = null;
        }
    }
}
