package ua.myxazaur.lemonskin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
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
import ua.myxazaur.lemonskin.helpers.*;

import java.util.List;

@SideOnly(Side.CLIENT)
public class TooltipOverlayHandler
{
	private static final ResourceLocation MOD_ICONS =
			new ResourceLocation(Tags.MOD_ID, "textures/icons.png");
	private ItemStack cachedStack = ItemStack.EMPTY;

	/* Legacy constants -------------------------------------------------- */
	private static final int LEGACY_BOTTOM_OFFSET = 3;
	private static final int LEGACY_TOP_OFFSET    = -3;

	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(new TooltipOverlayHandler());
	}

	/* ------------------------------------------------------------------ */
	/* 1)  Inject blank lines so Forge reserves space (Modern mode only)  */
	/* ------------------------------------------------------------------ */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemTooltip(ItemTooltipEvent event)
	{
		if (!ModConfig.CLIENT.USE_MODERN_TOOLTIP) return;

		ItemStack stack = event.getItemStack();
		if (stack.isEmpty()) return;
		this.cachedStack = stack;

		TooltipHelper.reserveFoodTooltipSpace(event.getToolTip(), stack);
	}

	/* ------------------------------------------------------------------ */
	/* 2)  Actual rendering                                               */
	/* ------------------------------------------------------------------ */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onRenderTooltip(RenderTooltipEvent.PostText event)
	{
		ItemStack stack = event.getStack();
		if (stack.isEmpty()) {
			// Forge gives empty ItemStack if tooltip rendering called from recipe book method
			if (this.cachedStack == null) return;
			stack = this.cachedStack;
		}

		boolean shouldShow =
				(ModConfig.CLIENT.SHOW_FOOD_VALUES_IN_TOOLTIP && KeyHelper.isShiftKeyDown()) ||
						ModConfig.CLIENT.ALWAYS_SHOW_FOOD_VALUES_TOOLTIP;
		if (!shouldShow || !FoodHelper.isFood(stack)) return;

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

		/* -------------------------------------------------------------- */
		/*  Branch depending on config                                   */
		/* -------------------------------------------------------------- */
		if (ModConfig.CLIENT.USE_MODERN_TOOLTIP)
			renderModern(event, stack, base, actual);
		else
			renderLegacy(event, stack, base, actual);

		// Clear ItemStack to avoid tooltip rendering issues
		this.cachedStack = ItemStack.EMPTY;
	}

	/* ================================================================ */
	/*  MODERN – draw inside tooltip (Forge already reserved space)    */
	/* ================================================================ */
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
		int y = event.getY() + (event.getLines().size() - 2) * lineHeight + 2;
		int x = event.getX();

		/* Hunger ------------------------------------------------------- */
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

		/* Saturation --------------------------------------------------- */
		y += 10;
		startX = x;

		float satInc = actual.getSaturationIncrement();
		float absSat = Math.abs(satInc);

		mc.getTextureManager().bindTexture(MOD_ICONS);
		for (int i = 0; i < satBars * 2; i += 2)
		{
			float eff = (absSat - i) / 2f;
			int u = eff >= 1 ? 21 : eff > 0.5 ? 14 : eff > 0.25 ? 7 : eff > 0 ? 0 : 28;
			int v = satInc >= 0 ? 27 : 34;

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

		/* GL reset ----------------------------------------------------- */
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
	}

	/* ================================================================ */
	/*  LEGACY – draw on a separate floating background                */
	/* ================================================================ */
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

		int toolTipBottomY = toolTipY + toolTipH + 1 + LEGACY_BOTTOM_OFFSET;
		boolean drawBelow  = toolTipBottomY + 20 < scale.getScaledHeight() - 3;

		int topY    = drawBelow ? toolTipBottomY : toolTipY - 20 + LEGACY_TOP_OFFSET;
		int bottomY = topY + 19;

		int hungerIconsWidth  = hungerBars * 9;
		int satIconsWidth     = satBars * 6;

		int hungerTextWidth  = hungerText == null ? 0 : (int) (mc.fontRenderer.getStringWidth(hungerText) * 0.75f);
		int satTextWidth     = satText    == null ? 0 : (int) (mc.fontRenderer.getStringWidth(satText)    * 0.75f);

		int hungerLineWidth = hungerIconsWidth  + (hungerTextWidth  > 0 ? hungerTextWidth  + 2 : 0);
		int satLineWidth    = satIconsWidth     + (satTextWidth     > 0 ? satTextWidth     + 2 : 0);

		int contentWidth = Math.max(hungerLineWidth, satLineWidth);
		int overlayWidth = contentWidth + 6;

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

		/* Draw background -------------------------------------------- */
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
		int y = bottomY - 18;

		/* Hunger icons ----------------------------------------------- */
		mc.getTextureManager().bindTexture(Gui.ICONS);
		boolean isRotten = FoodHelper.isRotten(stack);
		int iconOffset = isRotten ? 36 : 0;
		int background = isRotten ? 13 : 0;

		int hungerTextX = rightX - rightPadding - hungerIconsWidth - (hungerTextWidth > 0 ? 2 : 0) - hungerTextWidth;
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
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			mc.fontRenderer.drawStringWithShadow(hungerText,
					hungerTextX * 4 / 3, y * 4 / 3 + 2, 0xFFDDDDDD);
			GlStateManager.popMatrix();
		}

		/* Saturation icons ------------------------------------------- */
		y += 10;

		int satTextX = rightX - rightPadding - satIconsWidth - (satTextWidth > 0 ? 2 : 0) - satTextWidth;
		int satStartX = rightX - rightPadding - satIconsWidth;

		int satX = satStartX;

		float satInc = actual.getSaturationIncrement();
		float absSat = Math.abs(satInc);

		mc.getTextureManager().bindTexture(MOD_ICONS);
		for (int i = satBars * 2 - 2; i >= 0; i -= 2)
		{
			float eff = (absSat - i) / 2f;
			int u = eff >= 1 ? 21 : eff > 0.5 ? 14 : eff > 0.25 ? 7 : eff > 0 ? 0 : 28;
			int v = satInc >= 0 ? 27 : 34;

			if (absSat <= i)
				GlStateManager.color(1.0F, 1.0F, 1.0F, .5F);
			gui.drawTexturedModalRect(satX, y, u, v, 7, 7);
			if (absSat <= i)
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			satX += 6;
		}

		if (satText != null)
		{
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			mc.fontRenderer.drawStringWithShadow(satText,
					satTextX * 4 / 3, y * 4 / 3 + 1, 0xFFDDDDDD);
			GlStateManager.popMatrix();
		}

		/* GL reset ---------------------------------------------------- */
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
	}
}