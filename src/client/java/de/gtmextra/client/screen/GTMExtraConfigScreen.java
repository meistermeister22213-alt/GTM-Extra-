package de.gtmextra.client.screen;

import de.gtmextra.client.config.GTMExtraConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.Locale;

/**
 * Starting point for the ModMenu configuration UI.
 * Add future setting controls in {@link #init()} and persist them in a dedicated config class.
 */
public final class GTMExtraConfigScreen extends Screen {

    private final Screen parent;
    private ButtonWidget sneakToggle;
    private ButtonWidget outlineToggle;
    private TextFieldWidget hexField;
    private float hue;
    private float saturation;
    private float brightness;

    private int pickerX;
    private int pickerY;
    private static final int PICKER_WIDTH = 150;
    private static final int PICKER_HEIGHT = 100;
    private static final int HUE_WIDTH = 12;

    public GTMExtraConfigScreen(Screen parent) {
        super(Text.translatable("gtm_extra.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        updateColorState(GTMExtraConfig.getOutlineColor());
        pickerX = width / 2 - 75;
        pickerY = 115;

        sneakToggle = addDrawableChild(ButtonWidget.builder(plainToggleText("gtm_extra.config.sneak", GTMExtraConfig.isSneakAnimationEnabled()), button -> {
                    GTMExtraConfig.setSneakAnimationEnabled(!GTMExtraConfig.isSneakAnimationEnabled());
                    button.setMessage(plainToggleText("gtm_extra.config.sneak", GTMExtraConfig.isSneakAnimationEnabled()));
                })
                .dimensions(width / 2 - 100, 55, 200, 20)
                .build());
        outlineToggle = addDrawableChild(ButtonWidget.builder(plainToggleText("gtm_extra.config.outline", GTMExtraConfig.isOutlineHighlighterEnabled()), button -> {
                    GTMExtraConfig.setOutlineHighlighterEnabled(!GTMExtraConfig.isOutlineHighlighterEnabled());
                    button.setMessage(plainToggleText("gtm_extra.config.outline", GTMExtraConfig.isOutlineHighlighterEnabled()));
                })
                .dimensions(width / 2 - 100, 85, 200, 20)
                .build());

        hexField = addDrawableChild(new TextFieldWidget(textRenderer, pickerX, pickerY + PICKER_HEIGHT + 14, PICKER_WIDTH + HUE_WIDTH + 10, 20,
                Text.translatable("gtm_extra.config.hex")));
        hexField.setMaxLength(7);
        hexField.setText(formatHex(GTMExtraConfig.getOutlineColor()));
        hexField.setChangedListener(this::updateColorFromHex);

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> close())
                .dimensions(width / 2 - 100, height - 28, 200, 20)
                .build());
    }

    @Override
    public void close() {
        GTMExtraConfig.save();
        client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("gtm_extra.config.sneak.description"), width / 2, 42, 0xA0A0A0);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("gtm_extra.config.outline.description"), width / 2, 107, 0xA0A0A0);
        renderColorPicker(context);
        context.drawTextWithShadow(textRenderer, Text.translatable("gtm_extra.config.color"), pickerX, pickerY - 12, 0xFFFFFF);
        Text creator = Text.literal("Made By \"Yuq0\"");
        context.drawTextWithShadow(textRenderer, creator, width - textRenderer.getWidth(creator) - 8, height - 14, 0xA0A0A0);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0 && updatePicker((int) click.x(), (int) click.y())) {
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (click.button() == 0 && updatePicker((int) click.x(), (int) click.y())) {
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    private void renderColorPicker(DrawContext context) {
        int hueColor = hsvToRgb(hue, 1.0F, 1.0F);
        for (int index = 0; index < PICKER_WIDTH; index++) {
            float mix = (float) index / (PICKER_WIDTH - 1);
            int topColor = mixColors(0xFFFFFF, hueColor, mix);
            context.fillGradient(pickerX + index, pickerY, pickerX + index + 1, pickerY + PICKER_HEIGHT,
                    0xFF000000 | topColor, 0xFF000000);
        }

        int hueX = pickerX + PICKER_WIDTH + 5;
        for (int index = 0; index < PICKER_HEIGHT; index++) {
            float currentHue = 1.0F - (float) index / (PICKER_HEIGHT - 1);
            context.fill(hueX, pickerY + index, hueX + HUE_WIDTH, pickerY + index + 1, 0xFF000000 | hsvToRgb(currentHue, 1.0F, 1.0F));
        }

        int colorX = pickerX + Math.round(saturation * (PICKER_WIDTH - 1));
        int colorY = pickerY + Math.round((1.0F - brightness) * (PICKER_HEIGHT - 1));
        drawBorder(context, colorX - 2, colorY - 2, 4, 4, 0xFFFFFFFF);
        int hueMarkerY = pickerY + Math.round((1.0F - hue) * (PICKER_HEIGHT - 1));
        drawBorder(context, hueX - 1, hueMarkerY - 1, HUE_WIDTH + 2, 3, 0xFFFFFFFF);
    }

    private boolean updatePicker(int mouseX, int mouseY) {
        if (mouseX >= pickerX && mouseX <= pickerX + PICKER_WIDTH && mouseY >= pickerY && mouseY <= pickerY + PICKER_HEIGHT) {
            saturation = clamp((float) (mouseX - pickerX) / (PICKER_WIDTH - 1));
            brightness = clamp(1.0F - (float) (mouseY - pickerY) / (PICKER_HEIGHT - 1));
            applyPickerColor();
            return true;
        }

        int hueX = pickerX + PICKER_WIDTH + 5;
        if (mouseX >= hueX && mouseX <= hueX + HUE_WIDTH && mouseY >= pickerY && mouseY <= pickerY + PICKER_HEIGHT) {
            hue = clamp(1.0F - (float) (mouseY - pickerY) / (PICKER_HEIGHT - 1));
            applyPickerColor();
            return true;
        }
        return false;
    }

    private void applyPickerColor() {
        int color = hsvToRgb(hue, saturation, brightness);
        GTMExtraConfig.setOutlineColor(color);
        hexField.setText(formatHex(color));
    }

    private void updateColorFromHex(String value) {
        String hex = value.startsWith("#") ? value.substring(1) : value;
        if (hex.length() != 6 || !hex.matches("[0-9a-fA-F]{6}")) {
            return;
        }

        int color = Integer.parseInt(hex, 16);
        GTMExtraConfig.setOutlineColor(color);
        updateColorState(color);
    }

    private void updateColorState(int rgb) {
        float red = ((rgb >> 16) & 0xFF) / 255.0F;
        float green = ((rgb >> 8) & 0xFF) / 255.0F;
        float blue = (rgb & 0xFF) / 255.0F;
        float maximum = Math.max(red, Math.max(green, blue));
        float minimum = Math.min(red, Math.min(green, blue));
        float difference = maximum - minimum;
        brightness = maximum;
        saturation = maximum == 0.0F ? 0.0F : difference / maximum;
        if (difference == 0.0F) {
            hue = 0.0F;
        } else if (maximum == red) {
            hue = ((green - blue) / difference + (green < blue ? 6.0F : 0.0F)) / 6.0F;
        } else if (maximum == green) {
            hue = ((blue - red) / difference + 2.0F) / 6.0F;
        } else {
            hue = ((red - green) / difference + 4.0F) / 6.0F;
        }
    }

    private static Text plainToggleText(String key, boolean enabled) {
        return Text.translatable(key, Text.literal(enabled ? "On" : "Off"));
    }

    private static String formatHex(int color) {
        return String.format(Locale.ROOT, "#%06X", color);
    }

    private static int hsvToRgb(float hue, float saturation, float brightness) {
        return java.awt.Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF;
    }

    private static int mixColors(int first, int second, float progress) {
        int red = Math.round(((first >> 16) & 0xFF) * (1.0F - progress) + ((second >> 16) & 0xFF) * progress);
        int green = Math.round(((first >> 8) & 0xFF) * (1.0F - progress) + ((second >> 8) & 0xFF) * progress);
        int blue = Math.round((first & 0xFF) * (1.0F - progress) + (second & 0xFF) * progress);
        return red << 16 | green << 8 | blue;
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static void drawBorder(DrawContext context, int x, int y, int borderWidth, int borderHeight, int color) {
        context.fill(x, y, x + borderWidth, y + 1, color);
        context.fill(x, y + borderHeight - 1, x + borderWidth, y + borderHeight, color);
        context.fill(x, y, x + 1, y + borderHeight, color);
        context.fill(x + borderWidth - 1, y, x + borderWidth, y + borderHeight, color);
    }
}
