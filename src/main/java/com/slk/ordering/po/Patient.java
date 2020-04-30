package com.slk.ordering.po;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @describe： 患者
 * @author: Liu Tian Jun
 * @Date: 2020-04-27 13:37
 * @version: 1.0
 */

@Data
@Builder
public class Patient {

    /* 患者姓名 */
    private String name;

    /* 总检查项 */
    private List<Checkitem> checkitems;

    /* 待检查项 */
    private List<Checkitem> checktodoitems;

    /* 检查室，检查项安排 */
    private Map<Checkroom, List<Checkitem>> checkitemMap;

    /* 检查完成 */
    private List<Checkitem> checkeditems;

    /**
     * 给患者分配检查室
     */
    public void signCheckMap(Checkroom croom,Set<Checkitem> checkitemSet) {
        // 将要检查项连同检查室放入map
        checkitemMap.put(croom, Lists.newArrayList(checkitemSet));
        // 将要检查项从待检查list中移除
        checktodoitems.removeAll(checkitemSet);

    }



}
