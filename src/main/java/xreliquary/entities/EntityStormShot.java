package xreliquary.entities;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityStormShot extends EntityShotBase {
	public EntityStormShot(World par1World) {
		super(par1World);
	}

	public EntityStormShot(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
	}

	public EntityStormShot(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
	}

    @Override
    void doFiringEffects() {
		worldObj.spawnParticle("mobSpellAmbient", posX + smallGauss(0.1D), posY + smallGauss(0.1D), posZ + smallGauss(0.1D), 0.5D, 0.5D, 0.5D);
		worldObj.spawnParticle("flame", posX, posY, posZ, gaussian(motionX), gaussian(motionY), gaussian(motionZ));
	}

    @Override
    void doFlightEffects() {
        //does nothing
    }

    @Override
    void onImpact(MovingObjectPosition mop) {
		if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null) {
			if (mop.entityHit == shootingEntity)
				return;
			this.onImpact(mop.entityHit);
		} else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (worldObj.canLightningStrikeAt(mop.blockX, mop.blockY, mop.blockZ) && worldObj.getWorldInfo().isRaining() && worldObj.getWorldInfo().isThundering())
                worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, mop.blockX, mop.blockY, mop.blockZ));
			this.groundImpact(mop.sideHit);
		}
	}

    @Override
    void doBurstEffect(int sideHit) {
        //does nothing
    }

    @Override
    void onImpact(Entity mop) {
		if (mop != shootingEntity || ticksInAir > 3)
            doDamage(mop);
		spawnHitParticles("bubble", 18);
		this.setDead();
	}

    @Override
    void spawnHitParticles(String string, int i) {
		for (int particles = 0; particles < i; particles++)
			worldObj.spawnParticle(string, posX, posY - (string == "portal" ? 1 : 0), posZ, gaussian(motionX), rand.nextFloat() + motionY, gaussian(motionZ));
	}

    @Override
    int getRicochetMax() {
		return 1;
	}

    @Override
    int getDamageOfShot(Entity mop) {
        if (mop instanceof EntityCreeper)
            ((EntityCreeper) mop).onStruckByLightning(new EntityLightningBolt(worldObj, mop.posX, mop.posY, mop.posZ));
        if (worldObj.canLightningStrikeAt((int)(mop.posX +  0.5F), (int)(mop.posY +  0.5F), (int)(mop.posZ +  0.5F)) && worldObj.getWorldInfo().isRaining() && worldObj.getWorldInfo().isThundering())
            worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, (int)(mop.posX +  0.5F), (int)(mop.posY +  0.5F), (int)(mop.posZ +  0.5F)));
        float f = 1F + (worldObj.isRaining()? 0.5F : 0F) + (worldObj.isThundering() ? 0.5F : 0F);
        return Math.round(9F * f) + d6();
    }
}
