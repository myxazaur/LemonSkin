package ua.myxazaur.lemonskin.mixin.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.helpers.*;
import ua.myxazaur.lemonskin.client.compat.ThirstTooltipHandler;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This mixin is intended to reserve space in the tooltip for Modern Tooltip mode
 * All other logic is implemented in {@link ua.myxazaur.lemonskin.client.TooltipOverlayHandler}
 * and {@link ua.myxazaur.lemonskin.client.compat.ThirstTooltipHandler}
 */
@Mixin(value = GuiUtils.class, remap = false)
public abstract class GuiUtilsMixin
{
    @ModifyVariable(method = "drawHoveringText(Lnet/minecraft/item/ItemStack;Ljava/util/List;IIIIILnet/minecraft/client/gui/FontRenderer;)V",
            at = @At(value = "STORE", ordinal = 0),
            name = "tooltipTextWidth")
    private static int modifyTooltipTextWidth(int tooltipTextWidth, @Nonnull ItemStack stack, List<String> textLines)
    {
        // Check for food tooltip width
        if (TooltipHelper.shouldShowModernTooltip(stack))
        {
            int spacesNeeded = lemonSkin$getFoodSpacesNeeded(stack) * 4;
            tooltipTextWidth = Math.max(tooltipTextWidth, spacesNeeded);
        }

        // Check for drink tooltip width (SimpleDifficulty)
        if (ThirstTooltipHandler.shouldShowDrinkTooltip(stack))
        {
            int spacesNeeded = lemonSkin$getDrinkSpacesNeeded(stack) * 4;
            tooltipTextWidth = Math.max(tooltipTextWidth, spacesNeeded);
        }

        return tooltipTextWidth;
    }

    @Unique
    private static int lemonSkin$getFoodSpacesNeeded(ItemStack stack)
    {
        if (!FoodHelper.isFood(stack)) return 0;

        EntityPlayer player = Minecraft.getMinecraft().player;
        FoodHelper.BasicFoodValues base   = FoodHelper.getDefaultFoodValues(stack);
        FoodHelper.BasicFoodValues actual = FoodHelper.getModifiedFoodValues(stack, player);

        if (LemonSkin.hasAppleCore) {
            base   = AppleCoreHelper.getFoodValuesForDisplay(base, player);
            actual = AppleCoreHelper.getFoodValuesForDisplay(actual, player);
        }
        base   = BetterWithModsHelper.getFoodValuesForDisplay(base);
        actual = BetterWithModsHelper.getFoodValuesForDisplay(actual);

        return lemonSkin$calculateSpacesNeeded(base.hunger, actual.hunger,
                base.getSaturationIncrement(), actual.getSaturationIncrement());
    }

    @Unique
    private static int lemonSkin$getDrinkSpacesNeeded(ItemStack stack)
    {
        if (!LemonSkin.hasSimpleDifficulty) return 0;
        if (!ThirstHelper.isDrink(stack)) return 0;

        SimpleDifficultyHelper.DrinkValues values = SimpleDifficultyHelper.getDrinkValues(stack);
        if (values == null) return 0;

        int thirstBars = (int) Math.ceil(Math.abs(values.thirst) / 2f);
        int satBars = values.saturation > 0 ? (int) Math.ceil(values.saturation / 2f) : 0;

        float scale = 2.2f;
        float thirstLen = thirstBars <= 10 ? thirstBars * scale : 2;
        float satLen = satBars <= 10 ? satBars * scale * 0.8f : 2;

        return (int) Math.ceil(Math.max(thirstLen, satLen));
    }

    @Unique
    private static int lemonSkin$calculateSpacesNeeded(int baseVal, int actualVal, float baseSat, float actualSat)
    {
        int biggestVal = Math.max(baseVal, actualVal);
        float biggestSat = Math.max(baseSat, actualSat);

        int valBars = (int) Math.ceil(Math.abs(biggestVal) / 2f);
        int satBars = biggestSat > 0 ? (int) Math.ceil(Math.abs(biggestSat) / 2f) : 0;

        float scale = 2.2f;
        float valLen = valBars <= 10 ? valBars * scale : 2;
        float satLen = satBars <= 10 ? satBars * scale * 0.8f : 2;

        return (int) Math.ceil(Math.max(valLen, satLen));
    }
}