package com.erzbir.numeron.core.processor;

import com.erzbir.numeron.annotation.*;
import com.erzbir.numeron.api.processor.Processor;
import com.erzbir.numeron.core.context.AppContext;
import com.erzbir.numeron.core.filter.annotation.MessageAnnotationChannelFilter;
import com.erzbir.numeron.core.handler.factory.ExecutorFactory;
import com.erzbir.numeron.utils.NumeronLogUtil;
import kotlinx.coroutines.CoroutineScope;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Erzbir
 * @Date: 2022/11/18 15:10
 * <p>
 * 此类为消息处理类, 从bean容器中获取有特定注解的bean, 并根据方法上的注解执行 过滤channel/执行对应方法等
 * </p>
 */
@SuppressWarnings("unused")
public class MessageAnnotationProcessor implements Processor {
    public static EventChannel<?> channel;
    public static Map<String, Method> methodMap = new HashMap<>(4);

    static {
        Method[] methods = Message.class.getDeclaredMethods();
        Arrays.stream(methods).forEach(method -> methodMap.put(method.getName().replaceFirst("\\(.*\\)", ""), method));
    }

    /**
     * 通过过滤监听, 最终过滤到一个确定的事件, 过滤规则由注解标记
     *
     * @param channel    未过滤的监听频道
     * @param annotation 消息处理注解, 使用泛型代替实际的注解, 这样做的目的是减少代码量, 用反射的方式还原注解
     * @return EventChannel
     */
    @NotNull
    private <E extends Annotation> EventChannel<? extends net.mamoe.mirai.event.Event> toFilter(@NotNull EventChannel<MessageEvent> channel, E annotation) {
        if (!(annotation instanceof Message)) {
            return channel;
        }
        return new MessageAnnotationChannelFilter().setAnnotation((Message) annotation).filter(channel);
    }

    /**
     * @param bean       bean对象
     * @param method     反射获取到的bean对象的方法
     * @param channel    过滤的channel
     * @param annotation 消息处理注解
     */
    private <E extends Annotation> void execute(Object bean, Method method, @NotNull EventChannel<? extends net.mamoe.mirai.event.Event> channel, E annotation) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ExecutorFactory.INSTANCE.create(annotation)
                .getExecute()
                .execute(method, bean, channel, annotation);
    }

    private void bindScope(EventChannel<?> channel, CoroutineScope scope) {
        AppContext.INSTANCE.addBean(scope);
        channel.parentScope(scope);
    }

    private CoroutineScope getCoroutineScope(Class<?> beanClass, Scope scope) {
        CoroutineScope coroutineScope = DefaultScope.INSTANCE;
        if (scope != null) {
            Class<? extends CoroutineScope> value = scope.value();
            String name = scope.name();
            try {
                if (!value.equals(DefaultScope.class)) {
                    Constructor<? extends CoroutineScope> constructor = value.getConstructor();
                    constructor.setAccessible(true);
                    coroutineScope = constructor.newInstance();
                    AppContext.INSTANCE.addBean("defaultScope", coroutineScope);
                }
                if (!name.isEmpty()) {
                    AppContext.INSTANCE.addBean(name, coroutineScope);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                NumeronLogUtil.logger.error("ERROR", e);
                throw new RuntimeException(e);
            }
        }
        return coroutineScope;
    }

    /**
     * @param annotation 注解
     * @return 是否是需要的注解
     */
    private boolean isNeededAnnotation(Annotation annotation) {
        return annotation instanceof Message || annotation instanceof Event || annotation instanceof Scope;
    }

    /**
     * 执行消息处理方法的注册方法
     *
     * @param bean bean 对象
     */
    private void registerMethods(Object bean) {
        Class<?> beanClass = bean.getClass();
        String name = beanClass.getName();
        NumeronLogUtil.info("扫瞄到 " + name);
        Scope scope = beanClass.getAnnotation(Scope.class);
        EventChannel<MessageEvent> eventChannel;
        CoroutineScope coroutineScope = DefaultScope.INSTANCE;
        coroutineScope = getCoroutineScope(beanClass, scope);
        eventChannel = channel.filterIsInstance(MessageEvent.class).parentScope(coroutineScope);
        for (Method method : beanClass.getDeclaredMethods()) {
            Arrays.stream(method.getAnnotations())
                    .filter(this::isNeededAnnotation).forEach(annotation -> {
                        String s = Arrays.toString(method.getParameterTypes())
                                .replaceAll("\\[", "(")
                                .replaceAll("]", ")");
                        method.setAccessible(true);
                        try {
                            execute(bean, method, toFilter(eventChannel, annotation), annotation);
                        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                            NumeronLogUtil.logger.error("ERROR", e);
                            throw new RuntimeException(e);
                        }
                        NumeronLogUtil.info(method.getName() + s + " 处理方法注册完毕");
                    });
        }
    }

    @Override
    public void onApplicationEvent() {
        AppContext context = AppContext.INSTANCE;
        MessageAnnotationProcessor.channel = GlobalEventChannel.INSTANCE;
        NumeronLogUtil.info("开始注册注解消息处理监听......");
        context.getBeansWithAnnotation(Listener.class).forEach((k, v) -> registerMethods(v));
        NumeronLogUtil.info("注解消息处理监听注册完毕\n");
    }

    @Override
    public void destroy() {

    }
}