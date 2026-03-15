package ua.myxazaur.lemonskin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.FoodStats;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.Tags;
import ua.myxazaur.lemonskin.helpers.HungerHelper;
import ua.myxazaur.lemonskin.helpers.SimpleDifficultyHelper;
import ua.myxazaur.lemonskin.helpers.ThirstHelper;

import java.text.DecimalFormat;

@SideOnly(Side.CLIENT)
public class DebugInfoHandler
{
    private static final DecimalFormat saturationDF = new DecimalFormat("#.##");
    private static final DecimalFormat exhaustionValDF = new DecimalFormat("0.00");
    private static final DecimalFormat exhaustionMaxDF = new DecimalFormat("#.##");

    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(new DebugInfoHandler());
    }

    @SubscribeEvent
    public void onTextRender(RenderGameOverlayEvent.Text textEvent)
    {
        if (textEvent.getType() != RenderGameOverlayEvent.ElementType.TEXT)
            return;

        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.gameSettings.showDebugInfo)
            return;

        // LemonSkin section
        if (ModConfig.CLIENT.SHOW_FOOD_DEBUG_INFO ||
                (LemonSkin.hasSimpleDifficulty &&
                SimpleDifficultyHelper.isThirstEnabled() &&
                ModConfig.CLIENT.MODS.SIMPLE_DIFFICULTY.SHOW_THIRST_DEBUG_INFO))
        {
            textEvent.getLeft().add("");
            textEvent.getLeft().add(TextFormatting.YELLOW + "[" + Tags.MOD_NAME + "]");
        }

        // Food debug info
        if (ModConfig.CLIENT.SHOW_FOOD_DEBUG_INFO)
        {
            FoodStats stats = mc.player.getFoodStats();
            float curExhaustion = HungerHelper.getExhaustion(mc.player);
            float maxExhaustion = HungerHelper.getMaxExhaustion(mc.player);

            String formattedText = formatFoodDebugText(
                    ModConfig.CLIENT.DEBUG_INFO_FORMAT,
                    stats.getFoodLevel(),
                    stats.getSaturationLevel(),
                    curExhaustion,
                    maxExhaustion
            );

            textEvent.getLeft().add(formattedText);
        }

        // Thirst debug info (SimpleDifficulty)
        if (LemonSkin.hasSimpleDifficulty &&
                SimpleDifficultyHelper.isThirstEnabled() &&
                ModConfig.CLIENT.MODS.SIMPLE_DIFFICULTY.SHOW_THIRST_DEBUG_INFO)
        {
            int thirstLevel = ThirstHelper.getThirstLevel(mc.player);
            float thirstSaturation = ThirstHelper.getSaturation(mc.player);
            float thirstExhaustion = ThirstHelper.getExhaustion(mc.player);
            float maxThirstExhaustion = ThirstHelper.getMaxExhaustion(mc.player);

            String thirstText = formatThirstDebugText(
                    ModConfig.CLIENT.MODS.SIMPLE_DIFFICULTY.THIRST_DEBUG_INFO_FORMAT,
                    thirstLevel,
                    thirstSaturation,
                    thirstExhaustion,
                    maxThirstExhaustion
            );

            textEvent.getLeft().add(thirstText);
        }
    }

    private String formatFoodDebugText(String format, int hunger, float saturation, float exhaustion, float maxExhaustion)
    {
        return format
                .replace("%h", String.valueOf(hunger))
                .replace("%s", saturationDF.format(saturation))
                .replace("%eM", exhaustionMaxDF.format(maxExhaustion))
                .replace("%e", exhaustionValDF.format(exhaustion));
    }

    private String formatThirstDebugText(String format, int thirst, float saturation, float exhaustion, float maxExhaustion)
    {
        return format
                .replace("%t", String.valueOf(thirst))
                .replace("%s", saturationDF.format(saturation))
                .replace("%eM", exhaustionMaxDF.format(maxExhaustion))
                .replace("%e", exhaustionValDF.format(exhaustion));
    }
}