package cn.ksmcbrigade.cci.inventories;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ClientInventory extends ContainerScreen {

    private final File file = new File("config/cci-inv.json");

    private final int size;
    private final int rows;

    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final ResourceLocation CONTAINER_27_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/shulker_box.png");

    public ClientInventory(ChestMenu pMenu) throws IOException {
        super(pMenu, Minecraft.getInstance().player.getInventory(), Component.literal("ClientInventory"));
        this.rows = pMenu.getRowCount();
        this.size = this.rows*9-1;
        this.load();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(size==53?CONTAINER_BACKGROUND:CONTAINER_27_BACKGROUND, i, j, 0, 0, this.imageWidth, this.rows * 18 + 17);
        pGuiGraphics.blit(size==53?CONTAINER_BACKGROUND:CONTAINER_27_BACKGROUND, i, j + this.rows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    protected void slotClicked(@Nullable Slot pSlot, int pSlotId, int pMouseButton, @NotNull ClickType pType) {
        try {
            if(this.minecraft==null) this.minecraft = Minecraft.getInstance();
            if(pSlot==null || this.minecraft.player==null) return;
            pSlotId = pSlot.index;

            AbstractContainerMenu abstractcontainermenu = this.menu;
            NonNullList<Slot> nonnulllist = abstractcontainermenu.slots;
            int i = nonnulllist.size();
            List<ItemStack> list = Lists.newArrayListWithCapacity(i);
            Iterator var10 = nonnulllist.iterator();

            while(var10.hasNext()) {
                Slot slot = (Slot)var10.next();
                list.add(slot.getItem().copy());
            }

            if((pSlotId+1)<this.menu.slots.size()){
                abstractcontainermenu.clicked(pSlotId, pMouseButton, pType, this.minecraft.player);

            }
            if(pSlotId>53){
                int inventoryId = Math.abs(pSlotId-54+9);
                if(this.minecraft.player.isCreative()){
                    Minecraft.getInstance().getConnection().getConnection().send(new ServerboundSetCreativeModeSlotPacket(inventoryId,pSlot.getItem()));
                }
                else if(this.minecraft.isSingleplayer()){
                    Minecraft.getInstance().player.inventoryMenu.setItem(inventoryId,this.menu.getStateId(),pSlot.getItem());
                }
            }

            this.save();

            Int2ObjectMap<ItemStack> int2objectmap = new Int2ObjectOpenHashMap();

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = list.get(j);
                ItemStack itemstack1 = nonnulllist.get(j).getItem();
                if (!ItemStack.matches(itemstack, itemstack1)) {
                    int2objectmap.put(j, itemstack1.copy());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //this.connection.send(new ServerboundContainerClickPacket(p_171800_, abstractcontainermenu.getStateId(), p_171801_, p_171802_, p_171803_, abstractcontainermenu.getCarried().copy(), int2objectmap));
    }

    public void save() throws IOException {
        JsonArray array = new JsonArray();
        for (Slot slot : this.menu.slots) {
            if(slot.index>53) break;
            JsonObject object = new JsonObject();
            object.addProperty("slot",slot.index);
            object.addProperty("item",slot.getItem().getItem().toString());
            object.addProperty("count",slot.getItem().getCount());
            CompoundTag tag = (CompoundTag) slot.getItem().saveOptional(Minecraft.getInstance().level.registryAccess());
            Dynamic<Tag> nbtDynamic = new Dynamic<>(NbtOps.INSTANCE, tag);
            Dynamic<JsonElement> jsonDynamic = nbtDynamic.convert(JsonOps.INSTANCE);
            object.add("tags",jsonDynamic.getValue());
            array.add(object);
        }
        FileUtils.writeStringToFile(file,array.toString());
    }

    public void load() throws IOException {
        if(!file.exists()) return;
        JsonArray array = JsonParser.parseString(FileUtils.readFileToString(file)).getAsJsonArray();
        for (JsonElement element : array) {
            if(element instanceof JsonObject jsonObject){
                try {
                    int slot = jsonObject.get("slot").getAsInt();
                    Dynamic<JsonElement> jsonDynamic = new Dynamic<>(JsonOps.INSTANCE,jsonObject.get("tags"));
                    Dynamic<Tag> nbtDynamic = jsonDynamic.convert(NbtOps.INSTANCE);
                    ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(jsonObject.get("item").getAsString())),jsonObject.get("count").getAsInt());
                    if(nbtDynamic.getValue() instanceof CompoundTag compoundTag){
                        stack = ItemStack.parseOptional(Minecraft.getInstance().level.registryAccess(),compoundTag);
                    }
                    this.menu.setItem(slot,this.menu.getStateId(),stack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void containerTick() {
        if(Minecraft.getInstance().player!=null && !Minecraft.getInstance().player.isCreative()){
            this.onClose();
        }
    }
}
