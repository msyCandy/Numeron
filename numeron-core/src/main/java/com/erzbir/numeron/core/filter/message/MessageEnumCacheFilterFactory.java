package com.erzbir.numeron.core.filter.message;

import com.erzbir.numeron.core.exception.FilterNotFoundException;
import com.erzbir.numeron.core.filter.AbstractEnumCacheFilterFactory;
import com.erzbir.numeron.core.filter.CacheFilterFactory;
import com.erzbir.numeron.core.filter.FilterFactory;
import com.erzbir.numeron.filter.MessageRule;

/**
 * @author Erzbir
 * @Date: 2022/11/26 16:58
 * <p>
 * 消息过滤类工厂
 * </p>
 */
public class MessageEnumCacheFilterFactory extends AbstractEnumCacheFilterFactory implements FilterFactory, CacheFilterFactory {
    public final static MessageEnumCacheFilterFactory INSTANCE = new MessageEnumCacheFilterFactory();

    private MessageEnumCacheFilterFactory() {

    }

    @Override
    public AbstractMessageChannelFilter create(Enum<?> e) {
        if (e.equals(MessageRule.EQUAL)) {
            return (AbstractMessageChannelFilter) getFilter(EqualsMessageChannelFilter.class);
        } else if (e.equals(MessageRule.END_WITH)) {
            return (AbstractMessageChannelFilter) getFilter(EndMessageChannelFilter.class);
        } else if (e.equals(MessageRule.BEGIN_WITH)) {
            return (AbstractMessageChannelFilter) getFilter(BeginMessageChannelFilter.class);
        } else if (e.equals(MessageRule.CONTAINS)) {
            return (AbstractMessageChannelFilter) getFilter(ContainsMessageChannelFilter.class);
        } else if (e.equals(MessageRule.REGEX)) {
            return (AbstractMessageChannelFilter) getFilter(RegexMessageChannelFilter.class);
        } else if (e.equals(MessageRule.IN)) {
            return (AbstractMessageChannelFilter) getFilter(InMessageChannelFilter.class);
        }
        throw new FilterNotFoundException("no filter of this type");
    }
}
