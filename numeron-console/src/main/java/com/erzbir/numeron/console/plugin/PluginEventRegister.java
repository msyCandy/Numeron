package com.erzbir.numeron.console.plugin;

import com.erzbir.numeron.annotation.*;
import com.erzbir.numeron.api.filter.annotation.MessageAnnotationChannelFilter;
import com.erzbir.numeron.console.exception.PluginIllegalException;
import com.erzbir.numeron.core.context.AppContext;
import com.erzbir.numeron.core.handler.excute.EventMethodExecute;
import com.erzbir.numeron.utils.NumeronLogUtil;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Erzbir
 * @Date: 2023/6/30 11:59
 */
//public class PluginEventRegister {
//    public static final PluginEventRegister INSTANCE = new PluginEventRegister();
//
//    private PluginEventRegister() {
//
//    }
//
//    public void register(PluginContext pluginContext) {
//        EventChannel<BotEvent> eventEventChannel = GlobalEventChannel.INSTANCE
//                .filterIsInstance(BotEvent.class).parentScope(pluginContext.getPlugin());
//        pluginContext.getClasses().forEach(t -> {
//            if (t.isAnnotationPresent(Listener.class)) {
//                Method[] declaredMethods = t.getDeclaredMethods();
//                for (Method declaredMethod : declaredMethods) {
//                    Handler handlerAnnotation = declaredMethod.getAnnotation(Handler.class);
//                    if (handlerAnnotation == null) {
//                        continue;
//                    }
//                    MessageFilter message = declaredMethod.getAnnotation(MessageFilter.class);
//                    if (message != null) {
//                        MessageAnnotationChannelFilter filter = new MessageAnnotationChannelFilter();
//                        filter.setAnnotation(message);
//                        Object object = AppContext.INSTANCE.getBean(t.getName());
//                        EventChannel<BotEvent> filterChannel = eventEventChannel.filter(event -> event instanceof MessageEvent messageEvent && filter.filter(messageEvent));
//                        EventMethodExecute.INSTANCE.execute(declaredMethod, object, filterChannel);
//                    }
//                }
//            }
//
//        });
//    }
//}
