package com.erzbir.mirai.numeron.controller;

import com.erzbir.mirai.numeron.config.GlobalConfig;
import com.erzbir.mirai.numeron.entity.BlackList;

import java.util.Objects;

/**
 * @author Erzbir
 * @Date: 2022/11/13 17:27
 */
public class BlackListAction extends Action {
    private static BlackListAction INSTANCE;

    private BlackListAction() {

    }

    public static BlackListAction getInstance() {
        return Objects.requireNonNullElseGet(INSTANCE, () -> INSTANCE = new BlackListAction());
    }

    @Override
    public void add(Object id) {
        BlackList.INSTANCE.add((Long) id);
    }

    @Override
    public String query(Object id) {
        if ((Long) id == 0L) {
            return GlobalConfig.blackList.toString();
        } else if (GlobalConfig.blackList.contains((Long) id)) {
            return "在黑名单中";
        }
        return "查无此人";
    }

    @Override
    public void remove(Object id) {
        BlackList.INSTANCE.remove((Long) id);
    }
}
