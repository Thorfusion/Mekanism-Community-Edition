package defense.common.explosive.thread;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

/** Used for searching block spawn. Returns a block above this found block coordinate.
 * 
 * @author Calclavia */
public class ThreadSky extends ThreadExplosion
{
    public static interface IThreadCallBack
    {
        public float getResistance(World world, Pos3D position, Pos3D targetPosition, Entity source, Block block);
    }

    public IThreadCallBack callBack;

    public ThreadSky(World world, Pos3D position, int banJing, float nengLiang, Entity source, IThreadCallBack callBack)
    {
        super(world, position, banJing, nengLiang, source);
        this.callBack = callBack;
    }

    public ThreadSky(World world, Pos3D position, int banJing, float nengLiang, Entity source)
    {
        this(world, position, banJing, nengLiang, source, new IThreadCallBack()
        {

            @Override
            public float getResistance(World world, Pos3D explosionPosition, Pos3D targetPosition, Entity source, Block block)
            {
                float resistance = 0;

                if (block instanceof BlockLiquid || block instanceof IFluidBlock)
                {
                    resistance = 0.25f;
                }
                else
                {
                    resistance = block.getExplosionResistance(source, world, (int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, explosionPosition.xPos, explosionPosition.yPos, explosionPosition.zPos);
                }

                return resistance;
            }

        });
    }

    @Override
    public void run()
    {
        int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / this.explosionRadius));

        for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
        {
            for (int theta_n = 0; theta_n < steps; theta_n++)
            {
                double phi = Math.PI * 2 / steps * phi_n;
                double theta = Math.PI / steps * theta_n;

                Pos3D delta = new Pos3D(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi));
                float power = this.explosionEnergy - (this.explosionEnergy * this.worldObj.rand.nextFloat() / 2);

                Pos3D targetPosition = this.position.clone();

                for (float var21 = 0.3F; power > 0f; power -= var21 * 0.75F * 10)
                {
                    if (targetPosition.distance(position) > this.explosionRadius)
                        break;

                    Block block = this.worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                    if (!worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos))
                    {
                        if (block == Blocks.bedrock)
                        {
                            break;
                        }

                        float resistance = this.callBack.getResistance(this.worldObj, position, targetPosition, explosionSource, block);

                        power -= resistance;

                        if (power > 0f)
                        {
                            this.results.add(targetPosition.clone().translate(new Pos3D(0, 1, 0)));
                        }
                    }

                    targetPosition.xPos += delta.xPos;
                    targetPosition.yPos += delta.yPos;
                    targetPosition.zPos += delta.zPos;
                }
            }
        }
        
        super.run();
    }
}
