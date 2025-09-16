package ua.myxazaur.lemonskin.mixin;

import mezz.jei.gui.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.helpers.TooltipHelper;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = TooltipRenderer.class, remap = false)
public abstract class JEI_TooltipRendererMixin
{
    @Inject(method = "drawHoveringText(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/Minecraft;Ljava/util/List;IIILnet/minecraft/client/gui/FontRenderer;)V",
    at = @At("HEAD"), remap = false)
    private static void drawHoveringText(ItemStack itemStack, Minecraft minecraft, List<String> textLines, int x, int y, int maxWidth, FontRenderer font, CallbackInfo ci)
    {
        try {
            if (!ModConfig.CLIENT.USE_MODERN_TOOLTIP) return;
            if (textLines != null && !textLines.isEmpty()) {
                List<String> mutable = new ArrayList<>(textLines);
                mutable.removeIf(line -> line != null && line.contains("\u00A0"));

                if (textLines.getClass().getName().contains("java.util.Collections")) textLines = mutable;
                else {
                    textLines.clear();
                    textLines.addAll(mutable);
                }

                TooltipHelper.reserveFoodTooltipSpace(textLines, itemStack);
            }
        } catch (Exception e) {
            LemonSkin.Log.error("Failed to process JEI tooltip", e);
        }
    }
}
