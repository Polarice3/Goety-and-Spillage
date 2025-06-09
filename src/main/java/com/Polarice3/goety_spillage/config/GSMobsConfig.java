package com.Polarice3.goety_spillage.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class GSMobsConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ChagrinGhostArrow;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HinderCool;
    public static final ForgeConfigSpec.ConfigValue<Integer> HinderHealTime;
    public static final ForgeConfigSpec.ConfigValue<Integer> HinderCoolTime;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SniperGhostArrow;

    public static final ForgeConfigSpec.ConfigValue<Boolean> BoundFreakagerSun;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BoundEngineerSun;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ZombieAbsorberHeal;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieAbsorberHealTime;

    public static final ForgeConfigSpec.ConfigValue<Integer> PreserverDuration;

    public static final ForgeConfigSpec.ConfigValue<Boolean> VillagerHateRagno;

    static {
        BUILDER.push("Servants");
            BUILDER.push("Undead Servants");
            BoundFreakagerSun = BUILDER.comment("Whether Bound Freakagers burns in Sunlight, Default: false")
                    .define("boundFreakagerSun", false);
            BoundEngineerSun = BUILDER.comment("Whether Bound Engineers burns in Sunlight, Default: false")
                    .define("boundEngineerSun", false);
            ZombieAbsorberHeal = BUILDER.comment("Whether Zombie Absorbers can heal if summoned while wearing Necro Cape, Default: true")
                    .define("zombieAbsorberHeal", true);
            ZombieAbsorberHealTime = BUILDER.comment("How frequent Zombie Absorbers heal, count seconds, Default: 60")
                    .defineInRange("zombieAbsorberHealTime", 60, 0, Integer.MAX_VALUE);
            BUILDER.pop();
        ChagrinGhostArrow = BUILDER.comment("Whether Chagrin Sentries made by players or servants shoot ghost arrows, Default: true")
                .define("chagrinGhostArrow", true);
        HinderCool = BUILDER.comment("Whether Hinders made by players or servants will stop healing after a while and enter cooldown, Default: true")
                .define("hinderCool", true);
        HinderHealTime = BUILDER.comment("If 'hinderCool' is enabled, how long Hinders can heal before entering cooldown, count seconds, Default: 5")
                .defineInRange("hinderHealTime", 5, 0, Integer.MAX_VALUE);
        HinderCoolTime = BUILDER.comment("If 'hinderCool' is enabled, how long Hinders' cooldown last before they can heal again, count seconds, Default: 10")
                .defineInRange("hinderCoolTime", 10, 0, Integer.MAX_VALUE);
        SniperGhostArrow = BUILDER.comment("Whether Snipers made by players or servants shoot ghost arrows, Default: true")
                .define("sniperGhostArrow", true);
        PreserverDuration = BUILDER.comment("How long Preserver Servant's Hay Armor lasts, count seconds, Default: 300")
                .defineInRange("preserverDuration", 300, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.push("Villagers");
        VillagerHateRagno = BUILDER.comment("Having an owned Ragno, causes Villagers around the Player to have a negative Reputation, Default: true")
                .define("villagerHateRagno", true);
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
