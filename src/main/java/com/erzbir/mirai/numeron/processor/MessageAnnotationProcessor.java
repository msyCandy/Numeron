package com.erzbir.mirai.numeron.processor;

import com.erzbir.mirai.numeron.filter.MessageChannelFilter;
import com.erzbir.mirai.numeron.listener.Listener;
import com.erzbir.mirai.numeron.listener.massage.GroupMessage;
import com.erzbir.mirai.numeron.listener.massage.Message;
import com.erzbir.mirai.numeron.listener.massage.UserMessage;
import com.erzbir.mirai.numeron.processor.factory.ExecutorFactory;
import com.erzbir.mirai.numeron.utils.MiraiLogUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.BotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Erzbir
 * @Date: 2022/11/18 15:10
 * <p>
 * 此类为消息处理类, 从bean容器中获取有特定注解的bean, 并根据方法上的注解执行 过滤channel/执行对应方法等
 * </p>
 */
@Processor
@SuppressWarnings("unused")
public class MessageAnnotationProcessor implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
    public static ApplicationContext context;
    public static EventChannel<BotEvent> channel;
    public static Bot bot;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        MessageAnnotationProcessor.context = context;
    }

    /**
     * 通过过滤监听, 最终过滤到一个确定的事件, 过滤规则由注解标记
     *
     * @param channel    未过滤的监听频道
     * @param annotation 消息处理注解, 使用泛型代替实际的注解, 这样做的目的是减少代码量, 用反射的方式还原注解
     * @return EventChannel
     */
    @NotNull
    private <E extends Annotation> EventChannel<BotEvent> toFilter(@NotNull EventChannel<BotEvent> channel, E annotation) {
        return channel.filter(event -> MessageChannelFilter.INSTANCE.filter(event, annotation));
    }

    /**
     * @param bean       bean对象
     * @param method     反射获取到的bean对象的方法
     * @param channel    过滤的channel
     * @param annotation 消息处理注解, 使用泛型代替实际的注解, 这样做的目的是减少代码量, 用反射的方式还原注解
     */
    private <E extends Annotation> void execute(Object bean, Method method, @NotNull EventChannel<BotEvent> channel, E annotation) {
        ExecutorFactory.INSTANCE.create(annotation).getExecute().execute(method, bean, channel);

    }

    /**
     * 这个方法是spring自动调用的, 用来扫瞄有规定注解的方法
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        bot = context.getBean(Bot.class);
        channel = bot.getEventChannel();
        MiraiLogUtil.verbose("开始注册注解消息处理监听......");
        context.getBeansWithAnnotation(Listener.class).forEach((k, v) -> {
            Object bean = context.getBean(k);
            String name = bean.getClass().getName();
            MiraiLogUtil.debug("扫瞄到 " + name);
            List.of(bean.getClass().getDeclaredMethods()).forEach(method -> {
                Stream<Annotation> annotationStream = Arrays.stream(method.getAnnotations())
                        .filter(annotation -> annotation instanceof GroupMessage
                                || annotation instanceof UserMessage
                                || annotation instanceof Message);
                annotationStream.forEach(annotation -> {
                    String s = Arrays.toString(method.getParameterTypes())
                            .replaceAll("\\[", "(")
                            .replaceAll("]", ")");
                    MiraiLogUtil.verbose("开始注册处理方法 " + name + "." + method.getName() + s);
                    execute(bean, method, toFilter(channel, annotation), annotation);
                    MiraiLogUtil.info(bean.getClass().getName() + "." + method.getName() + s + " 处理方法注册完毕");
                });
            });
        });
        MiraiLogUtil.verbose("注解消息处理监听注册完毕\n");
    }
}