package ua.myxazaur.lemonskin.helpers;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.item.ItemDrinkBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

import java.util.List;

public class SimpleDifficultyHelper
{
    public static class DrinkValues
    {
        public final int thirst;
        public final float saturation;
        public final float thirstyChance;

        public DrinkValues(int thirst, float saturation, float thirstyChance)
        {
            this.thirst = thirst;
            this.saturation = saturation;
            this.thirstyChance = thirstyChance;
        }

        public DrinkValues(int thirst, float saturation)
        {
            this(thirst, saturation, 0f);
        }

        public boolean isDirty()
        {
            return thirstyChance > 0f;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof DrinkValues)) return false;
            DrinkValues that = (DrinkValues) o;
            return thirst == that.thirst &&
                    Float.compare(that.saturation, saturation) == 0 &&
                    Float.compare(that.thirstyChance, thirstyChance) == 0;
        }

        @Override
        public int hashCode()
        {
            int result = thirst;
            result = 31 * result + (saturation != 0.0f ? Float.floatToIntBits(saturation) : 0);
            result = 31 * result + (thirstyChance != 0.0f ? Float.floatToIntBits(thirstyChance) : 0);
            return result;
        }
    }

    public static boolean isThirstEnabled()
    {
        try {
            return QuickConfig.isThirstEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isDrink(ItemStack stack)
    {
        if (stack == null || stack.isEmpty()) return false;
        return getDrinkValues(stack) != null;
    }

    public static DrinkValues getDrinkValues(ItemStack stack)
    {
        if (stack == null || stack.isEmpty()) return null;

        try {
            // Check JsonConfig for custom consumables
            List<JsonConsumableThirst> consumableList = JsonConfig.consumableThirst.get(
                    stack.getItem().getRegistryName().toString()
            );
            if (consumableList != null)
            {
                for (JsonConsumableThirst jct : consumableList)
                {
                    if (jct != null && jct.matches(stack))
                    {
                        return new DrinkValues(jct.amount, jct.saturation, jct.thirstyChance);
                    }
                }
            }

            // Check vanilla potions
            if (stack.getItem().equals(Items.POTIONITEM))
            {
                PotionType potionType = PotionUtils.getPotionFromItem(stack);
                if (potionType.getRegistryName() != null)
                {
                    String modDomain = potionType.getRegistryName().getNamespace();
                    if (modDomain.equals("minecraft"))
                    {
                        if (potionType.equals(PotionTypes.WATER) ||
                                potionType.equals(PotionTypes.AWKWARD) ||
                                potionType.equals(PotionTypes.MUNDANE) ||
                                potionType.equals(PotionTypes.THICK))
                        {
                            return new DrinkValues(
                                    ThirstEnum.NORMAL.getThirst(),
                                    ThirstEnum.NORMAL.getSaturation(),
                                    ThirstEnum.NORMAL.getThirstyChance()
                            );
                        }
                        else if (!potionType.equals(PotionTypes.EMPTY))
                        {
                            return new DrinkValues(
                                    ThirstEnum.POTION.getThirst(),
                                    ThirstEnum.POTION.getSaturation(),
                                    ThirstEnum.POTION.getThirstyChance()
                            );
                        }
                    }
                    else if (SDPotions.potionTypes.containsValue(potionType))
                    {
                        return new DrinkValues(
                                ThirstEnum.POTION.getThirst(),
                                ThirstEnum.POTION.getSaturation(),
                                ThirstEnum.POTION.getThirstyChance()
                        );
                    }
                }
            }

            // Check ItemDrinkBase
            if (stack.getItem() instanceof ItemDrinkBase)
            {
                ItemDrinkBase drink = (ItemDrinkBase) stack.getItem();
                return new DrinkValues(
                        drink.getThirstLevel(stack),
                        drink.getSaturationLevel(stack),
                        drink.getDirtyChance(stack)
                );
            }
        } catch (Exception ignored) {}

        return null;
    }

    public static boolean hasThirstyEffect(EntityPlayer player)
    {
        try {
            return player.isPotionActive(SDPotions.thirsty);
        } catch (Exception e) {
            return false;
        }
    }

    public static IThirstCapability getThirstCapability(EntityPlayer player)
    {
        try {
            return player.getCapability(SDCapabilities.THIRST, null);
        } catch (Exception e) {
            return null;
        }
    }

    public static float getThirstExhaustion(EntityPlayer player)
    {
        IThirstCapability cap = getThirstCapability(player);
        return cap != null ? cap.getThirstExhaustion() : 0f;
    }

    public static void setThirstExhaustion(EntityPlayer player, float exhaustion)
    {
        IThirstCapability cap = getThirstCapability(player);
        if (cap != null) cap.setThirstExhaustion(exhaustion);
    }

    public static float getMaxThirstExhaustion()
    {
        try {
            // SD config
            return (float) ModConfig.server.thirst.thirstExhaustionLimit;
        } catch (Exception e) {
            return 4.0f;
        }
    }

    public static int getThirstLevel(EntityPlayer player)
    {
        IThirstCapability cap = getThirstCapability(player);
        return cap != null ? cap.getThirstLevel() : 20;
    }

    public static float getThirstSaturation(EntityPlayer player)
    {
        IThirstCapability cap = getThirstCapability(player);
        return cap != null ? cap.getThirstSaturation() : 0f;
    }

    public static boolean canDrink(EntityPlayer player)
    {
        IThirstCapability cap = getThirstCapability(player);
        return cap != null && cap.isThirsty();
    }

    public static boolean useClassicHUD()
    {
        try {
            // SD config
            return ModConfig.client.classicHUDThirst;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean shouldDrawSaturation()
    {
        try {
            // SD config
            return ModConfig.client.drawThirstSaturation;
        } catch (Exception e) {
            return true;
        }
    }
}