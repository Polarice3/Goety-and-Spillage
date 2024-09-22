package com.Polarice3.goety_spillage.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class GSMainConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> FreakyGuiHorizontal;
    public static final ForgeConfigSpec.ConfigValue<Integer> FreakyGuiVertical;

    static {
        BUILDER.push("General");
        FreakyGuiHorizontal = BUILDER.comment("Move where the Freaky Robe indicator is located horizontally from its original position (- = Left, + = Right), Default: 0")
                .defineInRange("freakyGuiHorizontal", 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        FreakyGuiVertical = BUILDER.comment("Move where the Freaky Robe indicator is located vertically from its original position (- = Up, + = Down), Default: 0")
                .defineInRange("freakyGuiVertical", 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
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
