package mekanism.nuclear.common.content.fission;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Old-version-native cuboid validator implementing the stable fission reactor
 * structure contract without importing a modern multiblock framework.
 */
public final class FissionReactorValidator
{
    public static final int MIN_SIZE = 3;
    public static final int MAX_SIZE = 18;
    private static final int MAX_COMPONENTS = MAX_SIZE * MAX_SIZE * MAX_SIZE;

    private FissionReactorValidator()
    {
    }

    public static Result validate(final World world,
          FissionReactorPosition port)
    {
        return validate(port, new ComponentLookup()
        {
            @Override
            public FissionReactorComponent get(FissionReactorPosition position)
            {
                return world.blockExists(position.getX(), position.getY(),
                      position.getZ())
                      ? FissionReactorComponent.fromWorld(world, position)
                      : FissionReactorComponent.INVALID;
            }
        });
    }

    public static Result validate(FissionReactorPosition port,
          ComponentLookup lookup)
    {
        if (lookup.get(port) != FissionReactorComponent.PORT)
        {
            return Result.failure(Failure.NOT_PORT, port);
        }

        Bounds bounds = discoverBounds(port, lookup);
        if (bounds.tooLarge)
        {
            return Result.failure(Failure.TOO_LARGE, bounds.failurePos);
        }
        int width = bounds.maxX - bounds.minX + 1;
        int height = bounds.maxY - bounds.minY + 1;
        int length = bounds.maxZ - bounds.minZ + 1;
        if (!validDimension(width) || !validDimension(height)
              || !validDimension(length))
        {
            return Result.failure(Failure.INVALID_SIZE, port);
        }

        Set<FissionReactorPosition> ports =
              new HashSet<FissionReactorPosition>();
        Set<FissionReactorPosition> logicAdapters =
              new HashSet<FissionReactorPosition>();
        Set<FissionReactorPosition> fuelPositions =
              new HashSet<FissionReactorPosition>();
        Map<ColumnPos, AssemblyColumn> columns =
              new HashMap<ColumnPos, AssemblyColumn>();
        for (int x = bounds.minX; x <= bounds.maxX; x++)
        {
            for (int y = bounds.minY; y <= bounds.maxY; y++)
            {
                for (int z = bounds.minZ; z <= bounds.maxZ; z++)
                {
                    FissionReactorPosition position =
                          new FissionReactorPosition(x, y, z);
                    FissionReactorComponent component = lookup.get(position);
                    int wallCount = boundaryCount(position, bounds);
                    if (wallCount >= 2)
                    {
                        if (component != FissionReactorComponent.CASING)
                        {
                            return Result.failure(Failure.INVALID_FRAME,
                                  position);
                        }
                    }
                    else if (wallCount == 1)
                    {
                        if (!component.isExterior())
                        {
                            return Result.failure(Failure.INVALID_FACE, position);
                        }
                        if (component == FissionReactorComponent.PORT)
                        {
                            ports.add(position);
                        }
                        else if (component == FissionReactorComponent.LOGIC_ADAPTER)
                        {
                            logicAdapters.add(position);
                        }
                    }
                    else if (component == FissionReactorComponent.FUEL_ASSEMBLY
                          || component == FissionReactorComponent.CONTROL_ROD)
                    {
                        ColumnPos columnPos = new ColumnPos(x, z);
                        AssemblyColumn column = columns.get(columnPos);
                        if (column == null)
                        {
                            column = new AssemblyColumn();
                            columns.put(columnPos, column);
                        }
                        if (component == FissionReactorComponent.FUEL_ASSEMBLY)
                        {
                            column.fuel.put(y, position);
                            fuelPositions.add(position);
                        }
                        else if (column.controlRod != null)
                        {
                            return Result.failure(Failure.EXTRA_CONTROL_ROD,
                                  position);
                        }
                        else
                        {
                            column.controlRod = position;
                        }
                    }
                    else if (component != FissionReactorComponent.AIR)
                    {
                        return Result.failure(Failure.INVALID_INNER, position);
                    }
                }
            }
        }

        if (columns.isEmpty())
        {
            return Result.failure(Failure.MISSING_FUEL_ASSEMBLY, null);
        }

        int controlRods = 0;
        for (AssemblyColumn column : columns.values())
        {
            if (column.controlRod == null)
            {
                FissionReactorPosition first = column.fuel.isEmpty() ? null
                      : column.fuel.firstEntry().getValue();
                return Result.failure(Failure.MISSING_CONTROL_ROD, first);
            }
            controlRods++;
            if (column.fuel.isEmpty())
            {
                return Result.failure(Failure.BAD_FUEL_ASSEMBLY,
                      column.controlRod);
            }
            int previousY = Integer.MIN_VALUE;
            for (Map.Entry<Integer, FissionReactorPosition> entry
                  : column.fuel.entrySet())
            {
                if (previousY != Integer.MIN_VALUE
                      && entry.getKey() != previousY + 1)
                {
                    return Result.failure(Failure.MALFORMED_FUEL_ASSEMBLY,
                          entry.getValue());
                }
                previousY = entry.getKey();
            }
            if (column.controlRod.getY() != previousY + 1)
            {
                return Result.failure(Failure.BAD_CONTROL_ROD,
                      column.controlRod);
            }
        }

        int surfaceArea = fuelPositions.size() * 6;
        for (FissionReactorPosition fuel : fuelPositions)
        {
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
            {
                if (fuelPositions.contains(fuel.offset(side)))
                {
                    surfaceArea--;
                }
            }
        }
        List<FissionReactorPosition> sortedPorts =
              new ArrayList<FissionReactorPosition>(ports);
        Collections.sort(sortedPorts, POSITION_ORDER);
        List<FissionReactorPosition> sortedLogicAdapters =
              new ArrayList<FissionReactorPosition>(logicAdapters);
        Collections.sort(sortedLogicAdapters, POSITION_ORDER);
        int volume = width * height * length;
        int interiorVolume = (width - 2) * (height - 2) * (length - 2);
        return Result.success(
              new FissionReactorPosition(bounds.minX, bounds.minY, bounds.minZ),
              new FissionReactorPosition(bounds.maxX, bounds.maxY, bounds.maxZ),
              width, height, length, volume, volume - interiorVolume,
              fuelPositions.size(), controlRods, surfaceArea, sortedPorts,
              sortedLogicAdapters);
    }

    private static boolean validDimension(int dimension)
    {
        return dimension >= MIN_SIZE && dimension <= MAX_SIZE;
    }

    private static Bounds discoverBounds(FissionReactorPosition start,
          ComponentLookup lookup)
    {
        Queue<FissionReactorPosition> open =
              new ArrayDeque<FissionReactorPosition>();
        Set<FissionReactorPosition> found =
              new HashSet<FissionReactorPosition>();
        Bounds bounds = new Bounds(start);
        open.add(start);
        found.add(start);
        while (!open.isEmpty())
        {
            FissionReactorPosition current = open.remove();
            bounds.include(current);
            if (bounds.width() > MAX_SIZE || bounds.height() > MAX_SIZE
                  || bounds.length() > MAX_SIZE
                  || found.size() > MAX_COMPONENTS)
            {
                bounds.tooLarge = true;
                bounds.failurePos = current;
                return bounds;
            }
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
            {
                FissionReactorPosition adjacent = current.offset(side);
                if (!found.contains(adjacent)
                      && lookup.get(adjacent).isExterior())
                {
                    found.add(adjacent);
                    open.add(adjacent);
                }
            }
        }
        return bounds;
    }

    private static int boundaryCount(FissionReactorPosition position,
          Bounds bounds)
    {
        int count = 0;
        if (position.getX() == bounds.minX || position.getX() == bounds.maxX)
        {
            count++;
        }
        if (position.getY() == bounds.minY || position.getY() == bounds.maxY)
        {
            count++;
        }
        if (position.getZ() == bounds.minZ || position.getZ() == bounds.maxZ)
        {
            count++;
        }
        return count;
    }

    private static final Comparator<FissionReactorPosition> POSITION_ORDER =
          new Comparator<FissionReactorPosition>()
          {
              @Override
              public int compare(FissionReactorPosition first,
                    FissionReactorPosition second)
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

    @FunctionalInterface
    public interface ComponentLookup
    {
        FissionReactorComponent get(FissionReactorPosition position);
    }

    public enum Failure
    {
        NONE(""),
        NOT_PORT("fission.mekanismnuclear.invalid_not_port"),
        TOO_LARGE("fission.mekanismnuclear.invalid_too_large"),
        INVALID_SIZE("fission.mekanismnuclear.invalid_size"),
        INVALID_FRAME("fission.mekanismnuclear.invalid_frame"),
        INVALID_FACE("fission.mekanismnuclear.invalid_face"),
        INVALID_INNER("fission.mekanismnuclear.invalid_inner"),
        MISSING_FUEL_ASSEMBLY(
              "fission.mekanismnuclear.invalid_missing_fuel_assembly"),
        EXTRA_CONTROL_ROD("fission.mekanismnuclear.invalid_extra_control_rod"),
        BAD_FUEL_ASSEMBLY("fission.mekanismnuclear.invalid_bad_fuel_assembly"),
        MALFORMED_FUEL_ASSEMBLY(
              "fission.mekanismnuclear.invalid_malformed_fuel_assembly"),
        MISSING_CONTROL_ROD(
              "fission.mekanismnuclear.invalid_missing_control_rod"),
        BAD_CONTROL_ROD("fission.mekanismnuclear.invalid_bad_control_rod");

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
        private final FissionReactorPosition failurePos;
        private final FissionReactorPosition min;
        private final FissionReactorPosition max;
        private final int width;
        private final int height;
        private final int length;
        private final int volume;
        private final int exteriorBlocks;
        private final int fuelAssemblies;
        private final int controlRods;
        private final int surfaceArea;
        private final List<FissionReactorPosition> ports;
        private final List<FissionReactorPosition> logicAdapters;

        private Result(boolean formed, Failure failure,
              @Nullable FissionReactorPosition failurePos,
              @Nullable FissionReactorPosition min,
              @Nullable FissionReactorPosition max, int width, int height,
              int length, int volume, int exteriorBlocks, int fuelAssemblies,
              int controlRods, int surfaceArea,
              List<FissionReactorPosition> ports,
              List<FissionReactorPosition> logicAdapters)
        {
            this.formed = formed;
            this.failure = failure;
            this.failurePos = failurePos;
            this.min = min;
            this.max = max;
            this.width = width;
            this.height = height;
            this.length = length;
            this.volume = volume;
            this.exteriorBlocks = exteriorBlocks;
            this.fuelAssemblies = fuelAssemblies;
            this.controlRods = controlRods;
            this.surfaceArea = surfaceArea;
            this.ports = ports;
            this.logicAdapters = logicAdapters;
        }

        private static Result failure(Failure failure,
              @Nullable FissionReactorPosition position)
        {
            return new Result(false, failure, position, null, null,
                  0, 0, 0, 0, 0, 0, 0, 0,
                  Collections.<FissionReactorPosition>emptyList(),
                  Collections.<FissionReactorPosition>emptyList());
        }

        private static Result success(FissionReactorPosition min,
              FissionReactorPosition max, int width, int height, int length,
              int volume, int exteriorBlocks, int fuelAssemblies,
              int controlRods, int surfaceArea,
              List<FissionReactorPosition> ports,
              List<FissionReactorPosition> logicAdapters)
        {
            return new Result(true, Failure.NONE, null, min, max, width,
                  height, length, volume, exteriorBlocks, fuelAssemblies,
                  controlRods, surfaceArea,
                  Collections.unmodifiableList(ports),
                  Collections.unmodifiableList(logicAdapters));
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
        public FissionReactorPosition getFailurePos()
        {
            return failurePos;
        }

        @Nullable
        public FissionReactorPosition getMin()
        {
            return min;
        }

        @Nullable
        public FissionReactorPosition getMax()
        {
            return max;
        }

        public int getWidth()
        {
            return width;
        }

        public int getHeight()
        {
            return height;
        }

        public int getLength()
        {
            return length;
        }

        public int getVolume()
        {
            return volume;
        }

        public int getExteriorBlocks()
        {
            return exteriorBlocks;
        }

        public int getFuelAssemblies()
        {
            return fuelAssemblies;
        }

        public int getControlRods()
        {
            return controlRods;
        }

        public int getSurfaceArea()
        {
            return surfaceArea;
        }

        public List<FissionReactorPosition> getPorts()
        {
            return ports;
        }

        public List<FissionReactorPosition> getLogicAdapters()
        {
            return logicAdapters;
        }
    }

    private static final class Bounds
    {
        private int minX;
        private int minY;
        private int minZ;
        private int maxX;
        private int maxY;
        private int maxZ;
        private boolean tooLarge;
        private FissionReactorPosition failurePos;

        private Bounds(FissionReactorPosition start)
        {
            minX = maxX = start.getX();
            minY = maxY = start.getY();
            minZ = maxZ = start.getZ();
        }

        private void include(FissionReactorPosition position)
        {
            minX = Math.min(minX, position.getX());
            minY = Math.min(minY, position.getY());
            minZ = Math.min(minZ, position.getZ());
            maxX = Math.max(maxX, position.getX());
            maxY = Math.max(maxY, position.getY());
            maxZ = Math.max(maxZ, position.getZ());
        }

        private int width()
        {
            return maxX - minX + 1;
        }

        private int height()
        {
            return maxY - minY + 1;
        }

        private int length()
        {
            return maxZ - minZ + 1;
        }
    }

    private static final class ColumnPos
    {
        private final int x;
        private final int z;

        private ColumnPos(int x, int z)
        {
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object object)
        {
            if (this == object)
            {
                return true;
            }
            if (!(object instanceof ColumnPos))
            {
                return false;
            }
            ColumnPos other = (ColumnPos)object;
            return x == other.x && z == other.z;
        }

        @Override
        public int hashCode()
        {
            return 31 * x + z;
        }
    }

    private static final class AssemblyColumn
    {
        private final TreeMap<Integer, FissionReactorPosition> fuel =
              new TreeMap<Integer, FissionReactorPosition>();
        private FissionReactorPosition controlRod;
    }
}
