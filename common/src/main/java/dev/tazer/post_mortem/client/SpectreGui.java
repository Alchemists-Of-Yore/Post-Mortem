package dev.tazer.post_mortem.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tazer.post_mortem.PostMortem;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpectreGui {
    private static final long FADE_OUT_DELAY = 5000L;
    private static final long FADE_OUT_TIME = 2000L;

    private final Minecraft minecraft;
    private long lastSelectionTime;
    private int heldTicks;
    private int lastHeldTick;
    private float currentY;

    @Nullable
    private SpectreMenu menu;

    public SpectreGui(Minecraft minecraft) {
        this.minecraft = minecraft;
        menu = null;
    }

    public void onHotbarSelected(int slot) {
        lastSelectionTime = Util.getMillis();
        if (menu != null) {
            SpectreMenuItem spectreMenuItem = menu.items.get(slot);
            if (menu.selectedSlot == slot && spectreMenuItem.isEnabled()) {
                heldTicks++;
                if (heldTicks == 20) {
                    spectreMenuItem.selectItem(menu);
                    heldTicks = 0;
                }
            } else heldTicks = 0;

        } else {
            menu = new SpectreMenu();
        }

        menu.selectSlot(slot);
    }

    public void tick() {
        if (heldTicks > 0) {
            if (lastHeldTick == heldTicks) heldTicks--;
            lastHeldTick = heldTicks;
        }
    }

    private float getHotbarAlpha() {
        long delta = lastSelectionTime - Util.getMillis() + FADE_OUT_DELAY;
        return Mth.clamp((float) delta / (float) FADE_OUT_TIME, 0.0F, 1.0F);
    }

    public void renderHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (menu != null) {
            float alpha = getHotbarAlpha();
            if (alpha <= 0) {
                closeMenu();
            } else {
                ResourceLocation background = PostMortem.location("textures/gui/hud/haunting_point_slot.png");
                ResourceLocation selected = PostMortem.location("textures/gui/hud/haunting_point_selector.png");

                float partialTick = deltaTracker.getGameTimeDeltaTicks();
                float currentIndex = menu.currentIndex;
                int y = guiGraphics.guiHeight() - 22;
                int selectedIndex = menu.selectedSlot;
                int centreX = guiGraphics.guiWidth() / 2 - 8;
                int spacing = 22;
                List<SpectreMenuItem> items = menu.items;

                float smoothing = 1 - (float) Math.exp(-5 * partialTick);
                currentIndex = Mth.lerp(smoothing, currentIndex, selectedIndex);
                menu.currentIndex = currentIndex;

                int startX = Mth.floor(centreX - currentIndex * spacing);

                RenderSystem.enableBlend();
                guiGraphics.setColor(1, 1, 1, alpha);

                for (int index = 0; index < items.size(); index++) {
                    int x = startX + index * spacing;
                    items.get(index).renderIcon(guiGraphics, x, y);
                    guiGraphics.blit(background, x, y, 0, 0, 16, 16, 16, 16);
                }

                guiGraphics.blit(selected, centreX - 3, y - 3, 0, 0, 22, 22, 22, 22);
                RenderSystem.disableBlend();
            }
        }
    }

    public void renderTooltip(GuiGraphics guiGraphics) {
        int a = (int) (getHotbarAlpha() * 255.0F);
        if (a > 3 && menu != null) {
            SpectreMenuItem item = menu.getSelectedItem();
            Component text = item.getName();
            if (text != null) {
                int w = minecraft.font.width(text);
                int x = (guiGraphics.guiWidth() - w) / 2;
                int y = guiGraphics.guiHeight() - 35;
                guiGraphics.drawStringWithBackdrop(minecraft.font, text, x, y, w, FastColor.ARGB32.color(a, -1));
            }
        }
    }

    public void closeMenu() {
        this.menu = null;
        lastSelectionTime = 0L;
    }

    public void onMouseScrolled(int amount) {
        if (menu != null) {
            int maxIndex = menu.items.size() - 1;
            int selectedIndex = menu.selectedSlot;
            selectedIndex = selectedIndex + amount;
            if (selectedIndex < 0) selectedIndex = maxIndex;
            if (selectedIndex > maxIndex) selectedIndex = 0;
            menu.selectSlot(selectedIndex);
            lastSelectionTime = Util.getMillis();
        }
    }

    public void onMouseMiddleClick() {
        lastSelectionTime = Util.getMillis();
        if (menu != null) {
            SpectreMenuItem spectreMenuItem = menu.getSelectedItem();
            if (spectreMenuItem.isEnabled()) {
                spectreMenuItem.selectItem(menu);
            }

            heldTicks = 0;
        }
    }
}
