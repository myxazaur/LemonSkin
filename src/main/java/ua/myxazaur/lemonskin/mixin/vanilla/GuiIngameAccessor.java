package ua.myxazaur.lemonskin.mixin.vanilla;

import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiIngame.class)
public interface GuiIngameAccessor
{
    @Accessor("updateCounter")
    int getUpdateCounter();
}
