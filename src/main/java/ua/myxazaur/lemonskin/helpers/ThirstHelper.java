package ua.myxazaur.lemonskin.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import ua.myxazaur.lemonskin.LemonSkin;

public class ThirstHelper
{
    public static float getMaxExhaustion(EntityPlayer player)
    {
        if (LemonSkin.hasSimpleDifficulty)
            return SimpleDifficultyHelper.getMaxThirstExhaustion();
        return 4.0f;
    }

    public static float getExhaustion(EntityPlayer player)
    {
        if (LemonSkin.hasSimpleDifficulty)
            return SimpleDifficultyHelper.getThirstExhaustion(player);
        return 0f;
    }

    public static void setExhaustion(EntityPlayer player, float exhaustion)
    {
        if (LemonSkin.hasSimpleDifficulty)
            SimpleDifficultyHelper.setThirstExhaustion(player, exhaustion);
    }

    public static int getThirstLevel(EntityPlayer player)
    {
        if (LemonSkin.hasSimpleDifficulty)
            return SimpleDifficultyHelper.getThirstLevel(player);
        return 20;
    }

    public static float getSaturation(EntityPlayer player)
    {
        if (LemonSkin.hasSimpleDifficulty)
            return SimpleDifficultyHelper.getThirstSaturation(player);
        return 0f;
    }

    public static boolean canDrink(EntityPlayer player)
    {
        if (LemonSkin.hasSimpleDifficulty)
            return SimpleDifficultyHelper.canDrink(player);
        return false;
    }

    public static boolean isDrink(ItemStack stack)
    {
        if (LemonSkin.hasSimpleDifficulty)
            return SimpleDifficultyHelper.isDrink(stack);
        return false;
    }

    public static boolean isDirtyDrink(ItemStack stack)
    {
        if (!LemonSkin.hasSimpleDifficulty) return false;

        SimpleDifficultyHelper.DrinkValues values = SimpleDifficultyHelper.getDrinkValues(stack);
        return values != null && values.isDirty();
    }

    public static boolean hasThirstyEffect(EntityPlayer player)
    {
        if (LemonSkin.hasSimpleDifficulty)
            return SimpleDifficultyHelper.hasThirstyEffect(player);
        return false;
    }
}