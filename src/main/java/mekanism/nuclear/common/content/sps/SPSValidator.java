package mekanism.nuclear.common.content.sps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Validates the stable 7 by 7 by 7 rounded SPS shell without depending on the
 * modern multiblock implementation.
 */
public final class SPSValidator {

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

    private SPSValidator() {
    }

    public static Result validate(World world, BlockPos port) {
        return validate(port, pos -> world.isBlockLoaded(pos)
              ? SPSComponent.fromState(world.getBlockState(pos)) : SPSComponent.UNLOADED);
    }

    public static Result validateAtMin(World world, BlockPos min) {
        return validateAtMin(min, pos -> world.isBlockLoaded(pos)
              ? SPSComponent.fromState(world.getBlockState(pos)) : SPSComponent.UNLOADED);
    }

    /**
     * Finds every fixed-size shell which could contain the selected port and
     * returns the valid one. The closest invalid candidate is retained for a
     * useful formation error when the structure is incomplete.
     */
    public static Result validate(BlockPos port, ComponentLookup lookup) {
        if (lookup.get(port) != SPSComponent.PORT) {
            return Result.failure(Failure.NOT_PORT, port, 0);
        }
        Result best = null;
        for (BlockPos min : candidateMinimums(port)) {
            Result candidate = validateAtMin(min, lookup);
            if (candidate.isFormed()) {
                return candidate;
            }
            if (best == null || candidate.getMatchedBlocks() > best.getMatchedBlocks()) {
                best = candidate;
            }
        }
        return best == null ? Result.failure(Failure.NO_MATCHING_STRUCTURE, port, 0) : best;
    }

    /** Validates one known 7 by 7 by 7 bounding cube. Exposed for deterministic tests. */
    public static Result validateAtMin(BlockPos min, ComponentLookup lookup) {
        BlockPos max = min.add(LAST, LAST, LAST);
        List<BlockPos> ports = new ArrayList<>();
        List<BlockPos> coils = new ArrayList<>();
        Failure firstFailure = Failure.NONE;
        BlockPos firstFailurePos = null;
        int matched = 0;

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {
                    BlockPos pos = min.add(x, y, z);
                    SPSComponent component = lookup.get(pos);
                    if (component == SPSComponent.UNLOADED) {
                        if (firstFailure == Failure.NONE) {
                            firstFailure = Failure.UNLOADED;
                            firstFailurePos = pos;
                        }
                        continue;
                    }

                    int requirement = requirement(x, y, z);
                    if (requirement == 0) {
                        // Stable SPS corners are ignored and may be left open.
                        matched++;
                    } else if (requirement == 1) {
                        if (component == SPSComponent.CASING) {
                            matched++;
                        } else if (firstFailure == Failure.NONE) {
                            firstFailure = Failure.INVALID_FRAME;
                            firstFailurePos = pos;
                        }
                    } else if (requirement == 2) {
                        if (component == SPSComponent.CASING || component == SPSComponent.PORT) {
                            matched++;
                            if (component == SPSComponent.PORT) {
                                ports.add(pos);
                            }
                        } else if (firstFailure == Failure.NONE) {
                            firstFailure = Failure.INVALID_FACE;
                            firstFailurePos = pos;
                        }
                    } else if (component == SPSComponent.AIR || component == SPSComponent.COIL) {
                        matched++;
                        if (component == SPSComponent.COIL) {
                            coils.add(pos);
                        }
                    } else if (firstFailure == Failure.NONE) {
                        firstFailure = Failure.INVALID_INNER;
                        firstFailurePos = pos;
                    }
                }
            }
        }

        if (firstFailure != Failure.NONE) {
            return Result.failure(firstFailure, firstFailurePos, matched);
        }
        if (ports.isEmpty()) {
            return Result.failure(Failure.MISSING_PORT, null, matched);
        }

        Set<BlockPos> connectedCoils = new LinkedHashSet<>();
        for (BlockPos port : ports) {
            EnumFacing outward = outwardSide(port, min, max);
            if (outward != null) {
                BlockPos inward = port.offset(outward.getOpposite());
                if (lookup.get(inward) == SPSComponent.COIL) {
                    connectedCoils.add(inward);
                }
            }
        }
        for (BlockPos coil : coils) {
            if (!connectedCoils.contains(coil)) {
                return Result.failure(Failure.DISCONNECTED_COIL, coil, matched);
            }
        }

        ports.sort(POSITION_ORDER);
        coils.sort(POSITION_ORDER);
        return Result.success(min, max, ports, coils, matched);
    }

    private static Set<BlockPos> candidateMinimums(BlockPos port) {
        Set<BlockPos> minimums = new LinkedHashSet<>();
        for (int first = 0; first < SIZE; first++) {
            for (int second = 0; second < SIZE; second++) {
                if (ALLOWED_GRID[first][second] != 2) {
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
    static int requirement(int x, int y, int z) {
        boolean boundary = x == 0 || x == LAST || y == 0 || y == LAST || z == 0 || z == LAST;
        if (!boundary) {
            return 3;
        }
        int requirement = 0;
        if (x == 0 || x == LAST) {
            requirement = Math.max(requirement, ALLOWED_GRID[z][y]);
        }
        if (y == 0 || y == LAST) {
            requirement = Math.max(requirement, ALLOWED_GRID[x][z]);
        }
        if (z == 0 || z == LAST) {
            requirement = Math.max(requirement, ALLOWED_GRID[x][y]);
        }
        return requirement;
    }

    @Nullable
    private static EnumFacing outwardSide(BlockPos pos, BlockPos min, BlockPos max) {
        if (pos.getX() == min.getX()) {
            return EnumFacing.WEST;
        } else if (pos.getX() == max.getX()) {
            return EnumFacing.EAST;
        } else if (pos.getY() == min.getY()) {
            return EnumFacing.DOWN;
        } else if (pos.getY() == max.getY()) {
            return EnumFacing.UP;
        } else if (pos.getZ() == min.getZ()) {
            return EnumFacing.NORTH;
        } else if (pos.getZ() == max.getZ()) {
            return EnumFacing.SOUTH;
        }
        return null;
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

        SPSComponent get(BlockPos pos);
    }

    public enum Failure {
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
        private final List<BlockPos> ports;
        private final List<BlockPos> coils;
        private final int matchedBlocks;

        private Result(boolean formed, Failure failure, @Nullable BlockPos failurePos,
              @Nullable BlockPos min, @Nullable BlockPos max, List<BlockPos> ports,
              List<BlockPos> coils, int matchedBlocks) {
            this.formed = formed;
            this.failure = failure;
            this.failurePos = failurePos;
            this.min = min;
            this.max = max;
            this.ports = ports;
            this.coils = coils;
            this.matchedBlocks = matchedBlocks;
        }

        private static Result failure(Failure failure, @Nullable BlockPos pos, int matchedBlocks) {
            return new Result(false, failure, pos, null, null, Collections.emptyList(),
                  Collections.emptyList(), matchedBlocks);
        }

        private static Result success(BlockPos min, BlockPos max, List<BlockPos> ports,
              List<BlockPos> coils, int matchedBlocks) {
            return new Result(true, Failure.NONE, null, min, max,
                  Collections.unmodifiableList(new ArrayList<>(ports)),
                  Collections.unmodifiableList(new ArrayList<>(coils)), matchedBlocks);
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

        public List<BlockPos> getPorts() {
            return ports;
        }

        public List<BlockPos> getCoils() {
            return coils;
        }

        int getMatchedBlocks() {
            return matchedBlocks;
        }
    }
}
