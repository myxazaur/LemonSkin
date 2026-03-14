package ua.myxazaur.lemonskin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.Tags;
import ua.myxazaur.lemonskin.client.compat.ThirstTooltipHandler;
import ua.myxazaur.lemonskin.helpers.*;

@SuppressWarnings("DataFlowIssue")
@SideOnly(Side.CLIENT)
public class TooltipOverlayHandler
{
	private ItemStack cachedStack = ItemStack.EMPTY;

	private static final int LEGACY_BOTTOM_OFFSET = 3;
	private static final int LEGACY_TOP_OFFSET    = -3;

	private static final ResourceLocation THIRST_OVERLAY = new ResourceLocation(Tags.MOD_ID, "textures/waterskin.png");
	private static final ResourceLocation THIRST_OVERLAY_CLASSIC = new ResourceLocation(Tags.MOD_ID, "textures/waterskin_classic.png");

	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(new TooltipOverlayHandler());
	}

	private static ResourceLocation getThirstTexture()
	{
		return SimpleDifficultyHelper.useClassicHUD() ? THIRST_OVERLAY_CLASSIC : THIRST_OVERLAY;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemTooltip(ItemTooltipEvent event)
	{
		if (!ModConfig.CLIENT.USE_MODERN_TOOLTIP) return;

		ItemStack stack = event.getItemStack();
		if (stack.isEmpty()) return;
		this.cachedStack = stack;

		TooltipHelper.reserveFoodTooltipSpace(event.getToolTip(), stack);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onRenderTooltip(RenderTooltipEvent.PostText event)
	{
		ItemStack stack = event.getStack();
		if (stack.isEmpty()) {
			if (this.cachedStack == null) return;
			stack = this.cachedStack;
		}

		if (!TooltipHelper.shouldShowFoodTooltip(stack)) return;

		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = mc.currentScreen;
		if (gui == null) return;

		EntityPlayer player = mc.player;
		FoodHelper.BasicFoodValues base   = FoodHelper.getDefaultFoodValues(stack);
		FoodHelper.BasicFoodValues actual = FoodHelper.getModifiedFoodValues(stack, player);

		if (LemonSkin.hasAppleCore)
		{
			base   = AppleCoreHelper.getFoodValuesForDisplay(base,   player);
			actual = AppleCoreHelper.getFoodValuesForDisplay(actual, player);
		}
		base   = BetterWithModsHelper.getFoodValuesForDisplay(base);
		actual = BetterWithModsHelper.getFoodValuesForDisplay(actual);

		if (base.equals(actual) && base.hunger == 0) return;

		if (ModConfig.CLIENT.USE_MODERN_TOOLTIP)
			renderModern(event, stack, base, actual);
		else
			renderLegacy(event, stack, base, actual);

		this.cachedStack = ItemStack.EMPTY;
	}

	// MODERN – draw inside tooltip
	private void renderModern(RenderTooltipEvent.PostText event, ItemStack stack,
	                          FoodHelper.BasicFoodValues base, FoodHelper.BasicFoodValues actual)
	{
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = mc.currentScreen;

		int biggestHunger   = Math.max(base.hunger, actual.hunger);
		float biggestSatInc = Math.max(base.getSaturationIncrement(), actual.getSaturationIncrement());

		int hungerBars = (int) Math.ceil(Math.abs(biggestHunger) / 2f);
		int satBars    = (int) Math.max(1, Math.ceil(Math.abs(biggestSatInc) / 2f));

		String hungerText = null;
		if (hungerBars > 10)
		{
			hungerText = "x" + ((biggestHunger < 0 ? "-" : "") + hungerBars);
			hungerBars = 1;
		}

		String satText = null;
		if (satBars > 10 || satBars == 0)
		{
			satText = "x" + ((biggestSatInc < 0 ? "-" : "") + satBars);
			satBars = 1;
		}

		int lineHeight = 10;

		int drinkLinesOffset = 0;
		if (ThirstTooltipHandler.shouldShowDrinkTooltip(stack))
		{
			drinkLinesOffset = 2;
		}

		int y = event.getY() + (event.getLines().size() - 2 - drinkLinesOffset) * lineHeight + 2;
		int x = event.getX();

		// Hunger
		mc.getTextureManager().bindTexture(Gui.ICONS);
		boolean rotten = FoodHelper.isRotten(stack);
		int iconOffset = rotten ? 36 : 0;

		int startX = x;
		for (int i = 0; i < hungerBars * 2; i += 2)
		{
			int u;
			if (actual.hunger < 0)
				u = 34 + iconOffset;
			else if (actual.hunger > base.hunger && base.hunger <= i)
				u = 133 + iconOffset;
			else if (actual.hunger > i + 1 || base.hunger == actual.hunger)
				u = 16 + (rotten ? 13 : 0) * 9;
			else if (actual.hunger == i + 1)
				u = 124;
			else
				u = 34;

			gui.drawTexturedModalRect(startX + i / 2 * 9, y, u, 27, 9, 9);

			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
			gui.drawTexturedModalRect(startX + i / 2 * 9, y,
					base.hunger - 1 == i ? 115 : 106, 27, 9, 9);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			if (actual.hunger > i)
				gui.drawTexturedModalRect(startX + i / 2 * 9, y,
						actual.hunger - 1 == i ? 61 + iconOffset : 52 + iconOffset, 27, 9, 9);
		}

		if (hungerText != null)
		{
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			mc.fontRenderer.drawStringWithShadow(hungerText,
					(startX + hungerBars * 9 + 2) * 4 / 3,
					(y * 4 / 3) + 2,
					0xFFDDDDDD);
			GlStateManager.popMatrix();
		}

		// Saturation
		y += 10;
		startX = x;

		float satInc = actual.getSaturationIncrement();
		float absSat = Math.abs(satInc);

		ModConfig.RGB color = ModConfig.CLIENT.getColor();
		mc.getTextureManager().bindTexture(ModConfig.CLIENT.getIcons());
		for (int i = 0; i < satBars * 2; i += 2)
		{
			float eff = (absSat - i) / 2f;
			int u = eff >= 1 ? 21 : eff > 0.5 ? 14 : eff > 0.25 ? 7 : eff > 0 ? 0 : 28;
			int v = satInc >= 0 ? 27 : 34;

			GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F);
			if (absSat <= i)
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);

			gui.drawTexturedModalRect(startX + i / 2 * 7, y, u, v, 7, 7);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (satText != null)
		{
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			mc.fontRenderer.drawStringWithShadow(satText,
					(startX + satBars * 6 + 2) * 4 / 3,
					(y * 4 / 3) + 1,
					0xFFDDDDDD);
			GlStateManager.popMatrix();
		}

		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
	}

	// LEGACY – draw on a separate floating background (includes drink values if applicable)
	private void renderLegacy(RenderTooltipEvent.PostText event, ItemStack stack,
	                          FoodHelper.BasicFoodValues base, FoodHelper.BasicFoodValues actual)
	{
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = mc.currentScreen;
		ScaledResolution scale = new ScaledResolution(mc);

		int toolTipY = event.getY();
		int toolTipX = event.getX();
		int toolTipW = event.getWidth();
		int toolTipH = event.getHeight();

		boolean hasDrinkValues = ThirstTooltipHandler.shouldShowDrinkTooltip(stack);
		SimpleDifficultyHelper.DrinkValues drinkValues = null;
		if (hasDrinkValues)
		{
			drinkValues = SimpleDifficultyHelper.getDrinkValues(stack);
			if (drinkValues == null || drinkValues.thirst <= 0)
				hasDrinkValues = false;
		}

		int biggestHunger   = Math.max(base.hunger, actual.hunger);
		float biggestSatInc = Math.max(base.getSaturationIncrement(), actual.getSaturationIncrement());

		int hungerBars = (int) Math.ceil(Math.abs(biggestHunger) / 2f);
		boolean hungerOverflow = hungerBars > 10;
		String hungerText = hungerOverflow ? ((biggestHunger < 0 ? -1 : 1) * hungerBars) + "x" : null;
		if (hungerOverflow) hungerBars = 1;

		int satBars = (int) Math.max(1, Math.ceil(Math.abs(biggestSatInc) / 2f));
		boolean satOverflow = satBars > 10;
		String satText = satOverflow ? ((biggestSatInc < 0 ? -1 : 1) * satBars) + "x" : null;
		if (satOverflow) satBars = 1;

		int thirstBars = 0;
		int hydrationBars = 0;
		String thirstText = null;
		String hydrationText = null;
		if (hasDrinkValues)
		{
			thirstBars = (drinkValues.thirst + 1) / 2;
			if (thirstBars > 10)
			{
				thirstText = thirstBars + "x";
				thirstBars = 1;
			}

			hydrationBars = drinkValues.saturation > 0 ? (int) Math.ceil(drinkValues.saturation / 2f) : 1;
			if (hydrationBars > 10)
			{
				hydrationText = hydrationBars + "x";
				hydrationBars = 1;
			}
		}

		int hungerIconsWidth  = hungerBars * 9;
		int satIconsWidth     = satBars * 6;
		int thirstIconsWidth  = thirstBars * 8;
		int hydrationIconsWidth = hydrationBars * 6;

		int hungerTextWidth    = hungerText == null ? 0 : (int) (mc.fontRenderer.getStringWidth(hungerText) * 0.75f);
		int satTextWidth       = satText == null ? 0 : (int) (mc.fontRenderer.getStringWidth(satText) * 0.75f);
		int thirstTextWidth    = thirstText == null ? 0 : (int) (mc.fontRenderer.getStringWidth(thirstText) * 0.75f);
		int hydrationTextWidth = hydrationText == null ? 0 : (int) (mc.fontRenderer.getStringWidth(hydrationText) * 0.75f);

		int hungerLineWidth    = hungerIconsWidth + (hungerTextWidth > 0 ? hungerTextWidth + 2 : 0);
		int satLineWidth       = satIconsWidth + (satTextWidth > 0 ? satTextWidth + 2 : 0);
		int thirstLineWidth    = thirstIconsWidth + (thirstTextWidth > 0 ? thirstTextWidth + 2 : 0);
		int hydrationLineWidth = hydrationIconsWidth + (hydrationTextWidth > 0 ? hydrationTextWidth + 2 : 0);

		int contentWidth = Math.max(hungerLineWidth, satLineWidth);
		if (hasDrinkValues)
		{
			contentWidth = Math.max(contentWidth, Math.max(thirstLineWidth, hydrationLineWidth));
		}
		int overlayWidth = contentWidth + 6;

		int blockHeight = hasDrinkValues ? 39 : 19;

		int toolTipBottomY = toolTipY + toolTipH + 1 + LEGACY_BOTTOM_OFFSET;
		boolean drawBelow  = toolTipBottomY + blockHeight < scale.getScaledHeight() - 3;

		int topY    = drawBelow ? toolTipBottomY : toolTipY - blockHeight + LEGACY_TOP_OFFSET;
		int bottomY = topY + blockHeight;

		int minLeftX = toolTipX;
		int maxRightX = toolTipX + toolTipW;

		int rightX = maxRightX;
		int leftX  = rightX - overlayWidth;

		boolean needsTopBorder = false;
		if (leftX < minLeftX)
		{
			leftX = minLeftX;
			rightX = leftX + overlayWidth;
			needsTopBorder = true;
		}

		// Draw background
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		Gui.drawRect(leftX - 1, topY, rightX + 1, bottomY, 0xF0100010);
		Gui.drawRect(leftX, drawBelow ? bottomY : topY - 1,
				rightX, drawBelow ? bottomY + 1 : topY, 0xF0100010);
		if (needsTopBorder || !drawBelow)
			Gui.drawRect(leftX, drawBelow ? topY - 1 : bottomY,
					rightX, drawBelow ? topY : bottomY + 1, 0xF0100010);
		Gui.drawRect(leftX, topY, rightX, bottomY, 0x66FFFFFF);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		int rightPadding = 3;
		int y = topY + 1;

		// === HUNGER ICONS ===
		mc.getTextureManager().bindTexture(Gui.ICONS);
		boolean isRotten = FoodHelper.isRotten(stack);
		int iconOffset = isRotten ? 36 : 0;
		int background = isRotten ? 13 : 0;

		int hungerStartX = rightX - rightPadding - hungerIconsWidth;
		int hungerX = hungerStartX;

		for (int i = hungerBars * 2 - 2; i >= 0; i -= 2)
		{
			if (actual.hunger < 0)
				gui.drawTexturedModalRect(hungerX, y, 34 + iconOffset, 27, 9, 9);
			else if (actual.hunger > base.hunger && base.hunger <= i)
				gui.drawTexturedModalRect(hungerX, y, 133 + iconOffset, 27, 9, 9);
			else if (actual.hunger > i + 1 || base.hunger == actual.hunger)
				gui.drawTexturedModalRect(hungerX, y, 16 + background * 9, 27, 9, 9);
			else if (actual.hunger == i + 1)
				gui.drawTexturedModalRect(hungerX, y, 124, 27, 9, 9);
			else
				gui.drawTexturedModalRect(hungerX, y, 34, 27, 9, 9);

			GlStateManager.color(1.0F, 1.0F, 1.0F, .25F);
			gui.drawTexturedModalRect(hungerX, y,
					base.hunger - 1 == i ? 115 : 106, 27, 9, 9);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			if (actual.hunger > i)
				gui.drawTexturedModalRect(hungerX, y,
						actual.hunger - 1 == i ? 61 + iconOffset : 52 + iconOffset, 27, 9, 9);
			hungerX += 9;
		}

		if (hungerText != null)
		{
			int hungerTextX = hungerStartX - hungerTextWidth - 2;
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			mc.fontRenderer.drawStringWithShadow(hungerText,
					hungerTextX * 4f / 3f, y * 4f / 3f + 2, 0xFFDDDDDD);
			GlStateManager.popMatrix();
		}

		// === FOOD SATURATION ICONS ===
		y += 10;

		int satStartX = rightX - rightPadding - satIconsWidth;
		int satX = satStartX;

		float satInc = actual.getSaturationIncrement();
		float absSat = Math.abs(satInc);

		ModConfig.RGB color = ModConfig.CLIENT.getColor();
		mc.getTextureManager().bindTexture(ModConfig.CLIENT.getIcons());
		for (int i = satBars * 2 - 2; i >= 0; i -= 2)
		{
			float eff = (absSat - i) / 2f;
			int u = eff >= 1 ? 21 : eff > 0.5 ? 14 : eff > 0.25 ? 7 : eff > 0 ? 0 : 28;
			int v = satInc >= 0 ? 27 : 34;

			GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), 1.0F);
			if (absSat <= i)
				GlStateManager.color(1.0F, 1.0F, 1.0F, .5F);
			gui.drawTexturedModalRect(satX, y, u, v, 7, 7);
			if (absSat <= i)
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			satX += 6;
		}

		if (satText != null)
		{
			int satTextX = satStartX - satTextWidth - 2;
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			mc.fontRenderer.drawStringWithShadow(satText,
					satTextX * 4f / 3f, y * 4f / 3f + 1, 0xFFDDDDDD);
			GlStateManager.popMatrix();
		}

		// === THIRST ICONS (if applicable) ===
		if (hasDrinkValues)
		{
			y += 10;

			mc.getTextureManager().bindTexture(getThirstTexture());
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			int thirstV = drinkValues.isDirty() ? 9 : 0;
			int thirstStartX = rightX - rightPadding - thirstIconsWidth;
			int thirstX = thirstStartX;

			for (int i = thirstBars - 1; i >= 0; i--)
			{
				int iconThirst = (i + 1) * 2;
				if (drinkValues.thirst >= iconThirst)
				{
					gui.drawTexturedModalRect(thirstX, y, 37, thirstV, 8, 9);
				}
				else if (drinkValues.thirst == iconThirst - 1)
				{
					gui.drawTexturedModalRect(thirstX, y, 46, thirstV, 8, 9);
				}
				thirstX += 8;
			}

			if (thirstText != null)
			{
				int thirstTextX = thirstStartX - thirstTextWidth - 2;
				GlStateManager.pushMatrix();
				GlStateManager.scale(0.75F, 0.75F, 0.75F);
				mc.fontRenderer.drawStringWithShadow(thirstText,
						thirstTextX * 4f / 3f, y * 4f / 3f + 2, 0xFFDDDDDD);
				GlStateManager.popMatrix();
			}

			// === HYDRATION ICONS ===
			y += 10;

			int hydrationStartX = rightX - rightPadding - hydrationIconsWidth;
			int hydrationX = hydrationStartX;

			for (int i = hydrationBars - 1; i >= 0; i--)
			{
				float iconSatStart = i * 2f;
				float fillAmount = Math.max(0, Math.min(2f, drinkValues.saturation - iconSatStart));

				int u = getHydrationU(fillAmount);
				gui.drawTexturedModalRect(hydrationX, y, u, 27, 7, 7);
				hydrationX += 6;
			}

			if (hydrationText != null)
			{
				int hydrationTextX = hydrationStartX - hydrationTextWidth - 2;
				GlStateManager.pushMatrix();
				GlStateManager.scale(0.75F, 0.75F, 0.75F);
				mc.fontRenderer.drawStringWithShadow(hydrationText,
						hydrationTextX * 4f / 3f, y * 4f / 3f + 1, 0xFFDDDDDD);
				GlStateManager.popMatrix();
			}
		}

		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
	}

	private int getHydrationU(float fillAmount)
	{
		if (fillAmount <= 0) return 28;
		if (fillAmount <= 0.5f) return 0;
		if (fillAmount <= 1.0f) return 7;
		if (fillAmount <= 1.5f) return 14;
		return 21;
	}
}