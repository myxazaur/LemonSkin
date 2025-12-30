package ua.myxazaur.lemonskin.helpers;

import net.minecraft.item.ItemStack;
import ua.myxazaur.lemonskin.ModConfig;

import java.util.List;

public class TooltipHelper
{
    public static void reserveFoodTooltipSpace(List<String> tooltip, ItemStack stack)
    {
        if (!shouldShowFoodTooltip(stack) || !FoodHelper.isFood(stack)) return;

        tooltip.add("\u00A0");
        tooltip.add("\u00A0");
    }

    public static boolean shouldShowFoodTooltip(ItemStack stack)
    {
        return FoodHelper.isFood(stack) && ((ModConfig.CLIENT.SHOW_FOOD_VALUES_IN_TOOLTIP && KeyHelper.isShiftKeyDown()) ||
                        ModConfig.CLIENT.ALWAYS_SHOW_FOOD_VALUES_TOOLTIP);
    }

    public static boolean shouldShowModernTooltip(ItemStack stack)
    {
        return shouldShowFoodTooltip(stack) && ModConfig.CLIENT.USE_MODERN_TOOLTIP;
    }
}
