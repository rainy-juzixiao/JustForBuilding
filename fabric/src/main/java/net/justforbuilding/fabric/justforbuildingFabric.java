/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.justforbuilding.fabric;

import net.justforbuilding.justforbuilding;
import net.fabricmc.api.ModInitializer;

public class justforbuildingFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        justforbuilding.init();
    }
}
