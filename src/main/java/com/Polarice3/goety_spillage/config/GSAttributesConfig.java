package com.Polarice3.goety_spillage.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class GSAttributesConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> CrocofangServantHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> CrocofangServantArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> CrocofangServantDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> ZombieAbsorberHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieAbsorberArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> ZombieAbsorberDamage;

    public static final ForgeConfigSpec.ConfigValue<Double> BoundEngineerHealth;
    public static final ForgeConfigSpec.ConfigValue<Double> BoundEngineerArmor;
    public static final ForgeConfigSpec.ConfigValue<Double> BoundEngineerDamage;

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
                BUILDER.push("Crocofang Servant");
                CrocofangServantHealth = BUILDER.comment("How much Max Health Crocofang Servants have, Default: 40.0")
                        .defineInRange("crocofangServantHealth", 40.0, 1.0, Double.MAX_VALUE);
                CrocofangServantArmor = BUILDER.comment("How much natural armor points Crocofang Servants have, Default: 3.0")
                        .defineInRange("crocofangServantArmor", 3.0, 0.0, Double.MAX_VALUE);
                CrocofangServantDamage = BUILDER.comment("How much damage Crocofang Servants deals, Default: 8.0")
                        .defineInRange("crocofangServantDamage", 8.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Zombie Absorber");
                ZombieAbsorberHealth = BUILDER.comment("How much Max Health Zombie Absorbers have, Default: 200.0")
                        .defineInRange("zombieAbsorberHealth", 100.0, 1.0, Double.MAX_VALUE);
                ZombieAbsorberArmor = BUILDER.comment("How much natural armor points Zombie Absorbers have, Default: 0.0")
                        .defineInRange("zombieAbsorberArmor", 0.0, 0.0, Double.MAX_VALUE);
                ZombieAbsorberDamage = BUILDER.comment("How much damage Zombie Absorbers deals, Default: 15.0")
                        .defineInRange("zombieAbsorberDamage", 15.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
                BUILDER.push("Bound Engineer");
                BoundEngineerHealth = BUILDER.comment("How much Max Health Bound Engineers have, Default: 30.0")
                        .defineInRange("boundEngineerHealth", 30.0, 1.0, Double.MAX_VALUE);
                BoundEngineerArmor = BUILDER.comment("How much natural armor points Bound Engineers have, Default: 0.0")
                        .defineInRange("boundEngineerArmor", 0.0, 0.0, Double.MAX_VALUE);
                BoundEngineerDamage = BUILDER.comment("How much damage Bound Engineers deals, Default: 5.0")
                        .defineInRange("boundEngineerDamage", 5.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();
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
                RagnoServantHealth = BUILDER.comment("How much Max Health Ragno Servants have, Default: 180.0")
                        .defineInRange("ragnoServantHealth", 180.0, 1.0, Double.MAX_VALUE);
                RagnoServantArmor = BUILDER.comment("How much natural armor points Ragno Servants have, Default: 10.0")
                        .defineInRange("ragnoServantArmor", 10.0, 0.0, Double.MAX_VALUE);
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
