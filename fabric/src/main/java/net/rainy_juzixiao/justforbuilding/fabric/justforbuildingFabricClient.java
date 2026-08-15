/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.rainy_juzixiao.justforbuilding.justforbuilding;
import net.rainy_juzixiao.justforbuilding.key.StaffKeyClient;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class justforbuildingFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        justforbuilding.initClient();
        StaffKeyClient.keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.jfb.confirm",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "key.categories.jfb"));
    }
}
