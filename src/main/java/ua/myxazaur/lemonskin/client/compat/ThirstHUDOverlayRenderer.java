package ua.myxazaur.lemonskin.client.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.helpers.SimpleDifficultyHelper;
import ua.myxazaur.lemonskin.helpers.ThirstHelper;

import java.util.Random;

@SideOnly(Side.CLIENT)
public final class ThirstHUDOverlayRenderer
{
    private static final ResourceLocation SD_ICONS = new ResourceLocation("simpledifficulty", "textures/gui/icons.png");
    private static final ResourceLocation SD_THIRSTHUD = new ResourceLocation("simpledifficulty", "textures/gui/thirsthud.png");

    private static final int ICON_SIZE = 9;

    private static final Random rand = new Random();

    public static ResourceLocation getSDTexture()
    {
        return SimpleDifficultyHelper.useClassicHUD() ? SD_ICONS : SD_THIRSTHUD;
    }

    public static void drawThirstExhaustionOverlay(float exhaustion, Minecraft mc, int left, int top, float alpha)
    {
        mc.getTextureManager().bindTexture(ModConfig.CLIENT.getIcons());

        float maxExhaustion = ThirstHelper.getMaxExhaustion(mc.player);
        float ratio = Math.min(1, Math.max(0, exhaustion / maxExhaustion));
        int width = (int) (ratio * 81);

        enableAlpha(0.75f);
        mc.ingameGUI.drawTexturedModalRect(left - width, top, 81 - width, 18, width, 9);
        disableAlpha();

        mc.getTextureManager().bindTexture(getSDTexture());
    }

    public static void drawThirstOverlay(int thirstRestored, int thirstLevel,
                                         Minecraft mc, int left, int top, float alpha,
                                         boolean isDirty, int updateCounter)
    {
        if (thirstRestored == 0) return;

        int startBar = thirstLevel / 2;
        int endBar = (int) Math.ceil(Math.min(20, thirstLevel + thirstRestored) / 2f);
        int barsNeeded = endBar - startBar;

        if (barsNeeded <= 0) return;

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(getSDTexture());

        // Shake logic (same seed as SD's ThirstGui: updateCounter * 445)
        rand.setSeed(updateCounter * 445L);
        float saturation = ThirstHelper.getSaturation(mc.player);
        boolean shouldShake = saturation <= 0.0F
                && updateCounter % (thirstLevel * 3 + 1) == 0
                && ModConfig.CLIENT.SHOW_VANILLA_ANIMATION_OVERLAY;

        if (shouldShake) {
            for (int j = 0; j < startBar; j++) rand.nextInt(3);
        }

        // Check if player has Thirsty effect OR drink is dirty
        boolean hasThirstyEffect = ThirstHelper.hasThirstyEffect(mc.player) || isDirty;

        int xOffset = hasThirstyEffect ? (ICON_SIZE * 4) : 0;
        int bgXOffset = hasThirstyEffect ? (ICON_SIZE * 13) : 0;

        for (int i = startBar; i < startBar + barsNeeded; i++)
        {
            int halfIcon = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = shouldShake ? top + (rand.nextInt(3) - 1) : top;

            // Draw background
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
            mc.ingameGUI.drawTexturedModalRect(x, y, bgXOffset, 0, ICON_SIZE, ICON_SIZE);

            // Draw thirst droplet
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

            if (halfIcon < thirstLevel + thirstRestored)
            {
                // Full droplet
                mc.ingameGUI.drawTexturedModalRect(x, y, xOffset + (ICON_SIZE * 4), 0, ICON_SIZE, ICON_SIZE);
            }
            else if (halfIcon == thirstLevel + thirstRestored)
            {
                // Half droplet
                mc.ingameGUI.drawTexturedModalRect(x, y, xOffset + (ICON_SIZE * 5), 0, ICON_SIZE, ICON_SIZE);
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(Gui.ICONS);
    }

    public static void drawThirstSaturationOverlay(float saturationGained, float saturationLevel,
                                                   Minecraft mc, int left, int top, float alpha,
                                                   int updateCounter)
    {
        if (saturationLevel + saturationGained <= 0) return;

        int startBar = saturationGained != 0 ? Math.max(0, (int) saturationLevel / 2) : 0;
        int endBar = (int) Math.ceil(Math.min(20, saturationLevel + saturationGained) / 2f);
        int barsNeeded = endBar - startBar;

        if (barsNeeded <= 0) return;

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        mc.getTextureManager().bindTexture(getSDTexture());

        // Shake logic (same seed as SD)
        rand.setSeed(updateCounter * 445L);
        int thirstLevel = ThirstHelper.getThirstLevel(mc.player);
        boolean shouldShake = saturationLevel <= 0.0F
                && updateCounter % (thirstLevel * 3 + 1) == 0
                && ModConfig.CLIENT.SHOW_VANILLA_ANIMATION_OVERLAY;

        if (shouldShake) {
            for (int j = 0; j < startBar; j++) rand.nextInt(3);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        for (int i = startBar; i < startBar + barsNeeded; i++)
        {
            int halfIcon = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = shouldShake ? top + (rand.nextInt(3) - 1) : top;

            int thirstSatInt = (int)(saturationLevel + saturationGained);

            if (halfIcon < thirstSatInt)
            {
                // Full saturation icon: X = 126 (14 * 9)
                mc.ingameGUI.drawTexturedModalRect(x, y, ICON_SIZE * 14, 0, ICON_SIZE, ICON_SIZE);
            }
            else if (halfIcon == thirstSatInt)
            {
                // Half saturation icon: X = 135 (15 * 9)
                mc.ingameGUI.drawTexturedModalRect(x, y, ICON_SIZE * 15, 0, ICON_SIZE, ICON_SIZE);
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(Gui.ICONS);
    }

    private static void enableAlpha(float alpha)
    {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
    }

    private static void disableAlpha()
    {
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}