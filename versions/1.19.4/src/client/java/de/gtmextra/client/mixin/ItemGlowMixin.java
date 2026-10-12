package de.gtmextra.client.mixin;

import de.gtmextra.client.feature.ItemGlow;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Adds the vanilla outline only to selected dropped items. */
@Mixin(Entity.class)
abstract class ItemGlowMixin {

    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    private void gtmExtra$highlightSelectedItems(CallbackInfoReturnable<Boolean> callback) {
        if (ItemGlow.shouldGlow((Entity) (Object) this)) {
            callback.setReturnValue(true);
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void gtmExtra$setItemHighlightColor(CallbackInfoReturnable<Integer> callback) {
        if (ItemGlow.shouldGlow((Entity) (Object) this)) {
            callback.setReturnValue(ItemGlow.getColor());
        }
    }
}
