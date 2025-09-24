package ua.myxazaur.lemonskin.helpers;

import net.minecraft.entity.player.EntityPlayer;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.mixin.vanilla.FoodStatsAccessor;

public class HungerHelper
{
	public static float getMaxExhaustion(EntityPlayer player)
	{
		if (LemonSkin.hasAppleCore)
			return AppleCoreHelper.getMaxExhaustion(player);

		return 4.0f;
	}

	public static float getExhaustion(EntityPlayer player)
	{
		if (LemonSkin.hasAppleCore)
			return AppleCoreHelper.getExhaustion(player);

		return ((FoodStatsAccessor) player.getFoodStats()).getFoodExhaustionLevel();
	}

	public static void setExhaustion(EntityPlayer player, float exhaustion)
	{
		if (LemonSkin.hasAppleCore)
		{
			AppleCoreHelper.setExhaustion(player, exhaustion);
			return;
		}

        ((FoodStatsAccessor) player.getFoodStats()).setFoodExhaustionLevel(exhaustion);
	}
}
