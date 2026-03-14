package ua.myxazaur.lemonskin.asm;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class LemonSkinLateLoader implements ILateMixinLoader
{
    private static final List<String> TARGET_MODS = Arrays.asList(
            "mantle",
            "simpledifficulty"
    );

    @Override
    public List<String> getMixinConfigs() {
        return TARGET_MODS.stream()
                .map(modid -> "mixins.lemonskin." + modid + ".json")
                .collect(Collectors.toList());
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        String[] parts = mixinConfig.split("\\.");
        if (parts.length >= 3) {
            String modid = parts[2];
            return Loader.isModLoaded(modid);
        }
        return true;
    }
}