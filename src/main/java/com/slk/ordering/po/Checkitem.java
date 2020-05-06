package com.slk.ordering.po;

import lombok.Builder;
import lombok.Data;

/**
 * @describe： 检查项
 * @author: Liu Tian Jun
 * @Date: 2020-04-27 13:37
 * @version: 1.0
 */
@Data
@Builder
public class Checkitem implements Comparable<Checkitem> {

    private String name;

    /* 权重 */
    private Integer power;

    /* 检查时间 */
    private Integer costTime;

    @Override
    public int compareTo(Checkitem o) {
        return this.power.compareTo(o.power);
    }
}
