package ua.myxazaur.lemonskin;

import net.minecraftforge.fml.common.Optional;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

@Optional.Interface(modid="mixinbooter", iface="zone.rong.mixinbooter.ILateMixinLoader")
public class LemonSkinLateLoader implements ILateMixinLoader {
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.lemonskin.json");
    }
}