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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Old-version-native cuboid validator implementing the stable fission reactor
 * structure contract without pulling the modern multiblock framework into 1.12.
 */
public final class FissionReactorValidator {

    public static final int MIN_SIZE = 3;
    public static final int MAX_SIZE = 18;
    private static final int MAX_COMPONENTS = MAX_SIZE * MAX_SIZE * MAX_SIZE;

    private FissionReactorValidator() {
    }

    public static Result validate(World world, BlockPos port) {
        return validate(port, pos -> world.isBlockLoaded(pos)
              ? FissionReactorComponent.fromState(world.getBlockState(pos))
              : FissionReactorComponent.INVALID);
    }

    public static Result validate(BlockPos port, ComponentLookup lookup) {
        if (lookup.get(port) != FissionReactorComponent.PORT) {
            return Result.failure(Failure.NOT_PORT, port);
        }

        Bounds bounds = discoverBounds(port, lookup);
        if (bounds.tooLarge) {
            return Result.failure(Failure.TOO_LARGE, bounds.failurePos);
        }
        int width = bounds.maxX - bounds.minX + 1;
        int height = bounds.maxY - bounds.minY + 1;
        int length = bounds.maxZ - bounds.minZ + 1;
        if (!validDimension(width) || !validDimension(height) || !validDimension(length)) {
            return Result.failure(Failure.INVALID_SIZE, port);
        }

        Set<BlockPos> ports = new HashSet<>();
        Set<BlockPos> fuelPositions = new HashSet<>();
        Map<ColumnPos, AssemblyColumn> columns = new HashMap<>();
        for (int x = bounds.minX; x <= bounds.maxX; x++) {
            for (int y = bounds.minY; y <= bounds.maxY; y++) {
                for (int z = bounds.minZ; z <= bounds.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    FissionReactorComponent component = lookup.get(pos);
                    int wallCount = boundaryCount(pos, bounds);
                    if (wallCount >= 2) {
                        if (component != FissionReactorComponent.CASING) {
                            return Result.failure(Failure.INVALID_FRAME, pos);
                        }
                    } else if (wallCount == 1) {
                        if (!component.isExterior()) {
                            return Result.failure(Failure.INVALID_FACE, pos);
                        }
                        if (component == FissionReactorComponent.PORT) {
                            ports.add(pos);
                        }
                    } else if (component == FissionReactorComponent.FUEL_ASSEMBLY
                          || component == FissionReactorComponent.CONTROL_ROD) {
                        ColumnPos columnPos = new ColumnPos(x, z);
                        AssemblyColumn column = columns.computeIfAbsent(columnPos, ignored -> new AssemblyColumn());
                        if (component == FissionReactorComponent.FUEL_ASSEMBLY) {
                            column.fuel.put(y, pos);
                            fuelPositions.add(pos);
                        } else if (column.controlRod != null) {
                            return Result.failure(Failure.EXTRA_CONTROL_ROD, pos);
                        } else {
                            column.controlRod = pos;
                        }
                    } else if (component != FissionReactorComponent.AIR) {
                        return Result.failure(Failure.INVALID_INNER, pos);
                    }
                }
            }
        }

        if (columns.isEmpty()) {
            return Result.failure(Failure.MISSING_FUEL_ASSEMBLY, null);
        }

        int controlRods = 0;
        for (AssemblyColumn column : columns.values()) {
            if (column.controlRod == null) {
                BlockPos first = column.fuel.isEmpty() ? null : column.fuel.firstEntry().getValue();
                return Result.failure(Failure.MISSING_CONTROL_ROD, first);
            }
            controlRods++;
            if (column.fuel.isEmpty()) {
                return Result.failure(Failure.BAD_FUEL_ASSEMBLY, column.controlRod);
            }
            int previousY = Integer.MIN_VALUE;
            for (Map.Entry<Integer, BlockPos> entry : column.fuel.entrySet()) {
                if (previousY != Integer.MIN_VALUE && entry.getKey() != previousY + 1) {
                    return Result.failure(Failure.MALFORMED_FUEL_ASSEMBLY, entry.getValue());
                }
                previousY = entry.getKey();
            }
            if (column.controlRod.getY() != previousY + 1) {
                return Result.failure(Failure.BAD_CONTROL_ROD, column.controlRod);
            }
        }

        int surfaceArea = fuelPositions.size() * 6;
        for (BlockPos fuel : fuelPositions) {
            for (EnumFacing side : EnumFacing.VALUES) {
                if (fuelPositions.contains(fuel.offset(side))) {
                    surfaceArea--;
                }
            }
        }
        List<BlockPos> sortedPorts = new ArrayList<>(ports);
        sortedPorts.sort(POSITION_ORDER);
        return Result.success(new BlockPos(bounds.minX, bounds.minY, bounds.minZ),
              new BlockPos(bounds.maxX, bounds.maxY, bounds.maxZ), width, height, length,
              width * height * length, fuelPositions.size(), controlRods, surfaceArea, sortedPorts);
    }

    private static boolean validDimension(int dimension) {
        return dimension >= MIN_SIZE && dimension <= MAX_SIZE;
    }

    private static Bounds discoverBounds(BlockPos start, ComponentLookup lookup) {
        Queue<BlockPos> open = new ArrayDeque<>();
        Set<BlockPos> found = new HashSet<>();
        Bounds bounds = new Bounds(start);
        open.add(start);
        found.add(start);
        while (!open.isEmpty()) {
            BlockPos current = open.remove();
            bounds.include(current);
            if (bounds.width() > MAX_SIZE || bounds.height() > MAX_SIZE || bounds.length() > MAX_SIZE
                  || found.size() > MAX_COMPONENTS) {
                bounds.tooLarge = true;
                bounds.failurePos = current;
                return bounds;
            }
            for (EnumFacing side : EnumFacing.VALUES) {
                BlockPos adjacent = current.offset(side);
                if (!found.contains(adjacent) && lookup.get(adjacent).isExterior()) {
                    found.add(adjacent);
                    open.add(adjacent);
                }
            }
        }
        return bounds;
    }

    private static int boundaryCount(BlockPos pos, Bounds bounds) {
        int count = 0;
        if (pos.getX() == bounds.minX || pos.getX() == bounds.maxX) {
            count++;
        }
        if (pos.getY() == bounds.minY || pos.getY() == bounds.maxY) {
            count++;
        }
        if (pos.getZ() == bounds.minZ || pos.getZ() == bounds.maxZ) {
            count++;
        }
        return count;
    }

    private static final Comparator<BlockPos> POSITION_ORDER = (first, second) -> {
        int x = Integer.compare(first.getX(), second.getX());
        if (x != 0) {
            return x;
        }
        int y = Integer.compare(first.getY(), second.getY());
        return y != 0 ? y : Integer.compare(first.getZ(), second.getZ());
    };

    @FunctionalInterface
    public interface ComponentLookup {

        FissionReactorComponent get(BlockPos pos);
    }

    public enum Failure {
        NONE(""),
        NOT_PORT("fission.mekanismnuclear.invalid_not_port"),
        TOO_LARGE("fission.mekanismnuclear.invalid_too_large"),
        INVALID_SIZE("fission.mekanismnuclear.invalid_size"),
        INVALID_FRAME("fission.mekanismnuclear.invalid_frame"),
        INVALID_FACE("fission.mekanismnuclear.invalid_face"),
        INVALID_INNER("fission.mekanismnuclear.invalid_inner"),
        MISSING_FUEL_ASSEMBLY("fission.mekanismnuclear.invalid_missing_fuel_assembly"),
        EXTRA_CONTROL_ROD("fission.mekanismnuclear.invalid_extra_control_rod"),
        BAD_FUEL_ASSEMBLY("fission.mekanismnuclear.invalid_bad_fuel_assembly"),
        MALFORMED_FUEL_ASSEMBLY("fission.mekanismnuclear.invalid_malformed_fuel_assembly"),
        MISSING_CONTROL_ROD("fission.mekanismnuclear.invalid_missing_control_rod"),
        BAD_CONTROL_ROD("fission.mekanismnuclear.invalid_bad_control_rod");

        private final String translationKey;

        Failure(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }

    public static final class Result {

        private final boolean formed;
        private final Failure failure;
        private final BlockPos failurePos;
        private final BlockPos min;
        private final BlockPos max;
        private final int width;
        private final int height;
        private final int length;
        private final int volume;
        private final int fuelAssemblies;
        private final int controlRods;
        private final int surfaceArea;
        private final List<BlockPos> ports;

        private Result(boolean formed, Failure failure, @Nullable BlockPos failurePos,
              @Nullable BlockPos min, @Nullable BlockPos max, int width, int height, int length,
              int volume, int fuelAssemblies, int controlRods, int surfaceArea, List<BlockPos> ports) {
            this.formed = formed;
            this.failure = failure;
            this.failurePos = failurePos;
            this.min = min;
            this.max = max;
            this.width = width;
            this.height = height;
            this.length = length;
            this.volume = volume;
            this.fuelAssemblies = fuelAssemblies;
            this.controlRods = controlRods;
            this.surfaceArea = surfaceArea;
            this.ports = ports;
        }

        private static Result failure(Failure failure, @Nullable BlockPos pos) {
            return new Result(false, failure, pos, null, null, 0, 0, 0, 0, 0, 0, 0,
                  Collections.emptyList());
        }

        private static Result success(BlockPos min, BlockPos max, int width, int height, int length,
              int volume, int fuelAssemblies, int controlRods, int surfaceArea, List<BlockPos> ports) {
            return new Result(true, Failure.NONE, null, min, max, width, height, length, volume,
                  fuelAssemblies, controlRods, surfaceArea, Collections.unmodifiableList(ports));
        }

        public boolean isFormed() {
            return formed;
        }

        public Failure getFailure() {
            return failure;
        }

        @Nullable
        public BlockPos getFailurePos() {
            return failurePos;
        }

        @Nullable
        public BlockPos getMin() {
            return min;
        }

        @Nullable
        public BlockPos getMax() {
            return max;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getLength() {
            return length;
        }

        public int getVolume() {
            return volume;
        }

        public int getFuelAssemblies() {
            return fuelAssemblies;
        }

        public int getControlRods() {
            return controlRods;
        }

        public int getSurfaceArea() {
            return surfaceArea;
        }

        public List<BlockPos> getPorts() {
            return ports;
        }
    }

    private static final class Bounds {

        private int minX;
        private int minY;
        private int minZ;
        private int maxX;
        private int maxY;
        private int maxZ;
        private boolean tooLarge;
        private BlockPos failurePos;

        private Bounds(BlockPos start) {
            minX = maxX = start.getX();
            minY = maxY = start.getY();
            minZ = maxZ = start.getZ();
        }

        private void include(BlockPos pos) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }

        private int width() {
            return maxX - minX + 1;
        }

        private int height() {
            return maxY - minY + 1;
        }

        private int length() {
            return maxZ - minZ + 1;
        }
    }

    private static final class ColumnPos {

        private final int x;
        private final int z;

        private ColumnPos(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ColumnPos)) {
                return false;
            }
            ColumnPos that = (ColumnPos) other;
            return x == that.x && z == that.z;
        }

        @Override
        public int hashCode() {
            return 31 * x + z;
        }
    }

    private static final class AssemblyColumn {

        private final TreeMap<Integer, BlockPos> fuel = new TreeMap<>();
        private BlockPos controlRod;
    }
}
