package com.Polarice3.goety_spillage.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class GSItemConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> FreakyPotionSpin;
    public static final ForgeConfigSpec.ConfigValue<Double> FreakyHatRagnoReduce;

    static {
        BUILDER.push("Curios");
            BUILDER.push("Robes");
            FreakyPotionSpin = BUILDER.comment("Whether performing potion spin with Freaky Robe spins the wearer around, Default: false")
                    .define("freakyPotionSpin", false);
            FreakyHatRagnoReduce = BUILDER.comment("How much damage is divided when riding an unstunned Ragno Servant while wearing a Freaky Hat, Default: 3.5")
                    .defineInRange("freakyHatRagnoReduce", 3.5, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path))
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        file.load();
        config.setConfig(file);
    }
}
