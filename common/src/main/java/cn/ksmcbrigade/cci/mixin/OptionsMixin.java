package cn.ksmcbrigade.cci.mixin;

import cn.ksmcbrigade.cci.CommonClass;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Options.class)
public class OptionsMixin {
    @Mutable
    @Final
    @Shadow public KeyMapping[] keyMappings;

    @Inject(method = "<init>",at = @At("TAIL"))
    public void initKeys(Minecraft pMinecraft, File pGameDirectory, CallbackInfo ci){
        this.keyMappings = ArrayUtils.add(this.keyMappings, CommonClass.key);
    }
}
