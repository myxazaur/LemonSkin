package ua.myxazaur.lemonskin.mixin.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.recipebook.GuiButtonRecipe;
import net.minecraft.client.gui.recipebook.GuiRecipeOverlay;
import net.minecraft.client.gui.recipebook.RecipeBookPage;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(RecipeBookPage.class)
public abstract class RecipeBookPageFix {
    @Shadow private GuiButtonRecipe  hoveredButton;
    @Shadow private Minecraft        minecraft;
    @Shadow private GuiRecipeOverlay overlay;

    /**
     * @author myxazaur
     * @reason Provide actual ItemStack to RenderTooltipEvent
     */
    @Overwrite
    public void renderTooltip(int mouseX, int mouseY) {
        if (this.minecraft.currentScreen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
            GuiScreen gui          = minecraft.currentScreen;
            ItemStack stack        = hoveredButton.getRecipe().getRecipeOutput();
            List<String> textLines = new ArrayList<>(gui.getItemToolTip(stack));
            GuiUtils.drawHoveringText(stack, textLines, mouseX, mouseY, gui.width, gui.height, -1, minecraft.fontRenderer);
        }
    }
}
