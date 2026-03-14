package ua.myxazaur.lemonskin.helpers;

import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registry for food items that grant healing effect not exposed via potionId
 */
public class FoodEffectRegistry
{
    private static final Map<Item, Function<ItemStack, PotionEffect>> HEALING_EFFECTS = new HashMap<>();

    public static void init()
    {
        // Golden Apple: Regeneration 2 for 100 ticks
        // Enchanted Golden Apple: Regeneration 2 for 400 ticks
        register(Items.GOLDEN_APPLE, stack ->
                new PotionEffect(MobEffects.REGENERATION, stack.getMetadata() > 0 ? 400 : 100, 1)
        );
    }

    /**
     * Register a healing effect provider for an item
     */
    public static void register(Item item, Function<ItemStack, PotionEffect> effectProvider)
    {
        HEALING_EFFECTS.put(item, effectProvider);
    }

    /**
     * Register a simple static effect for an item
     */
    public static void register(Item item, PotionEffect effect)
    {
        HEALING_EFFECTS.put(item, stack -> new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier()));
    }

    /**
     * Gets the healing/regeneration effect for the given food item
     */
    @Nullable
    public static PotionEffect getHealingEffect(ItemStack stack)
    {
        if (stack.isEmpty()) return null;

        Function<ItemStack, PotionEffect> provider = HEALING_EFFECTS.get(stack.getItem());
        return provider != null ? provider.apply(stack) : null;
    }

    /**
     * Checks if this item has a registered healing effect
     */
    public static boolean hasHealingEffect(ItemStack stack)
    {
        return !stack.isEmpty() && HEALING_EFFECTS.containsKey(stack.getItem());
    }
}