package de.gtmextra.client.mixin;

import de.gtmextra.client.feature.ItemGlow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Replaces the selected dropped item's vanilla nameplate with its grouped count label. */
@Mixin(Entity.class)
abstract class ItemDisplayNameMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void gtmExtra$setSelectedItemDisplayName(CallbackInfoReturnable<Text> callback) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof ItemEntity itemEntity && ItemGlow.shouldShowLabel(entity)) {
            callback.setReturnValue(ItemGlow.getLabel(itemEntity));
        }
    }
}
