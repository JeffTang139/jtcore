package org.eclipse.jt.core.type;

import org.eclipse.jt.core.type.GUID;

/**
 * 摘要收集器(散列器，签名器)
 * 
 * @author Jeff Tang
 * 
 */
public interface Digester {
    void update(boolean input);

    void update(byte input);

    void update(char input);

    void update(short input);

    void update(int input);

    void update(long input);

    void update(double input);

    void update(float input);

    void update(String input);

    void update(byte[] input);

    void update(Class<?> input);

    void update(Enum<?> input);

    void update(GUID input);
}
