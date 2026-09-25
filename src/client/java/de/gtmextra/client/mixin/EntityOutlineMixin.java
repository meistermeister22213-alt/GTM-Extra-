package de.gtmextra.client.mixin;

import de.gtmextra.client.feature.OutlineHighlighter;
import de.gtmextra.client.feature.ItemGlow;
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
        Entity entity = (Entity) (Object) this;
        if (OutlineHighlighter.shouldHighlight(entity) || ItemGlow.shouldGlow(entity)) {
            callback.setReturnValue(true);
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void gtmExtra$setHighlightColor(CallbackInfoReturnable<Integer> callback) {
        Entity entity = (Entity) (Object) this;
        if (OutlineHighlighter.shouldHighlight(entity)) {
            callback.setReturnValue(OutlineHighlighter.getColor());
        } else if (ItemGlow.shouldGlow(entity)) {
            callback.setReturnValue(ItemGlow.getColor());
        }
    }
}
