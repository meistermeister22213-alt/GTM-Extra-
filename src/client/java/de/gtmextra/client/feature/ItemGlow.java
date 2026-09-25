package de.gtmextra.client.feature;

import de.gtmextra.client.config.GTMExtraConfig;
import de.gtmextra.client.mixin.CameraAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Highlights selected dropped items and gives them a short, color-coded label. */
public final class ItemGlow {

    private static final double LABEL_GROUP_RADIUS = 1.5;
    private static final Map<Integer, Boolean> VISIBILITY_CACHE = new HashMap<>();
    private static final Map<Integer, List<ItemEntity>> LABEL_GROUP_CACHE = new HashMap<>();
    private static World cachedWorld;
    private static long cachedWorldTime = Long.MIN_VALUE;
    private static Vec3d cachedCameraPosition;

    private static final int DARK_RED = 0xAA0000;
    private static final int ORANGE = 0xFFAA00;
    private static final int RED = 0xFF5555;
    private static final int PINK = 0xFF55FF;

    private ItemGlow() {
    }

    public static boolean shouldGlow(Entity entity) {
        return entity instanceof ItemEntity itemEntity
                && GTMExtraConfig.isItemGlowEnabled()
                && isNamedItem(itemEntity.getStack())
                && hasClearLineOfSight(entity);
    }

    public static int getColor() {
        return GTMExtraConfig.getItemGlowColor();
    }

    public static Text getLabel(ItemEntity entity) {
        return makeLabel(getVisibleGroupCount(entity), getItemName(entity.getStack()));
    }

    public static Text getLabel(ItemStack stack) {
        Text itemName = getItemName(stack);
        if (itemName == null) {
            return null;
        }
        return makeLabel(stack.getCount(), itemName);
    }

    /** Only one visible item in a nearby group owns the shared label. */
    public static boolean shouldShowLabel(Entity entity) {
        if (!(entity instanceof ItemEntity itemEntity) || !shouldGlow(entity)) {
            return false;
        }

        refreshFrameCache(MinecraftClient.getInstance());
        return getVisibleGroup(itemEntity).stream().noneMatch(other -> other.getId() < itemEntity.getId());
    }

    private static Text makeLabel(int count, Text itemName) {
        if (itemName == null) {
            return null;
        }
        return Text.literal(count + "x ").setStyle(Style.EMPTY.withColor(0xFFFFFF)).append(itemName);
    }

    private static int getVisibleGroupCount(ItemEntity itemEntity) {
        return getVisibleGroup(itemEntity).stream().mapToInt(other -> other.getStack().getCount()).sum();
    }

    private static List<ItemEntity> getVisibleGroup(ItemEntity itemEntity) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            return List.of(itemEntity);
        }
        refreshFrameCache(client);
        List<ItemEntity> cachedGroup = LABEL_GROUP_CACHE.get(itemEntity.getId());
        if (cachedGroup != null) {
            return cachedGroup;
        }
        double radiusSquared = LABEL_GROUP_RADIUS * LABEL_GROUP_RADIUS;
        List<ItemEntity> nearby = new ArrayList<>(client.world.getEntitiesByClass(ItemEntity.class,
                itemEntity.getBoundingBox().expand(LABEL_GROUP_RADIUS),
                other -> isNamedItem(other.getStack())
                        && other.getStack().isOf(itemEntity.getStack().getItem())
                        && other.squaredDistanceTo(itemEntity) <= radiusSquared
                        && hasClearLineOfSight(other)));
        if (!nearby.contains(itemEntity) && hasClearLineOfSight(itemEntity)) {
            nearby.add(itemEntity);
        }
        LABEL_GROUP_CACHE.put(itemEntity.getId(), nearby);
        return nearby;
    }

    private static boolean hasClearLineOfSight(Entity entity) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || entity.getEntityWorld() != client.world || client.getCameraEntity() == null) {
            return false;
        }
        refreshFrameCache(client);
        return VISIBILITY_CACHE.computeIfAbsent(entity.getId(), ignored -> OutlineHighlighter.hasClearLineOfSight(entity));
    }

    private static void refreshFrameCache(MinecraftClient client) {
        if (client.world == null || client.gameRenderer == null || client.gameRenderer.getCamera() == null) {
            VISIBILITY_CACHE.clear();
            LABEL_GROUP_CACHE.clear();
            cachedWorld = null;
            cachedCameraPosition = null;
            return;
        }
        Vec3d cameraPosition = ((CameraAccessor) client.gameRenderer.getCamera()).gtmExtra$getPosition();
        long worldTime = client.world.getTime();
        if (cachedWorld != client.world || cachedWorldTime != worldTime || !cameraPosition.equals(cachedCameraPosition)) {
            VISIBILITY_CACHE.clear();
            LABEL_GROUP_CACHE.clear();
            cachedWorld = client.world;
            cachedWorldTime = worldTime;
            cachedCameraPosition = cameraPosition;
        }
    }

    private static boolean isNamedItem(ItemStack stack) {
        return getItemName(stack) != null;
    }

    private static Text getItemName(ItemStack stack) {
        if (stack.isOf(Items.GOLDEN_CHESTPLATE)) {
            return styled("Jetpack", DARK_RED, true);
        }
        if (stack.isOf(Items.DIAMOND_SWORD)) {
            return styled("Melee", ORANGE, false);
        }
        if (stack.isOf(Items.ELYTRA)) {
            return styled("Wingsuit", RED, false);
        }
        if (stack.isOf(Items.CHAINMAIL_BOOTS)) {
            return styled("Chain Boots", DARK_RED, true);
        }
        if (stack.isOf(Items.LEATHER_LEGGINGS) && stack.hasEnchantments()) {
            return styled("Kwon's Booty Shorts", PINK, true);
        }
        return null;
    }

    private static Text styled(String value, int color, boolean bold) {
        return Text.literal(value).setStyle(Style.EMPTY.withColor(color).withBold(bold));
    }
}
