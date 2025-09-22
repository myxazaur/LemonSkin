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
import ua.myxazaur.lemonskin.helpers.AppleCoreHelper;
import ua.myxazaur.lemonskin.helpers.BetterWithModsHelper;
import ua.myxazaur.lemonskin.helpers.FoodHelper;

import javax.annotation.Nonnull;
import java.util.List;

@Mixin(GuiUtils.class)
public abstract class GuiUtilsMixin {

    @ModifyVariable(method = "drawHoveringText(Lnet/minecraft/item/ItemStack;Ljava/util/List;IIIIILnet/minecraft/client/gui/FontRenderer;)V",
            at = @At(value = "STORE", ordinal = 0),
            name = "tooltipTextWidth", remap = false)
    private static int modifyTooltipTextWidth(int tooltipTextWidth, @Nonnull ItemStack stack, List<String> textLines) {
        int spacesNeeded = lemonSkin$getSpacesNeeded(stack) * 4;
        return Math.max(tooltipTextWidth, spacesNeeded);
    }

    @Unique
    private static int lemonSkin$getSpacesNeeded(ItemStack stack) {
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

        return lemonSkin$getSpacesNeeded(base, actual);
    }

    @Unique
    private static int lemonSkin$getSpacesNeeded(FoodHelper.BasicFoodValues base, FoodHelper.BasicFoodValues actual) {
        int   biggestHunger = Math.max(base.hunger, actual.hunger);
        float biggestSatInc = Math.max(base.getSaturationIncrement(), actual.getSaturationIncrement());

        int hungerBars = (int) Math.ceil(Math.abs(biggestHunger) / 2f);
        int satBars    = (int) Math.max(1, Math.ceil(Math.abs(biggestSatInc) / 2f));

        float scale     = 2.2f;
        float hungerLen = hungerBars < 10 ? hungerBars * scale : 2;
        float satLen    = satBars < 10 ? satBars * scale * 0.8f : 2;

        return (int) Math.ceil(Math.max(hungerLen, satLen));
    }
}