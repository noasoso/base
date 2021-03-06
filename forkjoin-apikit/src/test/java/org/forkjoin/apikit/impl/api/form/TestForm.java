package org.forkjoin.apikit.impl.api.form;

import org.forkjoin.apikit.core.Message;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 好吧，测试表单
 * <p>
 * <p>
 * <h1>好呀</h1>
 *
 * @author zuoge85 on 15/4/18.
 * @see java.lang
 */
@Message
public class TestForm<T> {
    String id;
    /**
     * 签名
     *
     * @see java.lang
     * @see java.lang
     */
    boolean booleanValue;
    int intValue;
    long longValue;
    float floatValue;
    double doubleValue;

    @Length(max = 255)
    String stringValue;
    byte[] bytesValue;

    Date regDate;


    boolean[] booleanValueArray;
    int[] intValueArray;
    long[] longValueArray;
    float[] floatValueArray;
    double[] doubleValueArray;

    String[] stringValueArray;

    Date[] regDateArray;


    T generic;
}
