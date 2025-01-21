package cn.ksmcbrigade.cci.platform;

import cn.ksmcbrigade.cci.platform.services.IPlatformHelper;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public ItemStack setNbt(ItemStack itemStack, PatchedDataComponentMap components) {
        ItemStack stack = itemStack.copy();
        stack.applyComponents(components);
        return stack;
    }
}
