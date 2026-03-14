package ua.myxazaur.lemonskin;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.myxazaur.lemonskin.client.*;
import ua.myxazaur.lemonskin.proxy.CommonProxy;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class LemonSkin {

    public static final Logger Log = LogManager.getLogger(Tags.MOD_ID);
    public static final ResourceLocation ICONS = new ResourceLocation(Tags.MOD_ID, "textures/icons.png");
    public static ResourceLocation grayIcons;

    @SidedProxy(clientSide = "ua.myxazaur.lemonskin.proxy.ClientProxy",
                serverSide = "ua.myxazaur.lemonskin.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static boolean hasAppleCore = false;
    public static boolean hasAppleSkin = false;
    public static boolean hasSimpleDifficulty = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}