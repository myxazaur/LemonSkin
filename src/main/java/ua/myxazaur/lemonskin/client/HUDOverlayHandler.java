package ua.myxazaur.lemonskin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.ModInfo;
import ua.myxazaur.lemonskin.helpers.AppleCoreHelper;
import ua.myxazaur.lemonskin.helpers.FoodHelper;
import ua.myxazaur.lemonskin.helpers.HealthHelper;
import ua.myxazaur.lemonskin.helpers.HungerHelper;

import java.lang.reflect.Field;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class HUDOverlayHandler
{
	private float flashAlpha = 0f;
	private byte alphaDir = 1;
	protected int iconsOffset;
	private static int updateCounter;

	private static final ResourceLocation modIcons = new ResourceLocation(ModInfo.MODID_LOWER, "textures/icons.png");
	private static final Field UCField = ReflectionHelper.findField(GuiIngame.class, "updateCounter", "field_73837_f");

	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(new HUDOverlayHandler());
	}

	@SubscribeEvent(priority=EventPriority.LOW)
	public void onPreRender(RenderGameOverlayEvent.Pre event)
	{
		iconsOffset = GuiIngameForge.right_height;

		if (event.isCanceled())
			return;

		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;

		ScaledResolution scale = event.getResolution();

		if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD)
		{
			if (!ModConfig.CLIENT.SHOW_FOOD_EXHAUSTION_UNDERLAY)
				return;

			int left = scale.getScaledWidth() / 2 + 91;
			int top = scale.getScaledHeight() - iconsOffset;

			drawExhaustionOverlay(HungerHelper.getExhaustion(player), mc, left, top, 1f);
		}

		if (
				event.getType() == RenderGameOverlayEvent.ElementType.HEALTH ||
				event.getType() == RenderGameOverlayEvent.ElementType.FOOD
		)
		{
			try {
				updateCounter = UCField.getInt(mc.ingameGUI);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOW)
	public void onRender(RenderGameOverlayEvent.Post event)
	{

		if (event.isCanceled())
			return;

		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		ScaledResolution scale = event.getResolution();

		if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
			if (!shouldShowEstimatedHealth(mc.player))
				return;

			ItemStack held = player.getHeldItemMainhand();
			if (!FoodHelper.isFood(held))
				held = player.getHeldItemOffhand();
			if (held.isEmpty() || !FoodHelper.isFood(held))
				return;

			FoodHelper.BasicFoodValues foodValues = FoodHelper.getModifiedFoodValues(held, player);

			PotionEffect effect = FoodHelper.getEffect(held);
			// Hardcode for golden apple
			if (held.getItem() == Items.GOLDEN_APPLE)
			{
				if (held.getMetadata() > 0) effect =
						new PotionEffect(MobEffects.REGENERATION, 400, 1);
				else    new PotionEffect(MobEffects.REGENERATION, 100, 1);

			}

			float heal = HealthHelper.getEstimatedHealthIncrement(player, foodValues, effect);

			if (heal <= 0) return;

			float currentHealth = player.getHealth();
			float newHealth = Math.min(currentHealth + heal, player.getMaxHealth());

			int left = event.getResolution().getScaledWidth()  / 2 - 91;
			int top  = event.getResolution().getScaledHeight() - iconsOffset;

			drawHealthOverlay(currentHealth, newHealth, mc, left, top, flashAlpha);
		}

		if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD)
		{
			if (!ModConfig.CLIENT.SHOW_FOOD_VALUES_OVERLAY && !ModConfig.CLIENT.SHOW_SATURATION_OVERLAY)
				return;

			ItemStack heldItem = player.getHeldItemMainhand();
			if(!FoodHelper.isFood(heldItem))
				heldItem = player.getHeldItemOffhand();
			FoodStats stats = player.getFoodStats();

			int left = scale.getScaledWidth() / 2 + 91;
			int top = scale.getScaledHeight() - iconsOffset;

			// saturation overlay
			if (ModConfig.CLIENT.SHOW_SATURATION_OVERLAY)
				drawSaturationOverlay(0, stats.getSaturationLevel(), mc, left, top, 1f);

			if (!ModConfig.CLIENT.SHOW_FOOD_VALUES_OVERLAY || heldItem.isEmpty() || !FoodHelper.isFood(heldItem))
			{
				flashAlpha = 0;
				alphaDir = 1;
				return;
			}

			// restored hunger/saturation overlay while holding food
			FoodHelper.BasicFoodValues foodValues = FoodHelper.getModifiedFoodValues(heldItem, player);
			// Apply scale for altered max hunger if necessary
			if (LemonSkin.hasAppleCore)
				foodValues = AppleCoreHelper.getFoodValuesForDisplay(foodValues, player);
			drawHungerOverlay(foodValues.hunger, stats.getFoodLevel(), mc, left, top, flashAlpha, FoodHelper.isRotten(heldItem));

			if (ModConfig.CLIENT.SHOW_SATURATION_OVERLAY)
			{
				int newFoodValue = stats.getFoodLevel() + foodValues.hunger;
				float newSaturationValue = stats.getSaturationLevel() + foodValues.getSaturationIncrement();
				drawSaturationOverlay(newSaturationValue > newFoodValue ? newFoodValue - stats.getSaturationLevel() : foodValues.getSaturationIncrement(), stats.getSaturationLevel(), mc, left, top, flashAlpha);
			}
		}
	}

	public static void drawSaturationOverlay(float saturationGained, float saturationLevel, Minecraft mc, int left, int top, float alpha)
	{
		if (saturationLevel + saturationGained < 0)
			return;

		int startBar = saturationGained != 0 ? Math.max(0, (int) saturationLevel / 2) : 0;
		int endBar = (int) Math.ceil(Math.min(20, saturationLevel + saturationGained) / 2f);
		int barsNeeded = endBar - startBar;
		mc.getTextureManager().bindTexture(modIcons);

		Random localRand = new Random((long) updateCounter * 312871L); // Hardcoded random seed from GuiIngameForge
		boolean shouldShake = mc.player.getFoodStats().getSaturationLevel() <= 0.0F &&
				updateCounter % (mc.player.getFoodStats().getFoodLevel() * 3 + 1) == 0 &&
				ModConfig.CLIENT.SHOW_VANILLA_ANIMATION_OVERLAY;

		if (shouldShake) {
			for (int j = 0; j < startBar; j++) {
				localRand.nextInt(3); // Dummy
			}
		}

		enableAlpha(alpha);
		for (int i = startBar; i < startBar + barsNeeded; ++i)
		{
			int x = left - i * 8 - 9;
			int y = top;

			if (shouldShake) {
				y = top + (localRand.nextInt(3) - 1);
			}

			float effectiveSaturationOfBar = (saturationLevel + saturationGained) / 2 - i;

			if (effectiveSaturationOfBar >= 1)
				mc.ingameGUI.drawTexturedModalRect(x, y, 27, 0, 9, 9);
			else if (effectiveSaturationOfBar > .5)
				mc.ingameGUI.drawTexturedModalRect(x, y, 18, 0, 9, 9);
			else if (effectiveSaturationOfBar > .25)
				mc.ingameGUI.drawTexturedModalRect(x, y, 9, 0, 9, 9);
			else if (effectiveSaturationOfBar > 0)
				mc.ingameGUI.drawTexturedModalRect(x, y, 0, 0, 9, 9);
		}
		disableAlpha(alpha);

		// rebind default icons
		mc.getTextureManager().bindTexture(Gui.ICONS);
	}

	public static void drawHungerOverlay(int hungerRestored, int foodLevel, Minecraft mc, int left, int top, float alpha, boolean isRotten) {
		if (hungerRestored == 0)
			return;

		int startBar = foodLevel / 2;
		int endBar = (int) Math.ceil(Math.min(20, foodLevel + hungerRestored) / 2f);
		int barsNeeded = endBar - startBar;

		mc.getTextureManager().bindTexture(Gui.ICONS);

		enableAlpha(alpha);

		Random localRand = new Random((long) updateCounter * 312871L); // Hardcoded random seed from GuiIngameForge

		boolean shouldShake = mc.player.getFoodStats().getSaturationLevel() <= 0.0F &&
				updateCounter % (foodLevel * 3 + 1) == 0 &&
				ModConfig.CLIENT.SHOW_VANILLA_ANIMATION_OVERLAY;

		if (shouldShake) {
			for (int j = 0; j < startBar; j++) {
				localRand.nextInt(3); // Dummy
			}
		}

		for (int i = startBar; i < startBar + barsNeeded; ++i) {
			int idx = i * 2 + 1;
			int x = left - i * 8 - 9;
			int y = top;
			int icon = 16;
			byte background = 0;

			if (mc.player.isPotionActive(MobEffects.HUNGER) || isRotten) {
				icon += 36;
				background = 13;
			}

			if (shouldShake) {
				y = top + (localRand.nextInt(3) - 1);
			}

			mc.ingameGUI.drawTexturedModalRect(x, y, 16 + background * 9, 27, 9, 9);

			if (idx < foodLevel + hungerRestored)
				mc.ingameGUI.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
			else if (idx == foodLevel + hungerRestored)
				mc.ingameGUI.drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
		}
		disableAlpha(alpha);
	}

	// BETA
	public static void drawHealthOverlay(float current, float modified, Minecraft mc, int left, int top, float alpha) {
		if (modified <= current) return;

		mc.getTextureManager().bindTexture(Gui.ICONS);

		EntityPlayer player = mc.player;
		int healthTarget  = MathHelper.ceil(modified);

		boolean hardcore = mc.world.getWorldInfo().isHardcoreModeEnabled();
		int topOffset = hardcore ? 45 : 0;

		int margin = 16;
		if (player.isPotionActive(MobEffects.POISON))      margin += 36;
		else if (player.isPotionActive(MobEffects.WITHER)) margin += 72;

		enableAlpha(alpha);

		int start;
		int end   = MathHelper.ceil(modified / 2.0F);

		if ((int)current % 2 == 0) start = (int)(current / 2.0F);
		else                       start = (int)Math.floor(current / 2.0F);

		float healthMax = player.getMaxHealth();
		float absorb = player.getAbsorptionAmount();
		int totalHearts = MathHelper.ceil((healthMax + absorb) / 2.0F);

		boolean shouldShake = current <= 4.0F && ModConfig.CLIENT.SHOW_VANILLA_ANIMATION_OVERLAY;

		for (int i = start; i < end; i++) {
			int row  = i / 10;
			int x = left + (i % 10) * 8;
			int y = top - row * 8;

			if (shouldShake) {
				Random rand = new Random((long) updateCounter * 312871L);
				int skips = totalHearts - 1 - i;
				for (int j = 0; j < skips; j++) {
					rand.nextInt(2);
				}
				y += rand.nextInt(2);
			}

			// Background
			mc.ingameGUI.drawTexturedModalRect(x, y, 16, topOffset, 9, 9);

			// Heart
			boolean isHalf = (i * 2 + 1) == healthTarget;
			int u = isHalf ? margin + 45 : margin + 36;
			mc.ingameGUI.drawTexturedModalRect(x, y, u, topOffset, 9, 9);
		}

		disableAlpha(alpha);
	}

	public static void drawExhaustionOverlay(float exhaustion, Minecraft mc, int left, int top, float alpha)
	{
		mc.getTextureManager().bindTexture(modIcons);

		float maxExhaustion = HungerHelper.getMaxExhaustion(mc.player);
		// clamp between 0 and 1
		float ratio = Math.min(1, Math.max(0, exhaustion / maxExhaustion));
		int width = (int) (ratio * 81);
		int height = 9;

		enableAlpha(.75f);
		mc.ingameGUI.drawTexturedModalRect(left - width, top, 81 - width, 18, width, height);
		disableAlpha(.75f);

		// rebind default icons
		mc.getTextureManager().bindTexture(Gui.ICONS);
	}

	private boolean shouldShowEstimatedHealth(EntityPlayer player) {
		if (!ModConfig.CLIENT.SHOW_FOOD_HEALTH_HUD_OVERLAY) return false;

		if (player.world.getDifficulty() == EnumDifficulty.PEACEFUL)
			return false;

		FoodStats stats = player.getFoodStats();
		if (stats.getFoodLevel() >= 18)
			return false;

		if (player.isPotionActive(MobEffects.POISON))   return false;
		if (player.isPotionActive(MobEffects.WITHER))   return false;
		if (player.isPotionActive(MobEffects.REGENERATION)) return false;

		return true;
	}

	public static void enableAlpha(float alpha)
	{
		GlStateManager.enableBlend();

		if (alpha == 1f)
			return;

		GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void disableAlpha(float alpha)
	{
		GlStateManager.disableBlend();

		if (alpha == 1f)
			return;

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
			return;

		flashAlpha += alphaDir * 0.125f;
		if (flashAlpha >= 1.5f)
		{
			flashAlpha = 1f;
			alphaDir = -1;
		}
		else if (flashAlpha <= -0.5f)
		{
			flashAlpha = 0f;
			alphaDir = 1;
		}
	}
}
