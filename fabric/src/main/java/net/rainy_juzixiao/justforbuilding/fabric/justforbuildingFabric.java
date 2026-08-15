/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.fabric;

import net.fabricmc.api.ModInitializer;
import net.rainy_juzixiao.justforbuilding.item.ModItems;
import net.rainy_juzixiao.justforbuilding.justforbuilding;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class justforbuildingFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new ResourceLocation("justforbuilding", "nbs_staff"), ModItems.NBS_STAFF);
        justforbuilding.init();
    }
}
