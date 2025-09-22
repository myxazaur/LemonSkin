package ua.myxazaur.lemonskin.helpers;

import net.minecraft.item.ItemStack;
import ua.myxazaur.lemonskin.ModConfig;

import java.util.List;

public class TooltipHelper {
    public static void reserveFoodTooltipSpace(List<String> tooltip, ItemStack stack)
    {
        boolean shouldShow =
                (ModConfig.CLIENT.SHOW_FOOD_VALUES_IN_TOOLTIP && KeyHelper.isShiftKeyDown()) ||
                        ModConfig.CLIENT.ALWAYS_SHOW_FOOD_VALUES_TOOLTIP;
        if (!shouldShow || !FoodHelper.isFood(stack)) return;

        tooltip.add("\u00A0");
        tooltip.add("\u00A0");
    }
}
