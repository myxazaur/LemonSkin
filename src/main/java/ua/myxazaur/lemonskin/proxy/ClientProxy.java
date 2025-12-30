package ua.myxazaur.lemonskin.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.client.*;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        DebugInfoHandler.init();
        TooltipOverlayHandler.init();
        GrayIconsReloader.init();
        LemonSkin.tickHandler = ClientTickHandler.init();
    }
}
