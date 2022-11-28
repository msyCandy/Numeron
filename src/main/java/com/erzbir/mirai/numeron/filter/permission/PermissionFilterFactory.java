package com.erzbir.mirai.numeron.filter.permission;

import com.erzbir.mirai.numeron.filter.ChannelFilterInter;
import com.erzbir.mirai.numeron.filter.FilterFactory;

import static com.erzbir.mirai.numeron.enums.PermissionType.*;

/**
 * @author Erzbir
 * @Date: 2022/11/26 17:00
 */
public class PermissionFilterFactory implements FilterFactory {
    public static PermissionFilterFactory INSTANCE = new PermissionFilterFactory();

    private PermissionFilterFactory() {

    }

    /**
     * @param e 枚举子类
     * @return ChannelFilterInter
     */
    @Override
    public ChannelFilterInter create(Enum<?> e) {
        if (e.equals(ALL)) {
            return AllPermissionFilter.INSTANCE;
        } else if (e.equals(MASTER)) {
            return MasterPermissionFilter.INSTANCE;
        } else if (e.equals(WHITE)) {
            return WhitePermissionFilter.INSTANCE;
        }
        return null;
    }
}