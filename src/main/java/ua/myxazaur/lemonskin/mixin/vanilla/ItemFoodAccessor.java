package ua.myxazaur.lemonskin.mixin.vanilla;

import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemFood.class)
public interface ItemFoodAccessor
{
    @Accessor("potionId")
    public PotionEffect getPotionId();
}
