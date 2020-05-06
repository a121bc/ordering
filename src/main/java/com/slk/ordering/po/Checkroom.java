package com.slk.ordering.po;

import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @describe： 检查室
 * @author: Liu Tian Jun
 * @Date: 2020-04-27 13:36
 * @version: 1.0
 */
@Data
@Builder
@Slf4j
public class Checkroom implements Comparable<Checkroom> {

    private String name;

    /* 标注的检查项 */
    private List<Checkitem> checkitems;

    /* 超时时间 分钟 */
    private Integer overtime;

    /* 预计排队时间 */
    private Integer preTime;

    /* 实际排队时间 */
    private Integer actTime;

    /********************************************************/
    /* 检查室内的患者 */
    private List<String> patients;

    /* 患者，检查项安排 */
    private Map<String, List<Checkitem>> checkitemMap;
    /********************************************************/

    /* 检查室状态 0-空闲中 1-检查中 */
    private Integer state;

    public void openCheckroom() {
        this.preTime = 0;
        this.actTime = 0;
        this.patients = new ArrayList<>();
        this.checkitemMap = new LinkedHashMap<>();

    }

    /**
     * @Description 判断当前检查室是否超时
     * @param
     * @return boolean
     * @author Liu Tian Jun
     * @date 11:16 2020-04-28 0028
     **/
    public boolean overstate() {
        if (patients!=null && patients.size()>0) {
            if (preTime >= overtime){
                log.warn("{} 检查室，预计超时时间{}，设置超时时间{}，已达限定值",name,preTime,overtime);
                return false;
            }
            log.info("{} 检查室，预计超时时间{}，设置超时时间{}，未达限定值",name,preTime,overtime);
            return true;
        }
        return false;
    }

    /**
     * 检查室接收患者
     * @param patient
     * @return
     */
    public boolean takeover(Patient patient) {
        // 判断当前检查室是否超时
        if (preTime >= overtime) {
            return false;
        }

        // 匹配检查项，决定是否接受
        HashSet<Checkitem> roomitems = Sets.newHashSet(this.checkitems);
        HashSet<Checkitem> paitems = Sets.newHashSet(patient.getChecktodoitems());

        Sets.SetView<Checkitem> intersection = Sets.intersection(roomitems, paitems);
        // 没有匹配项，检查室不接收
        if (intersection.isEmpty()) {
            return false;
        }

        // 匹配到的
        Set<Checkitem> effectitems = intersection.copyInto(Sets.newHashSet());
        // 给患者添加检查室分配队列，同时给检查室添加患者排队队列
        patient.signCheckMap(name,effectitems);

        this.signCheckMap(patient.getName(), effectitems);
        return true;
    }

    /**
     * 给检查室分配患者
     * @param patient
     * @param checkitemSet
     */
    public void signCheckMap(String patient, Set<Checkitem> checkitemSet) {
        checkitemMap.put(patient,new ArrayList<>(checkitemSet));
        // 将患者更新到该检查室患者区
        patients.add(patient);
        // 将该患者的检查项的
        preTime += checkitemSet.stream().mapToInt(Checkitem::getCostTime).sum();


    }

    /**
     * 患者离开检查室
     * @param patient
     */
    public void outCheckMap(String patient) {
        patients.remove(patient);
        preTime -= checkitemMap.get(patient).stream().mapToInt(Checkitem::getCostTime).sum();
        checkitemMap.remove(patient);
    }

    @Override
    public int compareTo(Checkroom o) {
        // 根据检查项比较优先级
        List<Checkitem> ci1 = this.getCheckitems();
        List<Checkitem> ci2 = o.getCheckitems();
        Collections.sort(ci1);
        Collections.sort(ci2);
        int min = Math.min(ci1.size(), ci2.size());
        for (int i = 0; i < min; i++) {
            Checkitem i1 = ci1.get(i);
            Checkitem i2 = ci2.get(i);
            // 权重相等则比较下一个检查项
            if (i1.getPower().equals(i2.getPower())) {
                continue;
            }
            // 不等则，取权重高的
            return i1.compareTo(i2);
        }
        //相等长度下，比较相等则长度大的权重大
        if (ci1.size() != ci2.size()) {
            return ci2.size() - ci1.size();
        }

        // 如果检查项权重相同，再根据预计排队时间比较优先级
        return o.preTime.compareTo(this.preTime);

    }
}
