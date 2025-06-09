package com.Polarice3.goety_spillage.init;

import com.Polarice3.Goety.api.entities.ally.illager.ITrainIllager;
import com.Polarice3.Goety.api.entities.ally.illager.IllagerType;
import com.Polarice3.goety_spillage.common.entities.ally.illager.train.GSIllagerType;

import java.util.ArrayList;
import java.util.List;

public class GSIllagerTypes {

    public static final List<IllagerType> NEW_ILLAGER_TYPES = new ArrayList<>();

    public static void addIllagers(){
        addIllager("GS_ILLAGERS", new GSIllagerType());
    }

    private static IllagerType addIllager(String name, ITrainIllager trainIllager) {
        IllagerType golemType = IllagerType.create(name, trainIllager);
        NEW_ILLAGER_TYPES.add(golemType);
        return golemType;
    }
}
