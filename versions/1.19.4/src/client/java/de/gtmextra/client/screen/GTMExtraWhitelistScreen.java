package de.gtmextra.client.screen;

import de.gtmextra.client.config.GTMExtraConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/** Per-item whitelist settings for glow and the count/name label. */
public final class GTMExtraWhitelistScreen extends Screen {

    private static final String[] ITEM_IDS = {
            "jetpack", "katana", "golf_club", "wingsuit", "chain_boots", "kwons_booty_shorts"
    };
    private final Screen parent;

    public GTMExtraWhitelistScreen(Screen parent) {
        super(Text.translatable("gtm_extra.whitelist.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = width / 2 - 190;
        int glowX = width / 2 + 5;
        int nameX = width / 2 + 90;

        for (int index = 0; index < ITEM_IDS.length; index++) {
            String itemId = ITEM_IDS[index];
            int y = 65 + index * 23;
            addDrawableChild(ButtonWidget.builder(optionText("gtm_extra.whitelist.glow", GTMExtraConfig.isItemGlowAllowed(itemId)), button -> {
                        boolean enabled = !GTMExtraConfig.isItemGlowAllowed(itemId);
                        GTMExtraConfig.setItemGlowAllowed(itemId, enabled);
                        button.setMessage(optionText("gtm_extra.whitelist.glow", enabled));
                    })
                    .dimensions(glowX, y, 80, 20)
                    .build());

            addDrawableChild(ButtonWidget.builder(optionText("gtm_extra.whitelist.name", GTMExtraConfig.isItemNameAllowed(itemId)), button -> {
                        boolean enabled = !GTMExtraConfig.isItemNameAllowed(itemId);
                        GTMExtraConfig.setItemNameAllowed(itemId, enabled);
                        button.setMessage(optionText("gtm_extra.whitelist.name", enabled));
                    })
                    .dimensions(nameX, y, 80, 20)
                    .build());
        }

        addDrawableChild(ButtonWidget.builder(Text.translatable("gtm_extra.whitelist.back"), button -> close())
                .dimensions(width / 2 - 100, height - 28, 200, 20)
                .build());
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, deltaTicks);
        drawCentered(matrices, title, width / 2, 20, 0xFFFFFF);
        drawCentered(matrices, Text.translatable("gtm_extra.whitelist.description"), width / 2, 39, 0xA0A0A0);
        drawCentered(matrices, Text.translatable("gtm_extra.whitelist.glow_header"), width / 2 + 45, 51, 0xFFFFFF);
        drawCentered(matrices, Text.translatable("gtm_extra.whitelist.name_header"), width / 2 + 130, 51, 0xFFFFFF);
        int left = width / 2 - 190;
        for (int index = 0; index < ITEM_IDS.length; index++) {
            textRenderer.drawWithShadow(matrices, Text.translatable("gtm_extra.whitelist." + ITEM_IDS[index]),
                    left, 71 + index * 23, 0xFFFFFF);
        }
    }

    private void drawCentered(MatrixStack matrices, Text text, int centerX, int y, int color) {
        textRenderer.drawWithShadow(matrices, text, centerX - textRenderer.getWidth(text) / 2, y, color);
    }

    private static Text optionText(String key, boolean enabled) {
        return Text.translatable(key, Text.literal(enabled ? "On" : "Off"));
    }
}
