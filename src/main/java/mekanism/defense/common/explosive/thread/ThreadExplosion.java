package defense.common.explosive.thread;

import java.util.HashSet;

import mekanism.api.Pos3D;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/** @author Calclavia */
public abstract class ThreadExplosion extends Thread
{
    public final Pos3D position;
    public World worldObj;
    public int explosionRadius;
    public float explosionEnergy;
    public Entity explosionSource;

    public boolean isComplete = false;

    public final HashSet<Pos3D> deltaSet = new HashSet<Pos3D>();
    public final HashSet<Pos3D> results = new HashSet<Pos3D>();

    public ThreadExplosion(World world, Pos3D pos, int radius, float energy, Entity source)
    {
    	worldObj = world;
        position = pos;
        explosionRadius = radius;
        explosionEnergy = energy;
        explosionSource = source;
        setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void run()
    {
        isComplete = true;
    }
}
