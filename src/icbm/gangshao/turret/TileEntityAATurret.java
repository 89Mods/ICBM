package icbm.gangshao.turret;

import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.actions.LookHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import universalelectricity.core.vector.Vector3;

public class TileEntityAATurret extends TileEntityAutoTurret
{
	@Override
	public void onWeaponActivated()
	{
		if (this.getPlatform() != null)
		{
			if (this.target instanceof EntityLiving && this.getPlatform().useAmmunition(ZhuYaoGangShao.conventionalBullet))
			{
				if (this.worldObj.rand.nextFloat() > 0.1)
				{
					((EntityLiving) this.target).attackEntityFrom(DamageSource.setExplosionSource(null), 10);
				}

				Vector3 look = LookHelper.getDeltaPositionFromRotation(this.targetRotationYaw, this.targetRotationPitch);
				look.multiply(-3);
				((EntityLiving) this.target).knockBack(null, 0, look.intX(), look.intZ());
				this.getPlatform().wattsReceived -= this.getRequest();

				if (!this.worldObj.isRemote && this.worldObj.rand.nextFloat() > 0.8)
				{
					Vector3 spawnPos = this.getMuzzle();
					EntityItem entityShell = new EntityItem(this.worldObj, spawnPos.x, spawnPos.y, spawnPos.z, ZhuYaoGangShao.bulletShell.copy());
					entityShell.delayBeforeCanPickup = 20;
					this.worldObj.spawnEntityInWorld(entityShell);
				}

				this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.machinegun", 5F, 1F);

			}
		}
	}

	@Override
	public boolean isRunning()
	{
		return super.isRunning() && this.getPlatform().wattsReceived > 0 && !this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public boolean canActivateWeapon()
	{
		return super.canActivateWeapon() && !this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public double getDetectRange()
	{
		int baseRange = 50;

		if (this.getPlatform() != null)
		{
			return baseRange + 10 * this.getPlatform().getUpgrades("Capacity");
		}

		return baseRange;
	}

	@Override
	public double getVoltage()
	{
		return 120;
	}

	@Override
	public int getCooldown()
	{
		return 1;
	}

	@Override
	public double getRequest()
	{
		return 27;
	}

}