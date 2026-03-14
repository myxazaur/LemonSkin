package ua.myxazaur.lemonskin.proxy;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ua.myxazaur.lemonskin.LemonSkin;
import ua.myxazaur.lemonskin.helpers.BetterWithModsHelper;
import ua.myxazaur.lemonskin.helpers.FoodEffectRegistry;
import ua.myxazaur.lemonskin.network.SyncHandler;

import static ua.myxazaur.lemonskin.LemonSkin.*;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event) {
        hasAppleCore = Loader.isModLoaded("applecore");
        hasAppleSkin = Loader.isModLoaded("appleskin");
        hasSimpleDifficulty = Loader.isModLoaded("simpledifficulty");

        if (hasAppleSkin) LemonSkin.Log.warn("AppleSkin detected. Better remove it");

        if (hasAppleCore) Log.info("AppleCore detected, enabling integration");
        if (hasSimpleDifficulty) Log.info("SimpleDifficulty detected, enabling thirst integration");

        FoodEffectRegistry.init();
        BetterWithModsHelper.init();
    }

    public void init(FMLInitializationEvent event) {
        SyncHandler.init();
    }
}
