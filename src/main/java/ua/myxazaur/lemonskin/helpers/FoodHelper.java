package ua.myxazaur.lemonskin.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import ua.myxazaur.lemonskin.LemonSkin;

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
