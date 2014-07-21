package mekanism.common.inventory.container;

import mekanism.common.IUpgradeTile;
import mekanism.common.inventory.slot.SlotMachineUpgrade;
import mekanism.common.tile.TileEntityContainerBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerUpgradeManagement extends Container
{
	private IUpgradeTile tileEntity;

	public ContainerUpgradeManagement(InventoryPlayer inventory, IUpgradeTile tile)
	{
		tileEntity = tile;
		addSlotToContainer(new SlotMachineUpgrade((TileEntityContainerBlock)tile, 3, 180, 11));
		
		int slotX;

		for(slotX = 0; slotX < 3; slotX++)
		{
			for(int slotY = 0; slotY < 9; slotY++)
			{
				addSlotToContainer(new Slot(inventory, slotY + slotX * 9 + 9, 8 + slotY * 18, 84 + slotX * 18));
			}
		}

		for(slotX = 0; slotX < 9; slotX++)
		{
			addSlotToContainer(new Slot(inventory, slotX, 8 + slotX * 18, 142));
		}

		((TileEntityContainerBlock)tileEntity).open(inventory.player);
		((TileEntityContainerBlock)tileEntity).openInventory();
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer)
	{
		super.onContainerClosed(entityplayer);

		((TileEntityContainerBlock)tileEntity).close(entityplayer);
		((TileEntityContainerBlock)tileEntity).closeInventory();
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return ((TileEntityContainerBlock)tileEntity).isUseableByPlayer(entityplayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
	{
		ItemStack stack = null;
		Slot currentSlot = (Slot)inventorySlots.get(slotID);

		if(currentSlot != null && currentSlot.getHasStack())
		{
			ItemStack slotStack = currentSlot.getStack();
			stack = slotStack.copy();

			if(slotID >= 0 && slotID <= 26)
			{
				if(!mergeItemStack(slotStack, 27, inventorySlots.size(), false))
				{
					return null;
				}
			}
			else if(slotID > 26)
			{
				if(!mergeItemStack(slotStack, 0, 26, false))
				{
					return null;
				}
			}
			else {
				if(!mergeItemStack(slotStack, 0, inventorySlots.size(), true))
				{
					return null;
				}
			}

			if(slotStack.stackSize == 0)
			{
				currentSlot.putStack((ItemStack)null);
			}
			else {
				currentSlot.onSlotChanged();
			}

			if(slotStack.stackSize == stack.stackSize)
			{
				return null;
			}

			currentSlot.onPickupFromSlot(player, slotStack);
		}

		return stack;
	}
}
