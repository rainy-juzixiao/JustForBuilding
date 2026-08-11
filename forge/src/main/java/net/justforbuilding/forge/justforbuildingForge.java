package net.justforbuilding.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.justforbuilding.justforbuilding;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(justforbuilding.MOD_ID)
public class justforbuildingForge {
    public justforbuildingForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(justforbuilding.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        justforbuilding.init();
    }
}
