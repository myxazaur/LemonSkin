package ua.myxazaur.lemonskin.client.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ua.myxazaur.lemonskin.ModConfig;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class MantleHealthOverlayRenderer
{
    private static final ResourceLocation MANTLE_HEARTS = new ResourceLocation("mantle", "textures/gui/hearts.png");
    private static final ResourceLocation VANILLA_ICONS = Gui.ICONS;

    public static void drawHealthOverlay(
            float currentHealth,
            float newHealth,
            Minecraft mc,
            int left,
            int top,
            float alpha,
            int updateCounter
    ) {
        if (newHealth <= currentHealth) return;

        EntityPlayer player = mc.player;
        if (player == null) return;

        int currentTier = getTier(currentHealth);
        int newTier = getTier(newHealth);

        int regen = player.isPotionActive(MobEffects.REGENERATION) ? updateCounter % 25 : -1;
        int potionOffset = getPotionOffset(player, mc);
        int healthBars = MathHelper.ceil(Math.min(player.getMaxHealth(), 20f) / 2.0F);
        boolean shouldShake = currentHealth <= 4 && ModConfig.CLIENT.SHOW_VANILLA_ANIMATION_OVERLAY;

        enableAlpha(alpha);

        for (int tier = currentTier; tier <= newTier; tier++) {
            drawTierOverlay(mc, player, left, top, tier, currentHealth, newHealth,
                    regen, potionOffset, healthBars, shouldShake, updateCounter);
        }

        disableAlpha();
        mc.getTextureManager().bindTexture(VANILLA_ICONS);
    }

    private static void drawTierOverlay(
            Minecraft mc, EntityPlayer player, int left, int top,
            int tier, float currentHealth, float newHealth,
            int regen, int potionOffset, int healthBars, boolean shouldShake, int updateCounter
    ) {
        int tierStart = tier * 10;
        int tierEnd = (tier + 1) * 10;

        int firstHeart = Math.max(MathHelper.floor(currentHealth / 2.0F), tierStart);
        int lastHeart = Math.min(MathHelper.ceil(newHealth / 2.0F), tierEnd);

        if (lastHeart <= firstHeart) return;

        mc.getTextureManager().bindTexture(tier == 0 ? VANILLA_ICONS : MANTLE_HEARTS);

        for (int heart = firstHeart; heart < lastHeart; heart++) {
            int i = heart % 10;
            int x = left + i * 8;
            int y = top;

            if (tier == 0 && shouldShake) {
                Random rand = new Random(updateCounter * 312871L);
                for (int j = healthBars - 1; j > i; j--) rand.nextInt(2);
                y += rand.nextInt(2);
            }

            if (i == regen) y -= 2;

            boolean isHalf = newHealth < (heart + 1) * 2;

            if (tier == 0) {
                drawVanillaHeart(mc, x, y, isHalf, player);
            } else {
                drawMantleHeart(mc, x, y, isHalf, (tier - 1) % 11, potionOffset);
            }
        }
    }

    private static int getTier(float health) {
        return health <= 0 ? 0 : (int) ((health - 1) / 20);
    }

    private static void drawVanillaHeart(Minecraft mc, int x, int y, boolean isHalf, EntityPlayer player) {
        int topOffset = mc.world.getWorldInfo().isHardcoreModeEnabled() ? 45 : 0;
        int margin = 16;
        if (player.isPotionActive(MobEffects.POISON)) margin += 36;
        else if (player.isPotionActive(MobEffects.WITHER)) margin += 72;

        mc.ingameGUI.drawTexturedModalRect(x, y, margin + (isHalf ? 45 : 36), topOffset, 9, 9);
    }

    private static void drawMantleHeart(Minecraft mc, int x, int y, boolean isHalf, int colorIndex, int potionOffset) {
        mc.ingameGUI.drawTexturedModalRect(x, y, colorIndex * 18 + (isHalf ? 9 : 0), potionOffset, 9, 9);
    }

    private static int getPotionOffset(EntityPlayer player, Minecraft mc) {
        int offset = 0;
        if (player.isPotionActive(MobEffects.WITHER)) offset = 18;
        if (player.isPotionActive(MobEffects.POISON)) offset = 9;
        if (mc.world.getWorldInfo().isHardcoreModeEnabled()) offset += 27;
        return offset;
    }

    private static void enableAlpha(float alpha) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1f, 1f, 1f, alpha);
    }

    private static void disableAlpha() {
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableBlend();
    }
}