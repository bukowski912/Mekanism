package mekanism.client.gui;

import org.lwjgl.glfw.GLFW;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import mekanism.client.gui.element.custom.GuiModuleScreen;
import mekanism.client.gui.element.scroll.GuiModuleScrollList;
import mekanism.client.gui.element.slot.GuiSlot;
import mekanism.client.gui.element.slot.SlotType;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Module;
import mekanism.common.inventory.container.ModuleTweakerContainer;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.network.PacketUpdateInventorySlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class GuiModuleTweaker extends GuiMekanism<ModuleTweakerContainer> {

    private GuiModuleScrollList scrollList;
    private GuiModuleScreen moduleScreen;

    private int selected = -1;

    public GuiModuleTweaker(ModuleTweakerContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        xSize = 248;
        ySize += 20;
    }

    @Override
    public void func_231160_c_() {
        super.func_231160_c_();

        func_230480_a_(moduleScreen = new GuiModuleScreen(this, 138, 20, stack -> {
            int slotId = container.inventorySlots.get(selected).getSlotIndex();
            Mekanism.packetHandler.sendToServer(new PacketUpdateInventorySlot(stack, slotId));
            playerInventory.player.inventory.setInventorySlotContents(slotId, stack);
        }));
        func_230480_a_(scrollList = new GuiModuleScrollList(this, 30, 20, 108, 134, () -> getStack(selected), this::onModuleSelected));
        int size = container.inventorySlots.size();
        for (int i = 0; i < size; i++) {
            Slot slot = container.inventorySlots.get(i);
            final int index = i;
            // initialize selected item
            if (selected == -1 && isValidItem(index)) {
                select(index);
            }
            func_230480_a_(new GuiSlot(SlotType.NORMAL, this, slot.xPos - 1, slot.yPos - 1)
                  .click((e, x, y) -> select(index))
                  .overlayColor(isValidItem(index) ? null : () -> 0xCC333333)
                  .with(() -> index == selected ? SlotOverlay.SELECT : null));
        }
    }

    private void onModuleSelected(Module module) {
        moduleScreen.setModule(module);
    }

    @Override
    public boolean func_231046_a_(int key, int i, int j) {
        if (super.func_231046_a_(key, i, j)) {
            return true;
        }

        if (selected != -1) {
            int curIndex = -1;
            IntList selectable = new IntArrayList();
            for (int index = 0; index < container.inventorySlots.size(); index++) {
                if (isValidItem(index)) {
                    selectable.add(index);
                    if (index == selected) {
                        curIndex = selectable.size() - 1;
                    }
                }
            }

            if (key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_LEFT) {
                curIndex = curIndex == 0 ? curIndex + selectable.size() - 1 : curIndex - 1;
                select(selectable.getInt(curIndex % selectable.size()));
                return true;
            } else if (key == GLFW.GLFW_KEY_DOWN || key == GLFW.GLFW_KEY_RIGHT) {
                select(selectable.getInt((curIndex + 1) % selectable.size()));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean func_231048_c_(double mouseX, double mouseY, int button) {
        // make sure we get the release event
        moduleScreen.onRelease(mouseX, mouseY);
        return super.func_231048_c_(mouseX, mouseY, button);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawTitleText(MekanismLang.MODULE_TWEAKER.translate(), 6);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    private void select(int index) {
        if (isValidItem(index)) {
            selected = index;
            scrollList.updateList(getStack(index), true);
        }
    }

    private boolean isValidItem(int index) {
        return getStack(index).getItem() instanceof IModuleContainerItem;
    }

    private ItemStack getStack(int index) {
        if (index == -1) {
            return ItemStack.EMPTY;
        }
        return container.inventorySlots.get(index).getStack();
    }
}
