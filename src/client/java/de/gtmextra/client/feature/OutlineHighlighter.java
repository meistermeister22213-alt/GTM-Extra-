package de.gtmextra.client.feature;

import de.gtmextra.client.config.GTMExtraConfig;
import de.gtmextra.client.mixin.CameraAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Adds a player outline only when the camera-to-player-center path contains no blocks. */
public final class OutlineHighlighter {

    private OutlineHighlighter() {
    }

    public static boolean shouldHighlight(Entity entity) {
        if (!(entity instanceof PlayerEntity player) || player.isInvisible() || !GTMExtraConfig.isOutlineHighlighterEnabled()) {
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || entity.getEntityWorld() != client.world || client.getCameraEntity() == null) {
            return false;
        }

        return hasClearLineOfSight(entity);
    }

    /** Uses the same block-occluded camera ray for other client highlighting features. */
    public static boolean hasClearLineOfSight(Entity entity) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || entity.getEntityWorld() != client.world || client.getCameraEntity() == null) {
            return false;
        }
        Vec3d camera = ((CameraAccessor) client.gameRenderer.getCamera()).gtmExtra$getPosition();
        Vec3d target = entity.getBoundingBox().getCenter();
        return !crossesBlock(client, camera, target);
    }

    public static int getColor() {
        return GTMExtraConfig.getOutlineColor();
    }

    private static boolean crossesBlock(MinecraftClient client, Vec3d start, Vec3d end) {
        Vec3d direction = end.subtract(start);
        int x = MathHelper.floor(start.x);
        int y = MathHelper.floor(start.y);
        int z = MathHelper.floor(start.z);
        int endX = MathHelper.floor(end.x);
        int endY = MathHelper.floor(end.y);
        int endZ = MathHelper.floor(end.z);

        double deltaX = direction.x == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / direction.x);
        double deltaY = direction.y == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / direction.y);
        double deltaZ = direction.z == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / direction.z);
        int stepX = direction.x > 0.0 ? 1 : direction.x < 0.0 ? -1 : 0;
        int stepY = direction.y > 0.0 ? 1 : direction.y < 0.0 ? -1 : 0;
        int stepZ = direction.z > 0.0 ? 1 : direction.z < 0.0 ? -1 : 0;
        double maxX = firstBoundary(start.x, x, direction.x, stepX);
        double maxY = firstBoundary(start.y, y, direction.y, stepY);
        double maxZ = firstBoundary(start.z, z, direction.z, stepZ);
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int steps = 0; steps < 512; steps++) {
            pos.set(x, y, z);
            if (!client.world.getBlockState(pos).isAir()) {
                return true;
            }
            if (x == endX && y == endY && z == endZ) {
                return false;
            }

            if (maxX <= maxY && maxX <= maxZ) {
                x += stepX;
                maxX += deltaX;
            } else if (maxY <= maxZ) {
                y += stepY;
                maxY += deltaY;
            } else {
                z += stepZ;
                maxZ += deltaZ;
            }
        }
        return true;
    }

    private static double firstBoundary(double coordinate, int block, double direction, int step) {
        if (step == 0) {
            return Double.POSITIVE_INFINITY;
        }
        double boundary = step > 0 ? block + 1.0 : block;
        return (boundary - coordinate) / direction;
    }
}
