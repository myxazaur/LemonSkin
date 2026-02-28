package ua.myxazaur.lemonskin.asm;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class LemonSkinLateLoader implements ILateMixinLoader
{
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.lemonskin.mantle.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig)
    {
        switch (mixinConfig)
        {
            case "mixins.lemonskin.mantle.json":
                return Loader.isModLoaded("mantle");
            default:
                return true;
        }
    }
}