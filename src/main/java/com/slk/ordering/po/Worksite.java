package com.slk.ordering.po;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @describe： 现场
 * @author: Liu Tian Jun
 * @Date: 2020-04-27 13:35
 * @version: 1.0
 */
@Data
@Builder
@Slf4j
public class Worksite implements Runnable {

    /* 现场名称 */
    private String name;

    /* 检查室 */
    private List<Checkroom> checkrooms;

    /* 压床值 */
    private Integer stockLimit;

    /* 现场是否开启 0-关闭 1-开启 */
    private Integer state;

    /* 现场内的患者 */
    private List<Patient> curPatients;

    /* 现场内所有检查室的检查项 */
    private List<Checkitem> checkitems;


    @Override
    public void run() {
        if (1 == state) {

        }

    }

    /**
     * @Description 开启现场
     * @param
     * @return void
     * @author Liu Tian Jun
     * @date 09:29 2020-04-28 0028
     **/
    public void openWorkSite() {
        // 1. 获取检查所有室内的检查项
        if (checkrooms == null || checkrooms.size() == 0) {
            log.error("{}现场内没有开启的检查室",name);
            return;
        }
        if (stockLimit == null) {
            log.error("{}现场没有设置压床值",name);
            return;
        }

        // 2. 根据其 （超时时间）和 （压床值）判断是否要人
        if (overStock()) {
            log.warn("排队患者已达压床值，{}现场停止要人",name);
            return;
        }

        if (checkrooms.stream().noneMatch(Checkroom::overstate) ) {
            log.warn("现场内的所有检查室均已达超时时间，{}现场停止要人",name);
            return;
        }

        log.info("开始获取病区内的检查项");
        checkitems = checkrooms.stream()
                .map(Checkroom::getCheckitems)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        log.info("现场开启的检查项：{}",checkitems);

    }

    /**
     * 检查是否超出压床值
     * @return
     */
    public boolean overStock() {
        return curPatients!=null && curPatients.size()>=stockLimit;
    }

    /**
     * 现场接收患者
     * @param patient
     * @return
     */
    public boolean takeover(Patient patient) {
        if (curPatients == null) {
            curPatients = new ArrayList<>();
        }

        // 现场排队患者超出压床值，则不再接收患者
        if (overStock()) {
            return false;
        }

        int oldSize = patient.getCheckitems().size();

        // 尝试进入检查室
        for (Checkroom checkroom : checkrooms) {
            boolean tover = checkroom.takeover(patient);
            // 如果检查室已接收患者，且患者的未分配检查项等于零，则该患者分配完成
            if (tover && patient.getChecktodoitems().size()==0) {
                break;
            }
        }

        // 如果该现场没有检查一项患者信息则
        if (patient.getChecktodoitems().size() == oldSize) {
            return false;
        }


        // 如果患者分配完成，则正式进入该现场
        if (patient.getChecktodoitems().size()==0) {
            // 当前现场 添加患者排队
            curPatients.add(patient);
            // 压床值加一
            stockLimit++;
            log.info("【{}】患者成功进入【{}】现场",patient.getName(),name);
        }
        return true;

    }

    /**
     * 分配失败患者离开现场
     */
    public void outWorksite(Patient patient) {
        curPatients.remove(patient);
        stockLimit--;
    }


}
