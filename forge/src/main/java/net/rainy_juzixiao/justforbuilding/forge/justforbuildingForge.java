/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.forge;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.rainy_juzixiao.justforbuilding.item.ModItems;
import net.rainy_juzixiao.justforbuilding.justforbuilding;
import net.rainy_juzixiao.justforbuilding.key.StaffKeyClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

@Mod(justforbuilding.MOD_ID)
public class justforbuildingForge {

    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, justforbuilding.MOD_ID);

    public justforbuildingForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(justforbuilding.MOD_ID, modBus);
        ITEMS.register("nbs_staff", () -> ModItems.NBS_STAFF);
        ITEMS.register(modBus);
        justforbuilding.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            justforbuilding.initClient();
            StaffKeyClient.keyBinding = new KeyMapping(
                    "key.jfb.confirm",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_B,
                    "key.categories.jfb");
            ClientRegistry.registerKeyBinding(StaffKeyClient.keyBinding);
        }
    }
}
