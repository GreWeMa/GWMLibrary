package dev.gwm.spongeplugin.library.superobject.randommanager;

import java.util.List;

public interface IDefaultRandombles extends
        LevelRandomManager.LevelRandomable,
        WeightRandomManager.WeightRandomable,
        AbsoluteRandomManager.AbsoluteRandomable {

    @Override
    List<? extends IDefaultRandombles> getChildren();
}
