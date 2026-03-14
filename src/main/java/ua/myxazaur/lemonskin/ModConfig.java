package ua.myxazaur.lemonskin;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Tags.MOD_ID)
public class ModConfig {

	@Config.Name("Client")
	@Config.LangKey("lemonskin.client")
	@Config.Comment("These config settings are client-side only")
	public static ClientCategory CLIENT = new ClientCategory();

	public static class ClientCategory {

		@Config.Ignore
		private transient RGB cachedColor = null;

		@Config.Name("Mods")
		@Config.LangKey("lemonskin.mods")
		@Config.Comment("Compatibility and integration settings with other mods")
		public ModCompatibility MODS = new ModCompatibility();

		public static class ModCompatibility {
			@Config.Name("Mantle")
			@Config.LangKey("lemonskin.mantle")
			@Config.Comment("If true, makes the health overlay compatible with Mantle")
			public boolean MANTLE = true;

			@Config.Name("Simple Difficulty")
			@Config.LangKey("lemonskin.simpledifficulty")
			@Config.Comment("Integration settings for Simple Difficulty mod")
			public SimpleDifficultyConfig SIMPLE_DIFFICULTY = new SimpleDifficultyConfig();

			public static class SimpleDifficultyConfig {
				@Config.Name("Show Thirst Exhaustion Underlay")
				@Config.LangKey("show.thirst.exhaustion.underlay")
				@Config.Comment("If true, shows thirst exhaustion as a progress bar behind the thirst bar")
				public boolean SHOW_THIRST_EXHAUSTION_UNDERLAY = true;

				@Config.Name("Show Thirst Values HUD Overlay")
				@Config.LangKey("show.thirst.values.hud.overlay")
				@Config.Comment("If true, shows the thirst that would be restored by drink you are currently holding")
				public boolean SHOW_THIRST_VALUES_OVERLAY = true;

				@Config.Name("Show Thirst Saturation HUD Overlay")
				@Config.LangKey("show.thirst.saturation.hud.overlay")
				@Config.Comment("If true, shows the hydration that would be restored by drink you are currently holding")
				public boolean SHOW_THIRST_SATURATION_OVERLAY = true;

				@Config.Name("Show Drink Values in Tooltip")
				@Config.LangKey("show.drink.values.in.tooltip")
				@Config.Comment("If true, shows the thirst and hydration values of drinks in tooltip while holding SHIFT")
				public boolean SHOW_DRINK_VALUES_IN_TOOLTIP = false;

				@Config.Name("Show Drink Values in Tooltip Always")
				@Config.LangKey("show.drink.values.in.tooltip.always")
				@Config.Comment("If true, shows the thirst and hydration values of drinks in tooltip automatically")
				public boolean ALWAYS_SHOW_DRINK_VALUES_TOOLTIP = true;

				@Config.Name("Show Thirst Stats in F3")
				@Config.LangKey("show.thirst.stats.in.debug.overlay")
				@Config.Comment("If true, adds a line that shows your thirst, hydration, and exhaustion in the F3 debug overlay")
				public boolean SHOW_THIRST_DEBUG_INFO = true;

				@Config.Name("Thirst Debug Info Format")
				@Config.LangKey("thirst.debug.info.format")
				@Config.Comment("Custom format for the thirst stats debug info (F3).\nPlaceholders: %t (thirst), %s (saturation/hydration), %e (exhaustion), %eM (max exhaustion)")
				public String THIRST_DEBUG_INFO_FORMAT = "thirst: %t, hydration: %s, exh: %e/%eM";
			}
		}

		@Config.Name("Use Modern Tooltip Rendering")
		@Config.LangKey("use.modern.tooltip")
		@Config.Comment("true  – draw bars directly inside the tooltip\nfalse – draw on a separate floating background (legacy)")
		public boolean USE_MODERN_TOOLTIP = true;

		@Config.Name("Show Food Values in Tooltip")
		@Config.LangKey("show.food.values.in.tooltip")
		@Config.Comment("If true, shows the hunger and saturation values of food in its tooltip while holding SHIFT")
		public boolean SHOW_FOOD_VALUES_IN_TOOLTIP = false;

		@Config.Name("Show Food Values in Tooltip Always")
		@Config.LangKey("show.food.values.in.tooltip.always")
		@Config.Comment("If true, shows the hunger and saturation values of food in its tooltip automatically (without needing to hold SHIFT)")
		public boolean ALWAYS_SHOW_FOOD_VALUES_TOOLTIP = true;

		@Config.Name("Show Saturation HUD Overlay")
		@Config.LangKey("show.saturation.hud.overlay")
		@Config.Comment("If true, shows your current saturation level overlayed on the hunger bar")
		public boolean SHOW_SATURATION_OVERLAY = true;

		@Config.Name("Show Food Values HUD Overlay")
		@Config.LangKey("show.food.values.hud.overlay")
		@Config.Comment("If true, shows the hunger (and saturation if show.saturation.hud.overlay is true) that would be restored by food you are currently holding")
		public boolean SHOW_FOOD_VALUES_OVERLAY = true;

		@Config.Name("Show Exhaustion HUD Underlay")
		@Config.LangKey("show.food.exhaustion.hud.underlay")
		@Config.Comment("If true, shows your food exhaustion as a progress bar behind the hunger bars")
		public boolean SHOW_FOOD_EXHAUSTION_UNDERLAY = true;

		@Config.Name("Show Food Health HUD Overlay")
		@Config.LangKey("show.food.health.hud.overlay")
		@Config.Comment("SHOW_FOOD_HEALTH_HUD_OVERLAY")
		public boolean SHOW_FOOD_HEALTH_HUD_OVERLAY = false;

		@Config.Name("Show Vanilla Animations Overlay")
		@Config.LangKey("show.vanilla.animations.overlay")
		@Config.Comment("If true, hunger/health overlay will shake to match Minecraft's icon animations")
		public boolean SHOW_VANILLA_ANIMATION_OVERLAY = true;

		@Config.Name("Show Food Stats in F3")
		@Config.LangKey("show.food.stats.in.debug.overlay")
		@Config.Comment("If true, adds a line that shows your hunger, saturation, and exhaustion level in the F3 debug overlay")
		public boolean SHOW_FOOD_DEBUG_INFO = true;

		@Config.Name("Custom Saturation Color")
		@Config.LangKey("custom.saturation.color")
		@Config.Comment("Set a custom color for the saturation HUD/Tooltip overlay using hex color code")
		public String CUSTOM_COLOR = "#FFDF00";

		@Config.Name("Use Custom Saturation Color")
		@Config.LangKey("use.custom.saturation.color")
		@Config.Comment("Enable to use the custom saturation color instead of default")
		public boolean USE_CUSTOM_COLOR = false;

		@Config.Name("Recipe Book Tooltip fix")
		@Config.LangKey("recipe.book.tooltip.fix")
		@Config.Comment("Apply a small fix in the recipe book, necessary for the correct display of tooltips for modern tooltip mode")
		public boolean RECIPE_BOOK_TOOLTIP_FIX = true;

		@Config.Name("Update Overlays When Game Is Paused")
		@Config.LangKey("update.overlay.pause")
		@Config.Comment("If true, HUD overlays animations will be updated when the game is paused")
		public boolean UPDATE_OVERLAY_ON_PAUSE = false;

		@Config.Name("Max Hud Overlay Flash Alpha")
		@Config.LangKey("max.flash.alpha")
		@Config.Comment("Alpha value of the flashing icons at their most visible point (1.0 = fully opaque, 0.0 = fully transparent)")
		@Config.RangeDouble(min = 0.0, max = 1.0)
		public float MAX_HUD_OVERLAY_FLASH_ALPHA = 0.65f;

		@Config.Name("Debug Info Format")
		@Config.LangKey("debug.info.format")
		@Config.Comment("Custom format for the food stats debug info (F3).\nPlaceholders: %h (hunger)\n%s (saturation)\\n%e (exhaustion)\n%eM (max exhaustion)")
		public String DEBUG_INFO_FORMAT = "hunger: %h, sat: %s, exh: %e/%eM";

		public RGB getColor()
		{
			if (cachedColor == null) {
				cachedColor = USE_CUSTOM_COLOR ? fromHex(CUSTOM_COLOR) : new RGB(1f, 1f, 1f);
			}
			return cachedColor;
		}

		public ResourceLocation getIcons()
		{
			return USE_CUSTOM_COLOR ? LemonSkin.grayIcons : LemonSkin.ICONS;
		}
	}

	public static class RGB
	{
		private final float red, green, blue;

		public RGB(float red, float green, float blue)
		{
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		public float getRed() { return red; }
		public float getGreen() { return green; }
		public float getBlue() { return blue; }
	}

	public static RGB fromHex(String hex)
	{
		if (hex == null) return new RGB(1f, 1f, 1f);

		try {
			String cleanHex = hex.trim();
			if (cleanHex.startsWith("#")) cleanHex = cleanHex.substring(1);
			if (cleanHex.length() != 6) return new RGB(1f, 1f, 1f);

			int r = Integer.parseInt(cleanHex.substring(0, 2), 16);
			int g = Integer.parseInt(cleanHex.substring(2, 4), 16);
			int b = Integer.parseInt(cleanHex.substring(4, 6), 16);

			return new RGB(r / 255f, g / 255f, b / 255f);
		} catch (Exception e) {
			return new RGB(1f, 1f, 1f);
		}
	}

	@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
	public static class ConfigSyncHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(Tags.MOD_ID)) {
				ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
				ModConfig.CLIENT.cachedColor = null;
			}
		}
	}
}