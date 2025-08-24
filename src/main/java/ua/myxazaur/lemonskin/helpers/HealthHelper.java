package ua.myxazaur.lemonskin.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;

public class HealthHelper
{
    public static final float REGEN_EXHAUSTION_INCREMENT = 6.0F;
    public static final float MAX_EXHAUSTION = 4.0F;

    public static float getEstimatedHealthIncrement(EntityPlayer player, FoodHelper.BasicFoodValues foodValues, PotionEffect effect)
    {
        if (!player.shouldHeal())
            return 0;

        FoodStats stats = player.getFoodStats();
        World world = player.world;

        int foodLevel = Math.min(stats.getFoodLevel() + foodValues.hunger, 20);
        float healthIncrement = 0;

        // health for natural regen
        if (foodLevel >= 18 && world != null && world.getGameRules().getBoolean("naturalRegeneration"))
        {
            float saturationLevel = Math.min(stats.getSaturationLevel() + foodValues.getSaturationIncrement(), (float) foodLevel);
            float exhaustionLevel = HungerHelper.getExhaustion(player);
            healthIncrement = getEstimatedHealthIncrement(foodLevel, saturationLevel, exhaustionLevel);
        }

        // health for regeneration effect
        if (effect != null && effect.getPotion() == MobEffects.REGENERATION)
        {
            int amplifier = effect.getAmplifier();
            int duration = effect.getDuration();

            healthIncrement += (float) Math.floor(duration / Math.max(50 >> amplifier, 1));
        }

        return healthIncrement;
    }

    public static float getEstimatedHealthIncrement(int foodLevel, float saturationLevel, float exhaustionLevel)
    {
        float health = 0;

        if (!Float.isFinite(exhaustionLevel) || !Float.isFinite(saturationLevel))
            return 0;

        while (foodLevel >= 18)
        {
            while (exhaustionLevel > MAX_EXHAUSTION)
            {
                exhaustionLevel -= MAX_EXHAUSTION;
                if (saturationLevel > 0)
                    saturationLevel = Math.max(saturationLevel - 1, 0);
                else
                    foodLevel -= 1;
            }

            if (foodLevel < 18)
                break;

            // Without this Float.compare, it's possible for this function to get stuck in an infinite loop
            if (foodLevel >= 20 && Float.compare(saturationLevel, Float.MIN_NORMAL) > 0)
            {
                // fast regen health - optimized calculation
                float limitedSaturationLevel = Math.min(saturationLevel, REGEN_EXHAUSTION_INCREMENT);
                float exhaustionUntilAboveMax = Math.nextUp(MAX_EXHAUSTION) - exhaustionLevel;
                int numIterationsUntilAboveMax = Math.max(1, (int) Math.ceil(exhaustionUntilAboveMax / limitedSaturationLevel));

                health += (limitedSaturationLevel / REGEN_EXHAUSTION_INCREMENT) * numIterationsUntilAboveMax;
                exhaustionLevel += limitedSaturationLevel * numIterationsUntilAboveMax;
            }
            else if (foodLevel >= 18)
            {
                // slow regen health
                health += 1;
                exhaustionLevel += REGEN_EXHAUSTION_INCREMENT;
            }
        }

        return health;
    }
}