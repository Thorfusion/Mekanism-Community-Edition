package mekanism.nuclear.common.content.sps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Validates the stable fixed 7 by 7 by 7 rounded SPS shell. */
public final class SPSValidator
{
    public static final int SIZE = 7;
    private static final int LAST = SIZE - 1;
    private static final byte[][] ALLOWED_GRID = {
          {0, 0, 1, 1, 1, 0, 0},
          {0, 1, 2, 2, 2, 1, 0},
          {1, 2, 2, 2, 2, 2, 1},
          {1, 2, 2, 2, 2, 2, 1},
          {1, 2, 2, 2, 2, 2, 1},
          {0, 1, 2, 2, 2, 1, 0},
          {0, 0, 1, 1, 1, 0, 0}
    };

    private SPSValidator()
    {
    }

    public static Result validate(final World world, SPSPosition port)
    {
        return validate(port, new ComponentLookup()
        {
            @Override
            public SPSComponent get(SPSPosition position)
            {
                return world.blockExists(position.getX(), position.getY(),
                      position.getZ())
                      ? SPSComponent.fromWorld(world, position)
                      : SPSComponent.UNLOADED;
            }
        });
    }

    public static Result validateAtMin(final World world, SPSPosition min)
    {
        return validateAtMin(min, new ComponentLookup()
        {
            @Override
            public SPSComponent get(SPSPosition position)
            {
                return world.blockExists(position.getX(), position.getY(),
                      position.getZ())
                      ? SPSComponent.fromWorld(world, position)
                      : SPSComponent.UNLOADED;
            }
        });
    }

    public static Result validate(SPSPosition port, ComponentLookup lookup)
    {
        if (lookup.get(port) != SPSComponent.PORT)
        {
            return Result.failure(Failure.NOT_PORT, port, 0);
        }
        Result best = null;
        for (SPSPosition min : candidateMinimums(port))
        {
            Result candidate = validateAtMin(min, lookup);
            if (candidate.isFormed())
            {
                return candidate;
            }
            if (best == null
                  || candidate.getMatchedBlocks() > best.getMatchedBlocks())
            {
                best = candidate;
            }
        }
        return best == null
              ? Result.failure(Failure.NO_MATCHING_STRUCTURE, port, 0) : best;
    }

    public static Result validateAtMin(SPSPosition min,
          ComponentLookup lookup)
    {
        SPSPosition max = min.add(LAST, LAST, LAST);
        List<SPSPosition> ports = new ArrayList<SPSPosition>();
        List<SPSPosition> coils = new ArrayList<SPSPosition>();
        Failure firstFailure = Failure.NONE;
        SPSPosition firstFailurePos = null;
        int matched = 0;

        for (int x = 0; x < SIZE; x++)
        {
            for (int y = 0; y < SIZE; y++)
            {
                for (int z = 0; z < SIZE; z++)
                {
                    SPSPosition position = min.add(x, y, z);
                    SPSComponent component = lookup.get(position);
                    if (component == SPSComponent.UNLOADED)
                    {
                        if (firstFailure == Failure.NONE)
                        {
                            firstFailure = Failure.UNLOADED;
                            firstFailurePos = position;
                        }
                        continue;
                    }

                    int requirement = requirement(x, y, z);
                    if (requirement == 0)
                    {
                        matched++;
                    }
                    else if (requirement == 1)
                    {
                        if (component == SPSComponent.CASING)
                        {
                            matched++;
                        }
                        else if (firstFailure == Failure.NONE)
                        {
                            firstFailure = Failure.INVALID_FRAME;
                            firstFailurePos = position;
                        }
                    }
                    else if (requirement == 2)
                    {
                        if (component == SPSComponent.CASING
                              || component == SPSComponent.PORT)
                        {
                            matched++;
                            if (component == SPSComponent.PORT)
                            {
                                ports.add(position);
                            }
                        }
                        else if (firstFailure == Failure.NONE)
                        {
                            firstFailure = Failure.INVALID_FACE;
                            firstFailurePos = position;
                        }
                    }
                    else if (component == SPSComponent.AIR
                          || component == SPSComponent.COIL)
                    {
                        matched++;
                        if (component == SPSComponent.COIL)
                        {
                            coils.add(position);
                        }
                    }
                    else if (firstFailure == Failure.NONE)
                    {
                        firstFailure = Failure.INVALID_INNER;
                        firstFailurePos = position;
                    }
                }
            }
        }

        if (firstFailure != Failure.NONE)
        {
            return Result.failure(firstFailure, firstFailurePos, matched);
        }
        if (ports.isEmpty())
        {
            return Result.failure(Failure.MISSING_PORT, null, matched);
        }

        Set<SPSPosition> connectedCoils =
              new LinkedHashSet<SPSPosition>();
        for (SPSPosition port : ports)
        {
            ForgeDirection outward = outwardSide(port, min, max);
            if (outward != ForgeDirection.UNKNOWN)
            {
                SPSPosition inward = port.offset(outward.getOpposite());
                if (lookup.get(inward) == SPSComponent.COIL)
                {
                    connectedCoils.add(inward);
                }
            }
        }
        for (SPSPosition coil : coils)
        {
            if (!connectedCoils.contains(coil))
            {
                return Result.failure(Failure.DISCONNECTED_COIL, coil,
                      matched);
            }
        }

        Collections.sort(ports, POSITION_ORDER);
        Collections.sort(coils, POSITION_ORDER);
        return Result.success(min, max, ports, coils, matched);
    }

    private static Set<SPSPosition> candidateMinimums(SPSPosition port)
    {
        Set<SPSPosition> minimums = new LinkedHashSet<SPSPosition>();
        for (int first = 0; first < SIZE; first++)
        {
            for (int second = 0; second < SIZE; second++)
            {
                if (ALLOWED_GRID[first][second] != 2)
                {
                    continue;
                }
                minimums.add(port.add(0, -second, -first));
                minimums.add(port.add(-LAST, -second, -first));
                minimums.add(port.add(-first, 0, -second));
                minimums.add(port.add(-first, -LAST, -second));
                minimums.add(port.add(-first, -second, 0));
                minimums.add(port.add(-first, -second, -LAST));
            }
        }
        return minimums;
    }

    /** 0 ignored, 1 frame, 2 casing/port, 3 inner. */
    static int requirement(int x, int y, int z)
    {
        boolean boundary = x == 0 || x == LAST || y == 0 || y == LAST
              || z == 0 || z == LAST;
        if (!boundary)
        {
            return 3;
        }
        int requirement = 0;
        if (x == 0 || x == LAST)
        {
            requirement = Math.max(requirement, ALLOWED_GRID[z][y]);
        }
        if (y == 0 || y == LAST)
        {
            requirement = Math.max(requirement, ALLOWED_GRID[x][z]);
        }
        if (z == 0 || z == LAST)
        {
            requirement = Math.max(requirement, ALLOWED_GRID[x][y]);
        }
        return requirement;
    }

    private static ForgeDirection outwardSide(SPSPosition position,
          SPSPosition min, SPSPosition max)
    {
        if (position.getX() == min.getX())
        {
            return ForgeDirection.WEST;
        }
        if (position.getX() == max.getX())
        {
            return ForgeDirection.EAST;
        }
        if (position.getY() == min.getY())
        {
            return ForgeDirection.DOWN;
        }
        if (position.getY() == max.getY())
        {
            return ForgeDirection.UP;
        }
        if (position.getZ() == min.getZ())
        {
            return ForgeDirection.NORTH;
        }
        if (position.getZ() == max.getZ())
        {
            return ForgeDirection.SOUTH;
        }
        return ForgeDirection.UNKNOWN;
    }

    private static final Comparator<SPSPosition> POSITION_ORDER =
          new Comparator<SPSPosition>()
          {
              @Override
              public int compare(SPSPosition first, SPSPosition second)
              {
                  int x = Integer.compare(first.getX(), second.getX());
                  if (x != 0)
                  {
                      return x;
                  }
                  int y = Integer.compare(first.getY(), second.getY());
                  return y != 0 ? y
                        : Integer.compare(first.getZ(), second.getZ());
              }
          };

    public interface ComponentLookup
    {
        SPSComponent get(SPSPosition position);
    }

    public enum Failure
    {
        NONE(""),
        NOT_PORT("sps.mekanismnuclear.invalid_not_port"),
        NO_MATCHING_STRUCTURE("sps.mekanismnuclear.invalid_no_structure"),
        UNLOADED("sps.mekanismnuclear.invalid_unloaded"),
        INVALID_FRAME("sps.mekanismnuclear.invalid_frame"),
        INVALID_FACE("sps.mekanismnuclear.invalid_face"),
        INVALID_INNER("sps.mekanismnuclear.invalid_inner"),
        MISSING_PORT("sps.mekanismnuclear.invalid_missing_port"),
        DISCONNECTED_COIL("sps.mekanismnuclear.invalid_disconnected_coil");

        private final String translationKey;

        Failure(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getTranslationKey()
        {
            return translationKey;
        }
    }

    public static final class Result
    {
        private final boolean formed;
        private final Failure failure;
        private final SPSPosition failurePos;
        private final SPSPosition min;
        private final SPSPosition max;
        private final List<SPSPosition> ports;
        private final List<SPSPosition> coils;
        private final int matchedBlocks;

        private Result(boolean formed, Failure failure,
              @Nullable SPSPosition failurePos, @Nullable SPSPosition min,
              @Nullable SPSPosition max, List<SPSPosition> ports,
              List<SPSPosition> coils, int matchedBlocks)
        {
            this.formed = formed;
            this.failure = failure;
            this.failurePos = failurePos;
            this.min = min;
            this.max = max;
            this.ports = ports;
            this.coils = coils;
            this.matchedBlocks = matchedBlocks;
        }

        private static Result failure(Failure failure,
              @Nullable SPSPosition position, int matchedBlocks)
        {
            return new Result(false, failure, position, null, null,
                  Collections.<SPSPosition>emptyList(),
                  Collections.<SPSPosition>emptyList(), matchedBlocks);
        }

        private static Result success(SPSPosition min, SPSPosition max,
              List<SPSPosition> ports, List<SPSPosition> coils,
              int matchedBlocks)
        {
            return new Result(true, Failure.NONE, null, min, max,
                  Collections.unmodifiableList(
                        new ArrayList<SPSPosition>(ports)),
                  Collections.unmodifiableList(
                        new ArrayList<SPSPosition>(coils)), matchedBlocks);
        }

        public boolean isFormed()
        {
            return formed;
        }

        public Failure getFailure()
        {
            return failure;
        }

        @Nullable
        public SPSPosition getFailurePos()
        {
            return failurePos;
        }

        @Nullable
        public SPSPosition getMin()
        {
            return min;
        }

        @Nullable
        public SPSPosition getMax()
        {
            return max;
        }

        public List<SPSPosition> getPorts()
        {
            return ports;
        }

        public List<SPSPosition> getCoils()
        {
            return coils;
        }

        int getMatchedBlocks()
        {
            return matchedBlocks;
        }
    }
}
