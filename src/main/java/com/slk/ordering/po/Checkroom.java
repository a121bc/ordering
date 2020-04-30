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
public class Checkroom {

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
    private List<Patient> patients;

    /* 患者，检查项安排 */
    private Map<Patient, List<Checkitem>> checkitemMap;
    /********************************************************/

    /* 检查室状态 0-空闲中 1-检查中 */
    private Integer state;

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
        HashSet<Checkitem> paitems = Sets.newHashSet(patient.getCheckitems());

        Sets.SetView<Checkitem> intersection = Sets.intersection(roomitems, paitems);
        // 没有匹配项，检查室不接收
        if (intersection.isEmpty()) {
            return false;
        }

        // 匹配到的
        Set<Checkitem> effectitems = intersection.copyInto(Sets.newHashSet());
        // 给患者添加检查室分配队列，同时给检查室添加患者排队队列
        patient.signCheckMap(this,effectitems);
        this.signCheckMap(patient, effectitems);
        return true;
    }

    /**
     * 给检查室分配患者
     * @param patient
     * @param checkitemSet
     */
    public void signCheckMap(Patient patient, Set<Checkitem> checkitemSet) {
        checkitemMap.put(patient,checkitems);
        // 将患者更新到该检查室患者区
        patients.add(patient);
        // 将该患者的检查项的
        preTime += checkitemSet.stream().mapToInt(Checkitem::getCostTime).sum();


    }

    /**
     * 患者离开检查室
     * @param patient
     */
    public void outCheckMap(Patient patient) {
        patients.remove(patient);
        preTime -= checkitemMap.get(patient).stream().mapToInt(Checkitem::getCostTime).sum();
        checkitemMap.remove(patient);
    }

}
