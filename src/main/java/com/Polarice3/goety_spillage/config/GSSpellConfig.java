package com.Polarice3.goety_spillage.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class GSSpellConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> ImpishCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> ImpishDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> ImpishCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> SpiritHandCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SpiritHandDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SpiritHandCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> RequiemCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> RequiemDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> RequiemCoolDown;

    public static final ForgeConfigSpec.ConfigValue<Integer> SoulBeamCost;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulBeamDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> SoulBeamCoolDown;
    public static final ForgeConfigSpec.ConfigValue<Double> SoulBeamDamage;

    public static final ForgeConfigSpec.ConfigValue<Integer> EngineerMachineLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> FactoryServantLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> ZombieAbsorberLimit;
    public static final ForgeConfigSpec.ConfigValue<Integer> RagnoLimit;

    static {
        BUILDER.push("Spells");
            BUILDER.push("Impish Spell");
            ImpishCost = BUILDER.comment("Impish Spell Cost, Default: 32")
                    .defineInRange("impishCost", 32, 0, Integer.MAX_VALUE);
            ImpishDuration = BUILDER.comment("Time to cast Impish Spell, Default: 40")
                    .defineInRange("impishTime", 40, 0, 72000);
            ImpishCoolDown = BUILDER.comment("Impish Spell Cooldown, Default: 100")
                    .defineInRange("impishCoolDown", 100, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Spirit Hand Spell");
            SpiritHandCost = BUILDER.comment("Spirit Hand Spell Cost, Default: 64")
                    .defineInRange("spiritHandCost", 64, 0, Integer.MAX_VALUE);
            SpiritHandDuration = BUILDER.comment("Time to cast Spirit Hand Spell, Default: 40")
                    .defineInRange("spiritHandTime", 40, 0, 72000);
            SpiritHandCoolDown = BUILDER.comment("Spirit Hand Spell Cooldown, Default: 900")
                    .defineInRange("spiritHandCoolDown", 900, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Requiem Spell");
            RequiemCost = BUILDER.comment("Requiem Spell Cost, Default: 10000")
                    .defineInRange("requiemCost", 10000, 0, Integer.MAX_VALUE);
            RequiemDuration = BUILDER.comment("Time to cast Requiem Spell, Default: 55")
                    .defineInRange("requiemTime", 55, 20, 72000);
            RequiemCoolDown = BUILDER.comment("Requiem Spell Cooldown, Default: 200")
                    .defineInRange("requiemCoolDown", 200, 0, Integer.MAX_VALUE);
            BUILDER.pop();
            BUILDER.push("Soul Beam Spell");
            SoulBeamCost = BUILDER.comment("Soul Beam Spell Cost, Default: 64")
                    .defineInRange("soulBeamCost", 64, 0, Integer.MAX_VALUE);
            SoulBeamDuration = BUILDER.comment("Time to cast Soul Beam Spell, Default: 100")
                    .defineInRange("soulBeamDuration", 100, 0, 72000);
            SoulBeamCoolDown = BUILDER.comment("Soul Beam Spell Cooldown, Default: 140")
                    .defineInRange("soulBeamCoolDown", 140, 0, Integer.MAX_VALUE);
            SoulBeamDamage = BUILDER.comment("How much base damage Soul Beam deals, Default: 4.0")
                    .defineInRange("soulBeamDamage", 4.0, 1.0, Double.MAX_VALUE);
            BUILDER.pop();
        BUILDER.pop();
        BUILDER.push("Servant Limits");
        EngineerMachineLimit = BUILDER.comment("How many Engineer Machines (Chagrin, Hinder, Factory) owned by a player or servant can be placed 100 blocks near them, Default: 3")
                .defineInRange("engineerMachineLimit", 3, 1, Integer.MAX_VALUE);
        FactoryServantLimit = BUILDER.comment("How many Factory Servants (Beeper, Sniper, Poker) made by a Factory owned by a player or servant can be placed 100 blocks near them, Default: 5")
                .defineInRange("factoryServantLimit", 5, 1, Integer.MAX_VALUE);
        ZombieAbsorberLimit = BUILDER.comment("Number of Zombie Absorbers that an individual player can have in total, Default: 2")
                .defineInRange("zombieAbsorberLimit", 2, 1, Integer.MAX_VALUE);
        RagnoLimit = BUILDER.comment("Number of Ragnos that an individual player can have in total, Default: 1")
                .defineInRange("ragnoLimit", 1, 1, Integer.MAX_VALUE);
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
