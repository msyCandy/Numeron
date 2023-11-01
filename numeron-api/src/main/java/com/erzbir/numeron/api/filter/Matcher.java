package com.erzbir.numeron.api.filter;

/**
 * @author Erzbir
 * @Date 2023/9/27
 */
public interface Matcher<TARGET, RULE> {
    boolean match(TARGET target, RULE rule);
}
