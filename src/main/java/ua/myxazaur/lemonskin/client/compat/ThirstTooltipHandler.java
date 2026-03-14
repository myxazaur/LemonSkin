package ua.myxazaur.lemonskin.client.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.Tags;
import ua.myxazaur.lemonskin.helpers.SimpleDifficultyHelper;
import ua.myxazaur.lemonskin.helpers.ThirstHelper;
import ua.myxazaur.lemonskin.helpers.TooltipHelper;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ThirstTooltipHandler
{
    private static final ResourceLocation THIRST_OVERLAY = new ResourceLocation(Tags.MOD_ID, "textures/waterskin.png");
    private static final ResourceLocation THIRST_OVERLAY_CLASSIC = new ResourceLocation(Tags.MOD_ID, "textures/waterskin_classic.png");

    private ItemStack cachedStack = ItemStack.EMPTY;

    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(new ThirstTooltipHandler());
    }

    private static ResourceLocation getTexture()
    {
        return SimpleDifficultyHelper.useClassicHUD() ? THIRST_OVERLAY_CLASSIC : THIRST_OVERLAY;
    }

    private static boolean shouldShowTooltip()
    {
        if (!LemonSkin.hasSimpleDifficulty) return false;
        if (!SimpleDifficultyHelper.isThirstEnabled()) return false;

        if (ModConfig.CLIENT.MODS.SIMPLE_DIFFICULTY.ALWAYS_SHOW_DRINK_VALUES_TOOLTIP)
            return true;
        if (ModConfig.CLIENT.MODS.SIMPLE_DIFFICULTY.SHOW_DRINK_VALUES_IN_TOOLTIP && GuiScreen.isShiftKeyDown())
            return true;

        return false;
    }

    public static boolean shouldShowDrinkTooltip(ItemStack stack)
    {
        if (!shouldShowTooltip()) return false;
        return ThirstHelper.isDrink(stack);
    }

    private static void reserveDrinkTooltipSpace(List<String> tooltip, ItemStack stack)
    {
        if (!shouldShowDrinkTooltip(stack)) return;
        if (!ModConfig.CLIENT.USE_MODERN_TOOLTIP) return;

        SimpleDifficultyHelper.DrinkValues values = SimpleDifficultyHelper.getDrinkValues(stack);
        if (values == null || values.thirst <= 0) return;

        tooltip.add("\u00A0");
        tooltip.add("\u00A0");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemTooltip(ItemTooltipEvent event)
    {
        if (!ModConfig.CLIENT.USE_MODERN_TOOLTIP) return;

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        this.cachedStack = stack;

        reserveDrinkTooltipSpace(event.getToolTip(), stack);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderTooltip(RenderTooltipEvent.PostText event)
    {
        ItemStack stack = event.getStack();
        if (stack.isEmpty())
        {
            if (this.cachedStack == null || this.cachedStack.isEmpty()) return;
            stack = this.cachedStack;
        }

        if (!shouldShowDrinkTooltip(stack)) return;

        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen gui = mc.currentScreen;
        if (gui == null) return;

        SimpleDifficultyHelper.DrinkValues values = SimpleDifficultyHelper.getDrinkValues(stack);
        if (values == null || values.thirst <= 0) return;

        if (ModConfig.CLIENT.USE_MODERN_TOOLTIP)
            renderModern(event, stack, values);
        else
            renderLegacy(event, stack, values);

        this.cachedStack = ItemStack.EMPTY;
    }

    private int getHydrationU(float fillAmount)
    {
        if (fillAmount <= 0) return 28;
        if (fillAmount <= 0.5f) return 0;
        if (fillAmount <= 1.0f) return 7;
        if (fillAmount <= 1.5f) return 14;
        return 21;
    }

    private int getThirstV(boolean isDirty)
    {
        return isDirty ? 9 : 0;
    }

    // MODERN – draw inside tooltip
    private void renderModern(RenderTooltipEvent.PostText event, ItemStack stack,
                              SimpleDifficultyHelper.DrinkValues values)
    {
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen gui = mc.currentScreen;

        int lineHeight = 10;
        int y = event.getY() + (event.getLines().size() - 2) * lineHeight + 2;
        int x = event.getX();

        mc.getTextureManager().bindTexture(getTexture());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int thirst = values.thirst;
        float saturation = values.saturation;
        boolean isDirty = values.isDirty();
        int thirstV = getThirstV(isDirty);

        // Thirst icons
        int thirstBars = (thirst + 1) / 2;
        String thirstText = null;
        if (thirstBars > 10)
        {
            thirstText = "x" + thirstBars;
            thirstBars = 1;
        }

        int startX = x;
        for (int i = 0; i < thirstBars; i++)
        {
            int iconThirst = (i + 1) * 2;
            if (thirst >= iconThirst)
            {
                gui.drawTexturedModalRect(startX + i * 8, y, 37, thirstV, 8, 9);
            }
            else if (thirst == iconThirst - 1)
            {
                gui.drawTexturedModalRect(startX + i * 8, y, 46, thirstV, 8, 9);
            }
        }

        if (thirstText != null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            mc.fontRenderer.drawStringWithShadow(thirstText,
                    (startX + thirstBars * 8 + 2) * 4f / 3f,
                    (y * 4f / 3f) + 2,
                    0xFFDDDDDD);
            GlStateManager.popMatrix();
        }

        // Hydration icons
        y += 10;
        startX = x;

        int satBars = saturation > 0 ? (int) Math.ceil(saturation / 2f) : 1;
        String satText = null;
        if (satBars > 10)
        {
            satText = "x" + satBars;
            satBars = 1;
        }

        for (int i = 0; i < satBars; i++)
        {
            float iconSatStart = i * 2f;
            float fillAmount = Math.max(0, Math.min(2f, saturation - iconSatStart));

            int u = getHydrationU(fillAmount);
            gui.drawTexturedModalRect(startX + i * 6, y, u, 27, 7, 7);
        }

        if (satText != null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            mc.fontRenderer.drawStringWithShadow(satText,
                    (startX + satBars * 6 + 2) * 4f / 3f,
                    (y * 4f / 3f) + 1,
                    0xFFDDDDDD);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
    }

    // LEGACY – draw WaterSkin-style floating background
    // Render ONLY if the item is NOT food (otherwise TooltipOverlayHandler has already rendered the water)
    private void renderLegacy(RenderTooltipEvent.PostText event, ItemStack stack,
                              SimpleDifficultyHelper.DrinkValues values)
    {
        if (TooltipHelper.shouldShowFoodTooltip(stack))
        {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen gui = mc.currentScreen;
        ScaledResolution res = new ScaledResolution(mc);

        int thirst = values.thirst;
        float saturation = values.saturation;
        boolean isDirty = values.isDirty();
        int thirstV = getThirstV(isDirty);

        int lengthThirst = (thirst + 1) >> 1 << 3;
        int hydrationBars = saturation > 0 ? (int) Math.ceil(saturation / 2f) : 1;
        int lengthHydration = 2 + hydrationBars * 6;
        int length = Math.max(lengthThirst, lengthHydration);

        int baseX = event.getX();
        int baseY = event.getY() + event.getHeight();

        if (baseY + 29 >= res.getScaledHeight())
        {
            baseY = event.getY() - 33;
        }

        mc.getTextureManager().bindTexture(getTexture());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        // Background
        gui.drawTexturedModalRect(baseX - 2, baseY + 3, 0, 42, length + 4, 26);
        gui.drawTexturedModalRect(baseX + length + 2, baseY + 3, 88, 42, 3, 26);

        // Thirst droplets
        for (int i = 0; i * 2 < thirst; i++)
        {
            if (thirst - i * 2 == 1)
            {
                gui.drawTexturedModalRect(baseX + 2 + i * 8, baseY + 7, 46, thirstV, 8, 9);
                break;
            }
            gui.drawTexturedModalRect(baseX + 2 + i * 8, baseY + 7, 37, thirstV, 8, 9);
        }

        // Hydration icons
        for (int i = 0; i < hydrationBars; i++)
        {
            float iconSatStart = i * 2f;
            float fillAmount = Math.max(0, Math.min(2f, saturation - iconSatStart));

            int u = getHydrationU(fillAmount);
            gui.drawTexturedModalRect(baseX + 2 + i * 6, baseY + 17, u, 27, 7, 7);
        }

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableDepth();
    }
}