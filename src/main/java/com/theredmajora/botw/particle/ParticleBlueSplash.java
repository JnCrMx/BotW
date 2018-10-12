package com.theredmajora.botw.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleBlueSplash extends Particle
{
	public ParticleBlueSplash(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
	{	
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		
		this.particleRed=0.0f;
		this.particleGreen=0.7f;
		this.particleBlue=1.0f;
	}

	public ParticleBlueSplash(World worldIn, double posXIn, double posYIn, double posZIn)
	{
		this(worldIn, posXIn, posYIn, posZIn, 0, 0, 0);
	}
	
    public static class Factory implements IParticleFactory
    {
    	public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
    	{
    		return new ParticleBlueSplash(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    	}
	}
}
