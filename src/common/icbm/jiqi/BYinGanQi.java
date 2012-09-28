package icbm.jiqi;

import icbm.ICBM;
import icbm.ICBMCommonProxy;

import java.util.ArrayList;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.UniversalElectricity;
import universalelectricity.implement.IRedstoneProvider;
import universalelectricity.prefab.BlockMachine;

public class BYinGanQi extends BlockMachine
{
    public BYinGanQi(int id, int texture)
    {
        super("Proximity Detector", id,  UniversalElectricity.machine, CreativeTabs.tabDecorations);
        this.blockIndexInTexture = texture;
    }
    
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int par2)
    {
        return side == 0 ? this.blockIndexInTexture : (side == 1 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture + 2);
    }
    
    @Override
    public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
    {
        TYinGanQi tileEntity = (TYinGanQi)par1World.getBlockTileEntity(x, y, z);
        
        if(tileEntity != null)
        {
        	par5EntityPlayer.openGui(ICBM.instance, ICBMCommonProxy.GUI_DETECTOR, par1World, x, y, z);
        }
        
		return true;
	}

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    /**
     * Is this block powering the block on the specified side
     */
    @Override
    public boolean isPoweringTo(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
    	TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);
    	
    	if(tileEntity instanceof IRedstoneProvider)
    	{
    		return ((IRedstoneProvider)tileEntity).isPoweringTo((byte)side);
    	}
    	
        return false;
    }
    
    /**
     * Is this block indirectly powering the block on the specified side
     */
    @Override
    public boolean isIndirectlyPoweringTo(World par1World, int x, int y, int z, int side)
    {
    	TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);
    	
    	if(tileEntity instanceof IRedstoneProvider)
    	{
    		return ((IRedstoneProvider)tileEntity).isIndirectlyPoweringTo((byte)side);
    	}
    	
        return false;
    }
    
    @Override
    public boolean canProvidePower()
    {
        return true;
    }

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TYinGanQi();
	}
	
	/**
     * Override this if you do not want your machine to be added to the creative menu or if you have
     * metadata machines and want to add more machines to the creative menu.
     */
    @Override
	public void addCreativeItems(ArrayList itemList)
    {
    	itemList.add(new ItemStack(this));
    }
    
    @Override
	public String getTextureFile()
    {
        return ICBM.BLOCK_TEXTURE_FILE;
    }
}