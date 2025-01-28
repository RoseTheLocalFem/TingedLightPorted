package tfc.tingedlights.util;

import net.minecraft.core.Direction;

import java.util.Arrays;

public enum BetterAdjacencyInfo {
    DOWN(AdjacencyInfo.DOWN, Direction.DOWN),
    UP(AdjacencyInfo.UP, Direction.UP),
    NORTH(AdjacencyInfo.NORTH, Direction.NORTH),
    SOUTH(AdjacencyInfo.SOUTH, Direction.SOUTH),
    WEST(AdjacencyInfo.WEST, Direction.WEST),
    EAST(AdjacencyInfo.EAST, Direction.EAST),
    ;

    public Direction[] edges = new Direction[4];

    private static final BetterAdjacencyInfo[] valuesCache = values();

    public static BetterAdjacencyInfo get(AdjacencyInfo info) {
        BetterAdjacencyInfo inf = valuesCache[info.ordinal()];
//        inf.compute();
        return inf;
    }

    AdjacencyInfo fromFacing;
    Direction facing;

    protected void compute() {
        if (facing.equals(Direction.UP)) {
            edges = new Direction[]{
                    fromFacing.corners[1],
                    fromFacing.corners[0],
                    fromFacing.corners[3],
                    fromFacing.corners[2],
            };
        } else if (facing.equals(Direction.DOWN)) {
            edges = new Direction[]{
                    fromFacing.corners[0],
                    fromFacing.corners[1],
                    fromFacing.corners[3],
                    fromFacing.corners[2],
            };
        } else if (facing.equals(Direction.EAST)) {
            edges = new Direction[]{
                    fromFacing.corners[1],
                    fromFacing.corners[0],
                    fromFacing.corners[2],
                    fromFacing.corners[3],
            };
        } else if (facing.equals(Direction.WEST)) {
            edges = new Direction[]{
                    fromFacing.corners[1],
                    fromFacing.corners[0],
                    fromFacing.corners[3],
                    fromFacing.corners[2],
            };
        } else if (facing.equals(Direction.NORTH)) {
            edges = new Direction[]{
                    fromFacing.corners[1],
                    fromFacing.corners[0],
                    fromFacing.corners[2],
                    fromFacing.corners[3],
            };
        } else if (facing.equals(Direction.SOUTH)) {
            edges = new Direction[]{
                    fromFacing.corners[1],
                    fromFacing.corners[0],
                    fromFacing.corners[3],
                    fromFacing.corners[2],
            };
        }
    }

    BetterAdjacencyInfo(AdjacencyInfo fromFacing, Direction facing) {
        Arrays.fill(edges, Direction.NORTH);
        this.fromFacing = fromFacing;
        this.facing = facing;
        compute();
    }

    public enum AdjacencyInfo {
        DOWN(new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}),
        UP(new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.WEST, Direction.EAST}),
        SOUTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.WEST, Direction.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}),
        EAST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH});

        public final Direction[] corners;

        AdjacencyInfo(Direction[] corners) {
            this.corners = corners;
        }
    }
}
