package ua.myxazaur.lemonskin.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.client.*;
import ua.myxazaur.lemonskin.client.compat.*;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        TooltipOverlayHandler.init();
        DebugInfoHandler.init();
        GrayIconsReloader.init();

        // SimpleDifficulty thirst tooltip
        if (LemonSkin.hasSimpleDifficulty) {
            ThirstTooltipHandler.init();
        }
    }
}