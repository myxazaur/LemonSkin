package ua.myxazaur.lemonskin.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import ua.myxazaur.lemonskin.LemonSkin;

import java.lang.reflect.Field;

public class FoodHelper
{
	public static class BasicFoodValues
	{
		public final int hunger;
		public final float saturationModifier;

		public BasicFoodValues(int hunger, float saturationModifier)
		{
			this.hunger = hunger;
			this.saturationModifier = saturationModifier;
		}

		public float getSaturationIncrement()
		{
			return hunger * saturationModifier * 2f;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof BasicFoodValues)) return false;

			BasicFoodValues that = (BasicFoodValues) o;

			return hunger == that.hunger && Float.compare(that.saturationModifier, saturationModifier) == 0;
		}

		@Override
		public int hashCode()
		{
			int result = hunger;
			result = 31 * result + (saturationModifier != +0.0f ? Float.floatToIntBits(saturationModifier) : 0);
			return result;
		}
	}

	public static boolean isFood(ItemStack itemStack)
	{
		if (LemonSkin.hasAppleCore)
			return AppleCoreHelper.isFood(itemStack);

		return itemStack.getItem() instanceof ItemFood;
	}

	public static boolean isRotten(ItemStack itemStack)
	{
		if (itemStack == null || itemStack.isEmpty())
		{
			return false;
		}

		if (!(isFood(itemStack)))
		{
			return false;
		}

		ItemFood itemFood = (ItemFood) itemStack.getItem();

		try
		{
			Field potionIdField = ReflectionHelper.findField(
					ItemFood.class, "potionId", "field_77851_ca");
			potionIdField.setAccessible(true);

			PotionEffect effect = (PotionEffect) potionIdField.get(itemFood);
			return effect != null && effect.getPotion() == MobEffects.HUNGER;
		}
		catch (IllegalAccessException | IllegalArgumentException e)
		{
			LemonSkin.Log.error("Error checking if food is rotten from ItemStack", e);
			return false;
		}
		catch (Exception e)
		{
			LemonSkin.Log.warn("Could not access potionId field for rotten food check");
			return false;
		}
	}

	public static BasicFoodValues getDefaultFoodValues(ItemStack itemStack)
	{
		if (LemonSkin.hasAppleCore)
			return AppleCoreHelper.getDefaultFoodValues(itemStack);

		ItemFood itemFood = (ItemFood) itemStack.getItem();
		int hunger = itemFood.getHealAmount(itemStack);
		float saturationModifier = itemFood.getSaturationModifier(itemStack);

		return new BasicFoodValues(hunger, saturationModifier);
	}

	public static BasicFoodValues getModifiedFoodValues(ItemStack itemStack, EntityPlayer player)
	{
		if (LemonSkin.hasAppleCore)
			return AppleCoreHelper.getModifiedFoodValues(itemStack, player);

		return getDefaultFoodValues(itemStack);
	}
}
