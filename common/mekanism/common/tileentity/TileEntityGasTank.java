package mekanism.common.tileentity;

import java.util.ArrayList;

import mekanism.api.Object3D;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.GasTransmission;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasItem;
import mekanism.api.gas.ITubeConnection;
import mekanism.common.IRedstoneControl;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityGasTank extends TileEntityContainerBlock implements IGasHandler, ITubeConnection, IRedstoneControl
{
	/** The type of gas stored in this tank. */
	public GasTank gasTank = new GasTank(MAX_GAS);
	
	public static final int MAX_GAS = 96000;
	
	/** How fast this tank can output gas. */
	public int output = 16;
	
	/** This machine's current RedstoneControl type. */
	public RedstoneControl controlType;
	
	public TileEntityGasTank()
	{
		super("GasTank");
		inventory = new ItemStack[2];
		controlType = RedstoneControl.DISABLED;
	}
	
	@Override
	public void onUpdate()
	{		
		if(inventory[0] != null && gasTank.getGas() != null)
		{
			gasTank.draw(GasTransmission.addGas(inventory[0], gasTank.getGas()), true);
		}
		
		if(inventory[1] != null && (gasTank.getGas() == null || gasTank.getGas().amount < gasTank.getMaxGas()))
		{
			gasTank.fill(GasTransmission.removeGas(inventory[1], null, gasTank.getNeeded()), true);
		}
		
		if(!worldObj.isRemote && gasTank.getGas() != null && MekanismUtils.canFunction(this))
		{
			GasStack toSend = new GasStack(gasTank.getGas().getGas(), Math.min(gasTank.getStored(), output));
			gasTank.draw(GasTransmission.emitGasToNetwork(toSend, this, ForgeDirection.getOrientation(facing)), true);
			
			TileEntity tileEntity = Object3D.get(this).getFromSide(ForgeDirection.getOrientation(facing)).getTileEntity(worldObj);
			
			if(tileEntity instanceof IGasHandler)
			{
				if(((IGasHandler)tileEntity).canReceiveGas(ForgeDirection.getOrientation(facing).getOpposite(), gasTank.getGas().getGas()))
				{
					gasTank.draw(((IGasHandler)tileEntity).receiveGas(ForgeDirection.getOrientation(facing).getOpposite(), toSend), true);
				}
			}
		}
	}
	
	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
	{
		if(slotID == 1)
		{
			return (itemstack.getItem() instanceof IGasItem && ((IGasItem)itemstack.getItem()).getGas(itemstack) == null);
		}
		else if(slotID == 0)
		{
			return (itemstack.getItem() instanceof IGasItem && ((IGasItem)itemstack.getItem()).getGas(itemstack) != null &&
					((IGasItem)itemstack.getItem()).getGas(itemstack).amount == ((IGasItem)itemstack.getItem()).getMaxGas(itemstack));
		}
		
		return false;
	}
	
	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
	{
		if(slotID == 0)
		{
			return itemstack.getItem() instanceof IGasItem && (gasTank.getGas() == null || ((IGasItem)itemstack.getItem()).canReceiveGas(itemstack, gasTank.getGas().getGas()));
		}
		else if(slotID == 1)
		{
			return itemstack.getItem() instanceof IGasItem && (gasTank.getGas() == null || ((IGasItem)itemstack.getItem()).canProvideGas(itemstack, gasTank.getGas().getGas()));
		}
		
		return true;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return side == 1 ? new int[] {0} : new int[] {1};
	}

	@Override
	public int receiveGas(ForgeDirection side, GasStack stack) 
	{
		return gasTank.fill(stack, true);
	}

	@Override
	public GasStack drawGas(ForgeDirection side, int amount)
	{
		return null;
	}

	@Override
	public boolean canDrawGas(ForgeDirection side, Gas type)
	{
		return gasTank.canDraw(type);
	}

	@Override
	public boolean canReceiveGas(ForgeDirection side, Gas type) 
	{
		if(side != ForgeDirection.getOrientation(facing))
		{
			return gasTank.canReceive(type);
		}
		
		return false;
	}
	
	@Override
	public void handlePacketData(ByteArrayDataInput dataStream)
	{
		super.handlePacketData(dataStream);
		
		if(dataStream.readBoolean())
		{
			gasTank.setGas(new GasStack(GasRegistry.getGas(dataStream.readInt()), dataStream.readInt()));
		}
		else {
			gasTank.setGas(null);
		}
		
		controlType = RedstoneControl.values()[dataStream.readInt()];
		
		MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbtTags)
    {
        super.readFromNBT(nbtTags);

    	gasTank = GasTank.readFromNBT(nbtTags.getCompoundTag("gasTank"));
        controlType = RedstoneControl.values()[nbtTags.getInteger("controlType")];
    }

	@Override
    public void writeToNBT(NBTTagCompound nbtTags)
    {
        super.writeToNBT(nbtTags);
        
        nbtTags.setCompoundTag("gasTank", gasTank.write(new NBTTagCompound()));
        nbtTags.setInteger("controlType", controlType.ordinal());
    }
	
	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		
		if(gasTank.getGas() != null)
		{
			data.add(true);
			data.add(gasTank.getGas().getGas().getID());
			data.add(gasTank.getStored());
		}
		else {
			data.add(false);
		}
		
		data.add(controlType.ordinal());
		
		return data;
	}
	
	@Override
	public boolean canSetFacing(int side)
	{
		return side != 0 && side != 1;
	}

	@Override
	public boolean canTubeConnect(ForgeDirection side) 
	{
		return true;
	}
	
	@Override
	public RedstoneControl getControlType() 
	{
		return controlType;
	}

	@Override
	public void setControlType(RedstoneControl type) 
	{
		controlType = type;
	}
}
