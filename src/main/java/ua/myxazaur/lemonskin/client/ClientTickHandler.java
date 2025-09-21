package ua.myxazaur.lemonskin.client;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientTickHandler
{
    public float flashAlpha = 0f;
    public byte  alphaDir   = 1;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END)
            return;

        flashAlpha += alphaDir * 0.125f;
        if (flashAlpha >= 1.5f)
        {
            flashAlpha = 1f;
            alphaDir = -1;
        }
        else if (flashAlpha <= -0.5f)
        {
            flashAlpha = 0f;
            alphaDir = 1;
        }
    }
}
