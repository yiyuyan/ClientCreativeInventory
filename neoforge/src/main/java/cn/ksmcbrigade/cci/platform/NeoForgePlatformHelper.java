package cn.ksmcbrigade.cci.platform;

import cn.ksmcbrigade.cci.platform.services.IPlatformHelper;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public ItemStack setNbt(ItemStack itemStack, PatchedDataComponentMap components) {
        ItemStack stack = itemStack.copy();
        stack.applyComponents(components);
        return stack;
    }
}
