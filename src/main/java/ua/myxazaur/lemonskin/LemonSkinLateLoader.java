package ua.myxazaur.lemonskin;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

@Optional.Interface(modid="mixinbooter", iface="zone.rong.mixinbooter.ILateMixinLoader")
public class LemonSkinLateLoader implements ILateMixinLoader
{
    public List<String> getMixinConfigs()
    {
        return Collections.singletonList("mixins.jei_compat.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig)
    {
        switch (mixinConfig)
        {
            case "mixins.jei_compat.json":
                Loader.isModLoaded("jei");
            default:
                return true;
        }
    }
}