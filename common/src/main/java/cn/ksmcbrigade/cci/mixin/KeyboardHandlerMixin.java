package cn.ksmcbrigade.cci.mixin;

import cn.ksmcbrigade.cci.CommonClass;
import cn.ksmcbrigade.cci.inventories.ClientInventory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress",at = @At("HEAD"))
    public void keyPress(long pWindowPointer, int pKey, int pScanCode, int pAction, int pModifiers, CallbackInfo ci) throws IOException {
        if(CommonClass.key.isDown() && Minecraft.getInstance().player!=null && Minecraft.getInstance().player.isCreative()){
            Screen screen = new ClientInventory(CommonClass.big?ChestMenu.sixRows(Minecraft.getInstance().player.inventoryMenu.containerId,Minecraft.getInstance().player.getInventory()):ChestMenu.threeRows(Minecraft.getInstance().player.inventoryMenu.containerId,Minecraft.getInstance().player.getInventory()));
            Minecraft.getInstance().setScreen(screen);
        }
        else if(CommonClass.key.isDown() && Minecraft.getInstance().player!=null && !Minecraft.getInstance().player.isCreative()){
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Only Creative Mode can do!").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
