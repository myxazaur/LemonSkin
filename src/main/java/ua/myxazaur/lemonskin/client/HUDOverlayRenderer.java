package ua.myxazaur.lemonskin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.helpers.HungerHelper;

import java.util.Random;

@SideOnly(Side.CLIENT)
public final class HUDOverlayRenderer {

    @Deprecated
    private static int updateCounter = 0;

    /** @deprecated use {@link #drawSaturationOverlay(float, float, Minecraft, int, int, float, int)} */
    @Deprecated
    public static void drawSaturationOverlay(float saturationGained, float saturationLevel,
                                             Minecraft mc, int left, int top, float alpha) {
        drawSaturationOverlay(saturationGained, saturationLevel, mc, left, top, alpha, updateCounter);
    }

    public static void drawSaturationOverlay(float saturationGained, float saturationLevel,
                                             Minecraft mc, int left, int top, float alpha,
                                             int _updateCounter) {
        if (saturationLevel + saturationGained < 0) return;

        ModConfig.RGB color = ModConfig.CLIENT.getColor();
        int startBar = saturationGained != 0 ? Math.max(0, (int) saturationLevel / 2) : 0;
        int endBar   = (int) Math.ceil(Math.min(20, saturationLevel + saturationGained) / 2f);
        int barsNeeded = endBar - startBar;

        mc.getTextureManager().bindTexture(ModConfig.CLIENT.getIcons());

        Random rand = new Random((long) _updateCounter * 312871L);
        boolean shouldShake = mc.player.getFoodStats().getSaturationLevel() <= 0.0F
                && _updateCounter % (mc.player.getFoodStats().getFoodLevel() * 3 + 1) == 0
                && ModConfig.CLIENT.SHOW_VANILLA_ANIMATION_OVERLAY;

        if (shouldShake) {
            for (int j = 0; j < startBar; j++) rand.nextInt(3);
        }

        enableAlpha(alpha);
        GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), alpha);

        for (int i = startBar; i < startBar + barsNeeded; i++) {
            int x = left - i * 8 - 9;
            int y = shouldShake ? top + (rand.nextInt(3) - 1) : top;

            float effective = (saturationLevel + saturationGained) / 2 - i;
            int u;
            if (effective >= 1)        u = 27;
            else if (effective > .5)   u = 18;
            else if (effective > .25)  u = 9;
            else                       u = 0;

            mc.ingameGUI.drawTexturedModalRect(x, y, u, 0, 9, 9);
        }

        GlStateManager.color(1f, 1f, 1f, alpha);
        disableAlpha(alpha);
        mc.getTextureManager().bindTexture(Gui.ICONS);
    }

    /* ============================================================
     *  HUNGER OVERLAY
     * ============================================================ */

    /** @deprecated use {@link #drawHungerOverlay(int, int, Minecraft, int, int, float, boolean, int)} */
    @Deprecated
    public static void drawHungerOverlay(int hungerRestored, int foodLevel,
                                         Minecraft mc, int left, int top, float alpha, boolean isRotten) {
        drawHungerOverlay(hungerRestored, foodLevel, mc, left, top, alpha, isRotten, updateCounter);
    }

    public static void drawHungerOverlay(int hungerRestored, int foodLevel,
                                         Minecraft mc, int left, int top, float alpha,
                                         boolean isRotten, int _updateCounter) {
        if (hungerRestored == 0) return;

        int startBar = foodLevel / 2;
        int endBar   = (int) Math.ceil(Math.min(20, foodLevel + hungerRestored) / 2f);
        int barsNeeded = endBar - startBar;

        mc.getTextureManager().bindTexture(Gui.ICONS);
        enableAlpha(alpha);

        Random rand = new Random((long) _updateCounter * 312871L);
        boolean shouldShake = mc.player.getFoodStats().getSaturationLevel() <= 0.0F
                && _updateCounter % (foodLevel * 3 + 1) == 0
                && ModConfig.CLIENT.SHOW_VANILLA_ANIMATION_OVERLAY;

        if (shouldShake) {
            for (int j = 0; j < startBar; j++) rand.nextInt(3);
        }

        for (int i = startBar; i < startBar + barsNeeded; i++) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = shouldShake ? top + (rand.nextInt(3) - 1) : top;

            int icon = 16;
            byte background = 0;
            if (mc.player.isPotionActive(MobEffects.HUNGER) || isRotten) {
                icon += 36;
                background = 13;
            }

            mc.ingameGUI.drawTexturedModalRect(x, y, 16 + background * 9, 27, 9, 9);

            if (idx < foodLevel + hungerRestored)
                mc.ingameGUI.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
            else if (idx == foodLevel + hungerRestored)
                mc.ingameGUI.drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
        }
        disableAlpha(alpha);
    }

    /* ============================================================
     *  HEALTH OVERLAY
     * ============================================================ */

    /** @deprecated use {@link #drawHealthOverlay(float, float, Minecraft, int, int, float, int)} */
    @Deprecated
    public static void drawHealthOverlay(float current, float modified,
                                         Minecraft mc, int left, int top, float alpha) {
        drawHealthOverlay(current, modified, mc, left, top, alpha, updateCounter);
    }

    public static void drawHealthOverlay(float current, float modified,
                                         Minecraft mc, int left, int top, float alpha,
                                         int _updateCounter) {
        if (modified <= current) return;

        mc.getTextureManager().bindTexture(Gui.ICONS);
        EntityPlayer player = mc.player;
        int healthTarget = MathHelper.ceil(modified);

        boolean hardcore = mc.world.getWorldInfo().isHardcoreModeEnabled();
        int topOffset = hardcore ? 45 : 0;

        int margin = 16;
        if (player.isPotionActive(MobEffects.POISON))      margin += 36;
        else if (player.isPotionActive(MobEffects.WITHER)) margin += 72;

        enableAlpha(alpha);

        int start = (int) Math.max(0, Math.ceil(current) / 2.0F);
        int end   = (int) Math.max(0, Math.ceil(modified / 2.0F));

        float healthMax = player.getMaxHealth();
        float absorb    = player.getAbsorptionAmount();
        int healthBars  = MathHelper.ceil((healthMax + absorb) / 2.0F);
        int healthRows  = MathHelper.ceil((float) healthBars / 10.0F);
        int rowHeight   = Math.max(10 - (healthRows - 2), 3);

        boolean shouldShake = current <= 4 && ModConfig.CLIENT.SHOW_VANILLA_ANIMATION_OVERLAY;

        for (int i = start; i < end; i++) {
            int rowIndex = (int) Math.ceil((float) (i + 1) / 10.0F) - 1;
            int x = left + (i % 10) * 8;
            int y = top - (rowIndex * rowHeight);

            if (shouldShake) {
                Random rand = new Random((long) _updateCounter * 312871L);
                int skips = healthBars - 1 - i;
                for (int j = 0; j < skips; j++) rand.nextInt(2);
                y += rand.nextInt(2);
            }

            mc.ingameGUI.drawTexturedModalRect(x, y, 16, topOffset, 9, 9); // background

            boolean isHalf = (i * 2 + 1) == healthTarget;
            int u = isHalf ? margin + 45 : margin + 36;
            mc.ingameGUI.drawTexturedModalRect(x, y, u, topOffset, 9, 9); // heart
        }
        disableAlpha(alpha);
    }

    public static void drawExhaustionOverlay(float exhaustion, Minecraft mc, int left, int top, float alpha) {
        mc.getTextureManager().bindTexture(ModConfig.CLIENT.getIcons());
        float maxExhaustion = HungerHelper.getMaxExhaustion(mc.player);
        float ratio = Math.min(1, Math.max(0, exhaustion / maxExhaustion));
        int width = (int) (ratio * 81);

        enableAlpha(0.75f);
        mc.ingameGUI.drawTexturedModalRect(left - width, top, 81 - width, 18, width, 9);
        disableAlpha(0.75f);
        mc.getTextureManager().bindTexture(Gui.ICONS);
    }

    public static boolean shouldShowEstimatedHealth(EntityPlayer player) {
        if (!ModConfig.CLIENT.SHOW_FOOD_HEALTH_HUD_OVERLAY) return false;
        if (player.world.getDifficulty() == EnumDifficulty.PEACEFUL) return false;

        FoodStats stats = player.getFoodStats();
        if (stats.getFoodLevel() >= 18) return false;
        if (player.isPotionActive(MobEffects.POISON)) return false;
        if (player.isPotionActive(MobEffects.WITHER)) return false;
        if (player.isPotionActive(MobEffects.REGENERATION)) return false;

        return true;
    }

    /* ----------------- helpers ----------------- */

    private static void enableAlpha(float alpha) {
        GlStateManager.enableBlend();
        if (alpha == 1f) return;
        GlStateManager.color(1, 1, 1, alpha);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private static void disableAlpha(float alpha) {
        GlStateManager.disableBlend();
        if (alpha == 1f) return;
        GlStateManager.color(1, 1, 1, 1);
    }
}