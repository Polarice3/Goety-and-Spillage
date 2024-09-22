package com.Polarice3.goety_spillage.common.capabilities.spillage;

public class SpillageImp implements ISpillage {
    private boolean casting = false;
    private boolean spinning = false;

    @Override
    public boolean isCasting() {
        return this.casting;
    }

    @Override
    public void setCasting(boolean casting) {
        this.casting = casting;
    }

    @Override
    public boolean isSpinning() {
        return this.spinning;
    }

    @Override
    public void setSpinning(boolean spin) {
        this.spinning = spin;
    }
}
