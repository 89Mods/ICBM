package icbm.zhapin;

import icbm.ICBM;
import icbm.dianqi.ItYaoKong;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.implement.IRotatable;
import universalelectricity.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

public class TZhaDan extends TileEntity implements IRotatable, IPacketReceiver
{
	public byte orientation = 3;
	
	@Override
	public boolean canUpdate()
    {
        return false;
    }

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.orientation);
	}

	@Override
	public void setDirection(ForgeDirection facingDirection) 
	{
		this.orientation = (byte) facingDirection.ordinal();
	}

	/**
     * Reads a tile entity from NBT.
     */
    @Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	
    	this.orientation = par1NBTTagCompound.getByte("orientation");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	
    	par1NBTTagCompound.setByte("orientation", this.orientation);
    }

	@Override
	public void handlePacketData(NetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
        {
	        final int ID = dataStream.readByte();

	        if(ID == 1)
	        {
	        	this.orientation = dataStream.readByte();
	        }
	        else if(ID == 2 && !this.worldObj.isRemote)
	        {
	        	//Packet explode command
	        	if(player.inventory.getCurrentItem().getItem() instanceof ItYaoKong)
				{
	        		ItemStack itemStack = player.inventory.getCurrentItem();
		    		BZhaDan.detonateTNT(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.getBlockMetadata(), 0);
					((ItYaoKong) ICBM.itemYaoKong).onUseElectricity(ItYaoKong.ELECTRICITY_REQUIRED, itemStack);
				}
	        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
}
