package ua.myxazaur.lemonskin.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import ua.myxazaur.lemonskin.LemonSkin;

import java.lang.reflect.Field;

public class HungerHelper
{
	protected static final Field foodExhaustion = ReflectionHelper.findField(FoodStats.class, "foodExhaustionLevel", "field_75126_c", "c");

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

		try
		{
			return foodExhaustion.getFloat(player.getFoodStats());
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void setExhaustion(EntityPlayer player, float exhaustion)
	{
		if (LemonSkin.hasAppleCore)
		{
			AppleCoreHelper.setExhaustion(player, exhaustion);
			return;
		}

		try
		{
			foodExhaustion.setFloat(player.getFoodStats(), exhaustion);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
