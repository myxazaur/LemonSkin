package ua.myxazaur.lemonskin;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static ua.myxazaur.lemonskin.ModInfo.MODID;

@Config(modid = MODID)
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
		public boolean ALWAYS_SHOW_FOOD_VALUES_TOOLTIP = false;

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
	}

	@Mod.EventBusSubscriber(modid = MODID)
	public static class ConfigSyncHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(MODID)) {
				ConfigManager.sync(MODID, Config.Type.INSTANCE);
			}
		}
	}
}