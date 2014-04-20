package mekanism.common.network;

import java.io.DataOutputStream;

import mekanism.api.Coord4D;
import mekanism.common.IUpgradeManagement;
import mekanism.common.Mekanism;
import mekanism.common.tile.TileEntityBasicBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketRemoveUpgrade extends MekanismPacket
{
	public Coord4D coord4D;

	public byte upgradeType;

	public PacketRemoveUpgrade(Coord4D coord, byte type)
	{
		coord4D = coord;
		upgradeType = type;
	}

	public void read(ByteArrayDataInput dataStream, EntityPlayer player, World world) throws Exception
	{
		int x = dataStream.readInt();
		int y = dataStream.readInt();
		int z = dataStream.readInt();

		byte type = dataStream.readByte();

		TileEntity tileEntity = world.getTileEntity(x, y, z);

		if(tileEntity instanceof IUpgradeManagement && tileEntity instanceof TileEntityBasicBlock)
		{
			IUpgradeManagement upgradeTile = (IUpgradeManagement)tileEntity;

			if(type == 0)
			{
				if(upgradeTile.getSpeedMultiplier() > 0)
				{
					if(player.inventory.addItemStackToInventory(new ItemStack(Mekanism.SpeedUpgrade)))
					{
						upgradeTile.setSpeedMultiplier(upgradeTile.getSpeedMultiplier()-1);
					}
				}
			}
			else if(type == 1)
			{
				if(upgradeTile.getEnergyMultiplier() > 0)
				{
					if(player.inventory.addItemStackToInventory(new ItemStack(Mekanism.EnergyUpgrade)))
					{
						upgradeTile.setEnergyMultiplier(upgradeTile.getEnergyMultiplier()-1);
					}
				}
			}
		}
	}

	public void write(DataOutputStream dataStream) throws Exception
	{
		dataStream.writeInt(coord4D.xCoord);
		dataStream.writeInt(coord4D.yCoord);
		dataStream.writeInt(coord4D.zCoord);

		dataStream.writeByte(upgradeType);
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{

	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{

	}

	@Override
	public void handleClientSide(EntityPlayer player)
	{

	}

	@Override
	public void handleServerSide(EntityPlayer player)
	{

	}
}
