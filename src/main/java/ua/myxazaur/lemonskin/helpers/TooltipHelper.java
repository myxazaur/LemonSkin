package ua.myxazaur.lemonskin.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.ModConfig;

import java.util.List;

public class TooltipHelper {
    public static void reserveFoodTooltipSpace(List<String> tooltip, ItemStack stack)
    {
        boolean shouldShow =
                (ModConfig.CLIENT.SHOW_FOOD_VALUES_IN_TOOLTIP && KeyHelper.isShiftKeyDown()) ||
                        ModConfig.CLIENT.ALWAYS_SHOW_FOOD_VALUES_TOOLTIP;
        if (!shouldShow || !FoodHelper.isFood(stack)) return;

        EntityPlayer player = Minecraft.getMinecraft().player;
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

        int biggestHunger   = Math.max(base.hunger, actual.hunger);
        float biggestSatInc = Math.max(base.getSaturationIncrement(), actual.getSaturationIncrement());

        int hungerBars = (int) Math.ceil(Math.abs(biggestHunger) / 2f);
        int satBars    = (int) Math.max(1, Math.ceil(Math.abs(biggestSatInc) / 2f));

        float scale = 2.2f;
        float hungerLen = hungerBars < 10 ? hungerBars * scale : 2;
        float satLen    = satBars < 10 ? satBars * scale * 0.8f : 2;
        int spacesNeeded = (int) Math.ceil(Math.max(hungerLen, satLen));

        StringBuilder sb = new StringBuilder("\u00A0");
        for (int i = 0; i < spacesNeeded; i++) sb.append("\u00A0");
        tooltip.add(sb.toString());
        tooltip.add(sb.toString());
    }
}
