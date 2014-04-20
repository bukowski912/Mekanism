package mekanism.common.network;

import java.io.DataOutputStream;

import mekanism.common.Mekanism;
import mekanism.common.entity.EntityRobit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketRobit extends MekanismPacket
{
	public RobitPacketType activeType;

	public int guiType;
	public int entityId;

	public String name;

	public PacketRobit(RobitPacketType type, int i1, int i2, String s)
	{
		activeType = type;

		switch(activeType)
		{
			case GUI:
				guiType = i1;
				entityId = i2;
				break;
			case FOLLOW:
				entityId = i1;
				break;
			case NAME:
				name = s;
				entityId = i1;
				break;
			case GO_HOME:
				entityId = i1;
				break;
			case DROP_PICKUP:
				entityId = i1;
				break;
		}
	}

	public void read(ByteArrayDataInput dataStream, EntityPlayer player, World world) throws Exception
	{
		int subType = dataStream.readInt();

		if(subType == 0)
		{
			int type = dataStream.readInt();
			int id = dataStream.readInt();

			if(type == 0)
			{
				player.openGui(Mekanism.instance, 21, world, id, 0, 0);
			}
			else if(type == 1)
			{
				player.openGui(Mekanism.instance, 22, world, id, 0, 0);
			}
			else if(type == 2)
			{
				player.openGui(Mekanism.instance, 23, world, id, 0, 0);
			}
			else if(type == 3)
			{
				player.openGui(Mekanism.instance, 24, world, id, 0, 0);
			}
			else if(type == 4)
			{
				player.openGui(Mekanism.instance, 25, world, id, 0, 0);
			}
		}
		else if(subType == 1)
		{
			int id = dataStream.readInt();

			EntityRobit robit = (EntityRobit)world.getEntityByID(id);

			if(robit != null)
			{
				robit.setFollowing(!robit.getFollowing());
			}
		}
		else if(subType == 2)
		{
			String name = dataStream.readUTF();
			int id = dataStream.readInt();

			EntityRobit robit = (EntityRobit)world.getEntityByID(id);

			if(robit != null)
			{
				robit.setName(name);
			}
		}
		else if(subType == 3)
		{
			int id = dataStream.readInt();

			EntityRobit robit = (EntityRobit)world.getEntityByID(id);

			if(robit != null)
			{
				robit.goHome();
			}
		}
		else if(subType == 4)
		{
			int id = dataStream.readInt();

			EntityRobit robit = (EntityRobit)world.getEntityByID(id);

			if(robit != null)
			{
				robit.setDropPickup(!robit.getDropPickup());
			}
		}
	}

	public void write(DataOutputStream dataStream) throws Exception
	{
		dataStream.writeInt(activeType.ordinal());

		switch(activeType)
		{
			case GUI:
				dataStream.writeInt(guiType);
				dataStream.writeInt(entityId);
				break;
			case FOLLOW:
				dataStream.writeInt(entityId);
				break;
			case NAME:
				dataStream.writeUTF(name);
				dataStream.writeInt(entityId);
				break;
			case GO_HOME:
				dataStream.writeInt(entityId);
				break;
			case DROP_PICKUP:
				dataStream.writeInt(entityId);
				break;
		}
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

	public static enum RobitPacketType
	{
		GUI,
		FOLLOW,
		NAME,
		GO_HOME,
		DROP_PICKUP;
	}
}
