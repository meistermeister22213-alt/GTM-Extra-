package de.gtmextra.client.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Exposes the real render-camera position for visibility raycasts. */
@Mixin(Camera.class)
public interface CameraAccessor {

    @Accessor("pos")
    Vec3d gtmExtra$getPosition();
}
