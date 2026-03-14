package ua.myxazaur.lemonskin.mixin.simpledifficulty;

import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.client.gui.ThirstGui;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.client.AnimationHandler;
import ua.myxazaur.lemonskin.client.compat.ThirstHUDOverlayRenderer;
import ua.myxazaur.lemonskin.helpers.SimpleDifficultyHelper;
import ua.myxazaur.lemonskin.helpers.ThirstHelper;

@Mixin(value = ThirstGui.class, remap = false)
public abstract class ThirstGuiMixin
{
    @Shadow
    private int updateCounter;

    @Unique
    private static int ls$cachedRightHeight;

    @Inject(method = "onPreRenderGameOverlay",
            at = @At(value = "INVOKE",
                    target = "Lcom/charles445/simpledifficulty/client/gui/ThirstGui;renderThirst(IIIF)V"))
    private void preRenderThirst(RenderGameOverlayEvent.Pre event, CallbackInfo ci)
    {
        ls$cachedRightHeight = GuiIngameForge.right_height;

        if (!ModConfig.CLIENT.MODS.SIMPLE_DIFFICULTY.SHOW_THIRST_EXHAUSTION_UNDERLAY)
            return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();

        int left = width / 2 + 91;
        int top = height - ls$cachedRightHeight;

        ThirstHUDOverlayRenderer.drawThirstExhaustionOverlay(
                ThirstHelper.getExhaustion(player), mc, left, top, 1f
        );
    }

    @Inject(method = "onPreRenderGameOverlay", at = @At("TAIL"))
    private void postRenderThirst(RenderGameOverlayEvent.Pre event, CallbackInfo ci)
    {
        // Match SD's condition exactly
        if (event.getType() != ElementType.AIR) return;
        if (!QuickConfig.isThirstEnabled()) return;
        if (!SDCompatibility.defaultThirstDisplay) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        if (!ModConfig.CLIENT.MODS.SIMPLE_DIFFICULTY.SHOW_THIRST_VALUES_OVERLAY &&
                !ModConfig.CLIENT.MODS.SIMPLE_DIFFICULTY.SHOW_THIRST_SATURATION_OVERLAY)
            return;

        ItemStack held = player.getHeldItemMainhand();
        if (!ThirstHelper.isDrink(held)) held = player.getHeldItemOffhand();
        if (!ThirstHelper.isDrink(held)) return;

        if (!ThirstHelper.canDrink(player)) return;

        SimpleDifficultyHelper.DrinkValues values = SimpleDifficultyHelper.getDrinkValues(held);
        if (values == null || values.thirst <= 0) return;

        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();

        // Use cached right_height (before SD incremented it)
        int left = width / 2 + 91;
        int top = height - ls$cachedRightHeight;

        int thirstLevel = ThirstHelper.getThirstLevel(player);
        float saturationLevel = ThirstHelper.getSaturation(player);
        boolean isDirty = values.isDirty();

        // Draw predicted thirst overlay
        if (ModConfig.CLIENT.MODS.SIMPLE_DIFFICULTY.SHOW_THIRST_VALUES_OVERLAY)
        {
            ThirstHUDOverlayRenderer.drawThirstOverlay(
                    values.thirst, thirstLevel, mc, left, top,
                    AnimationHandler.getFlashAlpha(), isDirty, this.updateCounter
            );
        }

        // Draw predicted saturation overlay (only if SD draws saturation)
        if (ModConfig.CLIENT.MODS.SIMPLE_DIFFICULTY.SHOW_THIRST_SATURATION_OVERLAY &&
                SimpleDifficultyHelper.shouldDrawSaturation())
        {
            int newThirstValue = thirstLevel + values.thirst;
            float newSaturationValue = saturationLevel + values.saturation;

            float satToAdd = newSaturationValue > newThirstValue
                    ? newThirstValue - saturationLevel
                    : values.saturation;

            ThirstHUDOverlayRenderer.drawThirstSaturationOverlay(
                    satToAdd, saturationLevel, mc, left, top,
                    AnimationHandler.getFlashAlpha(), this.updateCounter
            );
        }
    }
}