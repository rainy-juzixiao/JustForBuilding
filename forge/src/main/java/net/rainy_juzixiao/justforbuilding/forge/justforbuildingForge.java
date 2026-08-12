/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.rainy_juzixiao.justforbuilding.justforbuilding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(justforbuilding.MOD_ID)
public class justforbuildingForge {
    public justforbuildingForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(justforbuilding.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        justforbuilding.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            justforbuilding.initClient();
        }
    }
}
