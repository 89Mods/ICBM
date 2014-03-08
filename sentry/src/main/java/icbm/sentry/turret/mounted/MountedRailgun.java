package icbm.sentry.turret.mounted;

import icbm.Reference;
import icbm.api.sentry.IAmmunition;
import icbm.explosion.explosive.EntityExplosion;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.items.ItemAmmo.AmmoType;
import icbm.sentry.turret.weapon.WeaponProjectile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.fake.IMultiBlock;
import calclavia.lib.prefab.vector.Cuboid;

/**
 * Railgun
 * 
 * @author Calclavia
 */
public class MountedRailgun extends TurretMounted implements IMultiBlock
{
	private int powerUpTicks = -1;

	/** Is current ammo antimatter */
	private boolean isAntimatter;

	private float explosionSize;

	/** A counter used client side for the smoke and streaming effects of the Railgun after a shot. */
	private int endTicks = 0;

	public MountedRailgun(TileTurret turretProvider)
	{
		super(turretProvider);
		energy = new EnergyStorageHandler(1000000000);
		riderOffset = new Vector3(0, 0.2, 0);
		explosionSize = 5;

		weaponSystem = new WeaponProjectile(this, 1, 0)
		{
			@Override
			public boolean isAmmo(ItemStack stack)
			{
				return stack.getItem() instanceof IAmmunition && (stack.getItemDamage() == AmmoType.BULLET_RAIL.ordinal() || stack.getItemDamage() == AmmoType.BULLET_ANTIMATTER.ordinal());
			}

			@Override
			public void fire(Vector3 target)
			{
				powerUpTicks = 0;
				consumeAmmo(ammoAmount, true);
			}
		};
	}

	@Override
	public void update()
	{
		super.update();

		if (!world().isRemote)
		{
			if (world().isBlockIndirectlyGettingPowered((int) getHost().x(), (int) getHost().y() - 1, (int) getHost().z()))
			{
				if (canFire())
				{
					powerUpTicks = 0;
				}
			}

			if (powerUpTicks >= 0)
			{
				powerUpTicks++;

				if (powerUpTicks >= 70)
				{
					int explosionDepth = 10;
					Vector3 hit = null;
					while (explosionDepth > 0)
					{
						MovingObjectPosition objectMouseOver = ai.rayTrace(2000);

						if (objectMouseOver != null)
						{
							hit = new Vector3(objectMouseOver.hitVec);

							/**
							 * Kill all active explosives with antimatter.
							 */
							if (isAntimatter)
							{
								int radius = 50;
								AxisAlignedBB bounds = new Cuboid().expand(radius).translate(hit).toAABB();
								List<EntityExplosion> entities = world().getEntitiesWithinAABB(EntityExplosion.class, bounds);

								for (EntityExplosion entity : entities)
								{
									entity.endExplosion();
								}
							}

							int blockID = hit.getBlockID(world());

							if (Block.blocksList[blockID] == null || Block.blocksList[blockID].blockResistance != -1)
							{
								hit.setBlock(world(), 0);
							}

							// TODO: Fix this null.
							world().newExplosion(null, hit.x, hit.y, hit.z, explosionSize, true, true);
						}

						explosionDepth--;
					}

					if (hit != null)
						fire(hit);

					this.powerUpTicks = -1;
				}
			}
		}
	}

	public void renderShot(Vector3 target)
	{
		this.endTicks = 20;
	}

	public void playFiringSound()
	{
		this.world().playSoundEffect(this.x(), this.y(), this.z(), Reference.PREFIX + "railgun", 5F, 1F);
	}

	@Override
	public Vector3[] getMultiBlockVectors()
	{
		return new Vector3[] { new Vector3(0, 1, 0) };
	}
}