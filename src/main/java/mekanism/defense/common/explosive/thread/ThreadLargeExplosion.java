package defense.common.explosive.thread;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

/** Used for large raycasting explosions.
 * 
 * @author Calclavia */
public class ThreadLargeExplosion extends ThreadExplosion
{
    public static interface IThreadCallBack
    {
        public float getResistance(World world, Pos3D position, Pos3D targetPosition, Entity source, Block block);
    }

    public IThreadCallBack callBack;

    public ThreadLargeExplosion(World world, Pos3D position, int range, float energy, Entity source, IThreadCallBack cb)
    {
        super(world, position, range, energy, source);
        callBack = cb;
    }

    public ThreadLargeExplosion(World world, Pos3D position, int range, float energy, Entity source)
    {
        this(world, position, range, energy, source, new IThreadCallBack() {
            @Override
            public float getResistance(World world, Pos3D pos, Pos3D targetPosition, Entity source, Block block)
            {
                float resistance = 0;

                if(block instanceof BlockLiquid || block instanceof IFluidBlock)
                {
                    resistance = 0.25f;
                }
                else {
                    resistance = block.getExplosionResistance(source, world, (int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, pos.xPos, pos.yPos, pos.zPos);
                }

                return resistance;
            }
        });
    }

    @Override
    public void run()
    {
        int steps = (int)Math.ceil(Math.PI / Math.atan(1.0D / explosionRadius));

        for(int phi_n = 0; phi_n < 2 * steps; phi_n++)
        {
            for(int theta_n = 0; theta_n < steps; theta_n++)
            {
                double phi = Math.PI * 2 / steps * phi_n;
                double theta = Math.PI / steps * theta_n;

                Pos3D delta = new Pos3D(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi));
                float power = explosionEnergy - (explosionEnergy * worldObj.rand.nextFloat() / 2);

                Pos3D t = position.clone();

                for(float d = 0.3F; power > 0f; power -= d * 0.75F * 10)
                {
                    if(t.distance(position) > explosionRadius)
                    {
                        break;
                    }

                    Block block = worldObj.getBlock((int)t.xPos, (int)t.yPos, (int)t.zPos);

                    if(!worldObj.isAirBlock((int)t.xPos, (int)t.yPos, (int)t.zPos))
                    {
                        if(block.getBlockHardness(worldObj, (int)t.xPos, (int)t.yPos, (int)t.zPos) >= 0)
                        {
                            power -= callBack.getResistance(worldObj, position, t, explosionSource, block);

                            if(power > 0f)
                            {
                                results.add(t.clone());
                            }
                        }
                    }
                    
                    t.translate(delta);
                }
            }
        }

        super.run();
    }
}
