package com.erzbir.numeron.plugin.qqmanage.action;

import com.erzbir.numeron.core.filter.message.MessageRule;
import com.erzbir.numeron.core.filter.permission.PermissionType;
import com.erzbir.numeron.core.handler.Command;
import com.erzbir.numeron.core.handler.Plugin;
import com.erzbir.numeron.core.handler.PluginRegister;
import com.erzbir.numeron.core.listener.Listener;
import com.erzbir.numeron.core.listener.massage.GroupMessage;
import com.erzbir.numeron.menu.Menu;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;

import java.util.HashMap;

/**
 * @author Erzbir
 * @Date: 2022/11/27 13:07
 */
@Plugin
@Listener
@Menu(name = "退群提示")
@SuppressWarnings("unused")
public class MemberLeaveAction implements PluginRegister {
    private final HashMap<Long, Boolean> isOn = new HashMap<>();

    @Override
    public void register(Bot bot, EventChannel<BotEvent> channel) {
        bot.getEventChannel().subscribeAlways(MemberLeaveEvent.class, event -> {
            if (isOn.containsKey(event.getGroupId())) {
                event.getGroup().sendMessage(event.getMember().getNick() + " 离开了我们...");
            }
        });
    }

    @Command(
            name = "退群提示",
            dec = "开关退群",
            help = "/leave [true|false]",
            permission = PermissionType.ADMIN
    )
    @GroupMessage(
            text = "/leave\\s+?feedback\\s+?(true|false)",
            permission = PermissionType.ADMIN,
            messageRule = MessageRule.REGEX
    )
    private void onOff(GroupMessageEvent event) {
        String[] s = event.getMessage().contentToString().split("\\s+?");
        isOn.put(event.getGroup().getId(), Boolean.parseBoolean(s[2]));
        event.getSubject().sendMessage(isOn.toString());
    }
}