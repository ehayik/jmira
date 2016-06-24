package org.eljaiek.jmira.core.logs;

/**
 *
 * @author eduardo.eljaiek
 */
public interface MessageResolver {

    String getMessage(String code, Object... args);

    String getMessage(String code);
}
