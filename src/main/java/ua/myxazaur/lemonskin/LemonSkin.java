package ua.myxazaur.lemonskin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.myxazaur.lemonskin.client.*;
import ua.myxazaur.lemonskin.helpers.BetterWithModsHelper;
import ua.myxazaur.lemonskin.network.SyncHandler;
import ua.myxazaur.lemonskin.proxy.CommonProxy;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class LemonSkin {

    public static Logger Log = LogManager.getLogger(Tags.MOD_ID);
    public static ResourceLocation grayIcons;

    @SideOnly(Side.CLIENT)
    public static ClientTickHandler tickHandler;

    @SidedProxy(clientSide = "ua.myxazaur.lemonskin.proxy.ClientProxy",
                serverSide = "ua.myxazaur.lemonskin.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static boolean hasAppleCore = false;
    public static boolean hasAppleSkin = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
