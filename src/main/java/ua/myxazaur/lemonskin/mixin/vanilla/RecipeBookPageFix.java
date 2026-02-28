package ua.myxazaur.lemonskin.mixin.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.recipebook.GuiButtonRecipe;
import net.minecraft.client.gui.recipebook.RecipeBookPage;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ua.myxazaur.lemonskin.ModConfig;

import java.util.List;

@Mixin(RecipeBookPage.class)
public abstract class RecipeBookPageFix
{
    @Shadow private GuiButtonRecipe  hoveredButton;
    @Shadow private Minecraft        minecraft;

    @Redirect(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawHoveringText(Ljava/util/List;II)V"))
    private void renderTooltipRedirect(GuiScreen gui, List<String> textLines, int mouseX, int mouseY) {
        if (ModConfig.CLIENT.RECIPE_BOOK_TOOLTIP_FIX) {
            ItemStack stack = hoveredButton.getRecipe().getRecipeOutput();
            GuiUtils.drawHoveringText(stack, textLines, mouseX, mouseY, gui.width, gui.height, -1, minecraft.fontRenderer);
        } else {
            gui.drawHoveringText(textLines, mouseX, mouseY);
        }
    }
}
