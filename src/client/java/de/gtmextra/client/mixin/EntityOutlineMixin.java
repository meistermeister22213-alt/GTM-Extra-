package de.gtmextra.client.mixin;

import de.gtmextra.client.feature.OutlineHighlighter;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Adds the vanilla outline only while a player has an unobstructed line of sight. */
@Mixin(Entity.class)
abstract class EntityOutlineMixin {

    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    private void gtmExtra$highlightVisiblePlayers(CallbackInfoReturnable<Boolean> callback) {
        if (OutlineHighlighter.shouldHighlight((Entity) (Object) this)) {
            callback.setReturnValue(true);
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void gtmExtra$setHighlightColor(CallbackInfoReturnable<Integer> callback) {
        if (OutlineHighlighter.shouldHighlight((Entity) (Object) this)) {
            callback.setReturnValue(OutlineHighlighter.getColor());
        }
    }
}
