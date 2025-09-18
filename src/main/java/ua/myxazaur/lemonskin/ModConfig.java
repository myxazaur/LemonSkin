package ua.myxazaur.lemonskin;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Tags.MOD_ID)
public class ModConfig {

	@Config.Name("client")
	@Config.Comment("These config settings are client-side only")
	public static ClientCategory CLIENT = new ClientCategory();

	public static class ClientCategory {

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
		public boolean SHOW_FOOD_HEALTH_HUD_OVERLAY = false; // Still beta

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

		public RGB getColor()
		{
			return USE_CUSTOM_COLOR ? fromHex(CUSTOM_COLOR) : new RGB(1f, 1f, 1f);
		}

		public ResourceLocation getIcons()
		{
			//return new ResourceLocation(Tags.MOD_ID, "textures/icons.png");
			return USE_CUSTOM_COLOR ? LemonSkin.grayIcons
					:  new ResourceLocation(Tags.MOD_ID, "textures/icons.png");
		}
	}

	public static class RGB
	{
		private final float red, green, blue;

		public RGB (float red, float green, float blue)
		{
			this.red   = red;
			this.green = green;
			this.blue  = blue;
		}

		public float getRed() {
			return red;
		}

		public float getGreen() {
			return green;
		}

		public float getBlue() {
			return blue;
		}
	}

	public static RGB fromHex(String hex)
	{
		if (hex == null) {
			return new RGB(1f, 1f, 1f);
		}

		try {
			String cleanHex = hex.trim();
			if (cleanHex.startsWith("#")) {
				cleanHex = cleanHex.substring(1);
			}

			if (cleanHex.length() != 6) {
				return new RGB(1f, 1f, 1f);
			}

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
			}
		}
	}
}