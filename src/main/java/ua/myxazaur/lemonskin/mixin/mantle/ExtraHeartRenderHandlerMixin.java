package ua.myxazaur.lemonskin.mixin.mantle;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.client.ExtraHeartRenderHandler;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.ModConfig;
import ua.myxazaur.lemonskin.client.HUDOverlayRenderer;
import ua.myxazaur.lemonskin.client.compat.MantleHealthOverlayRenderer;
import ua.myxazaur.lemonskin.helpers.AppleCoreHelper;
import ua.myxazaur.lemonskin.helpers.FoodHelper;
import ua.myxazaur.lemonskin.helpers.HealthHelper;
import ua.myxazaur.lemonskin.mixin.vanilla.GuiIngameAccessor;

@Mixin(value = ExtraHeartRenderHandler.class, remap = false)
public abstract class ExtraHeartRenderHandlerMixin
{
    @Unique
    private int ls$cachedLeftHeight = 0;

    @Inject(method = "renderHealthbar", at = @At("HEAD"))
    private void ls$cacheLeftHeight(RenderGameOverlayEvent.Pre event, CallbackInfo ci) {
        ls$cachedLeftHeight = GuiIngameForge.left_height;
    }

    @Inject(
            method = "renderHealthbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lslimeknights/mantle/client/ExtraHeartRenderHandler;renderExtraHearts(IILnet/minecraft/entity/player/EntityPlayer;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void ls$onAfterHeartsRendered(
            RenderGameOverlayEvent.Pre event, CallbackInfo ci,
            @Local(name = "player") EntityPlayer player
    ) {
        if (!ModConfig.CLIENT.MODS.MANTLE) return;
        if (player == null) return;

        if (!HUDOverlayRenderer.shouldShowEstimatedHealth(player))
            return;

        Minecraft mc = Minecraft.getMinecraft();
        int updateCounter = ((GuiIngameAccessor) mc.ingameGUI).getUpdateCounter();

        ItemStack held = player.getHeldItemMainhand();
        if (!FoodHelper.isFood(held)) held = player.getHeldItemOffhand();
        if (!FoodHelper.isFood(held)) return;

        FoodHelper.BasicFoodValues values = FoodHelper.getModifiedFoodValues(held, player);
        if (LemonSkin.hasAppleCore)
            values = AppleCoreHelper.getFoodValuesForDisplay(values, player);

        PotionEffect effect = FoodHelper.getEffect(held);

        if (held.getItem() == Items.GOLDEN_APPLE) {
            if (held.getMetadata() > 0)
                effect = new PotionEffect(MobEffects.REGENERATION, 400, 1);
            else
                effect = new PotionEffect(MobEffects.REGENERATION, 100, 1);
        }

        float heal = HealthHelper.getEstimatedHealthIncrement(player, values, effect);
        if (heal <= 0) return;

        float currentHealth = player.getHealth();
        float newHealth = Math.min(currentHealth + heal, player.getMaxHealth());

        ScaledResolution resolution = event.getResolution();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();

        int left = width / 2 - 91;
        int top = height - ls$cachedLeftHeight;

        MantleHealthOverlayRenderer.drawHealthOverlay(
                currentHealth, newHealth, mc, left, top,
                LemonSkin.tickHandler.flashAlpha, updateCounter
        );
    }
}