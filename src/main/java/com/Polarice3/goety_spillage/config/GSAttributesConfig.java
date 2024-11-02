package com.Polarice3.goety_spillage.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class GSAttributesConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> BoundFreakagerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BoundFreakagerArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> BoundFreakagerDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BoundFreakagerScytheDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BoundFreakagerAxeDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> RagnoServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> RagnoServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> RagnoServantDamage;

    static {
        BUILDER.push("Attributes");
            BUILDER.push("Summoned Mobs");
                BUILDER.push("Bound Freakager");
                BoundFreakagerHealth = BUILDER.comment("How much Max Health Bound Freakagers have, Default: 160.0")
                        .defineInRange("boundFreakagerHealth", 160.0, 1.0, Double.MAX_VALUE);
                BoundFreakagerArmor = BUILDER.comment("How much natural armor points Bound Freakagers have, Default: 0.0")
                        .defineInRange("boundFreakagerArmor", 0.0, 0.0, Double.MAX_VALUE);
                BoundFreakagerDamage = BUILDER.comment("How much damage Bound Freakagers deals, Default: 5.0")
                        .defineInRange("boundFreakagerDamage", 5.0, 1.0, Double.MAX_VALUE);
                BoundFreakagerScytheDamage = BUILDER.comment("How much damage Bound Freakagers' Scythe deals, Default: 5.0")
                        .defineInRange("boundFreakagerScytheDamage", 5.0, 1.0, Double.MAX_VALUE);
                BoundFreakagerAxeDamage = BUILDER.comment("How much damage Bound Freakagers' Axes deals, Default: 8.0")
                        .defineInRange("boundFreakagerAxeDamage", 8.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Ragno Servant");
                RagnoServantHealth = BUILDER.comment("How much Max Health Ragno Servants have, Default: 200.0")
                        .defineInRange("ragnoServantHealth", 200.0, 1.0, Double.MAX_VALUE);
                RagnoServantArmor = BUILDER.comment("How much natural armor points Ragno Servants have, Default: 0.0")
                        .defineInRange("ragnoServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                RagnoServantDamage = BUILDER.comment("How much damage Ragno Servants deals, Default: 5.0")
                        .defineInRange("ragnoServantDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
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
