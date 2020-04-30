package com.slk.ordering.po;

import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @describe： 医院
 * @author: Liu Tian Jun
 * @Date: 2020-04-28 11:59
 * @version: 1.0
 */

@Data
@Builder
@Slf4j
public class Hospital {

    /* 现场 */
    private List<Worksite> worksites;

    /* 等待池 */
    private List<Patient> waitPatients;



    /* 现场内的患者 */
    private List<Patient> quenePatients;

    /* 所有现场的检查项 */
    private List<Checkitem> checkitems;

    /* 开启的现场 */
    private List<Worksite> worksitesOpened;

    private Map<String,Thread> worksiteThMap;

    /**
     * @Description 医院开始运行
     * @param
     * @return void
     * @author Liu Tian Jun
     * @date 13:14 2020-04-28 0028
     **/
    public void openHospital() {
        if (worksites == null || worksites.size() == 0 ) {
            log.warn("没有配置现场");
            return;
        }

        if (waitPatients == null || waitPatients.size() == 0) {
            log.warn("等待池为空");
            return;
        }

        // 把等待池配置给现场，并获取现场的检查项
        reloadData();

        // 根据所有检查项，匹配可以做全部检查的患者分给现场
        Iterator<Patient> wPatient = waitPatients.iterator();
        while (wPatient.hasNext()) {
            Patient wp = wPatient.next();
            wp.init();
            // 匹配所有现场开启的检查项
            if (checkitems.containsAll(wp.getCheckitems())) {
                // 选择先要去的现场
//                List<Worksite> wss = findWorksite(wp);
                // 分配患者进入现场
                boolean f = takeoverFromWard(wp);
                // 如果进入现场成功，则将患者从等待池移除
                if (f) {
                    quenePatients.add(wp);
                    wPatient.remove();
                }

            }
        }


        // 开启map中的线程
        /*Collection<Thread> threads = worksiteThMap.values();
        for (Thread th : threads) {
            th.start();
        }*/

        logInfo();

    }

    private void logInfo() {
        log.info("现场情况");
        for (Worksite ws : worksites) {
            log.info("现场名：{}",ws.getName());
            log.info("  压床值：{},现场内患者数量：{},检查项：{}",
                    ws.getStockLimit(),
                    ws.getCurPatients().size(),
                    ws.getCheckitems().stream().map(Checkitem::getName).collect(Collectors.toSet())
            );
            log.info("*************************************************");
            List<Checkroom> crooms = ws.getCheckrooms();
            for (Checkroom croom : crooms) {
                log.info("  检查室名：{}", croom.getName());
                log.info("  超时时间：{},预计排队时间：{},检查项：{}", croom.getOvertime(),croom.getPreTime(),croom.getCheckitems().stream().map(Checkitem::getName).collect(Collectors.toSet()));

                log.info("  检查室内的患者:");
                Map<String, List<Checkitem>> checkitemMap = croom.getCheckitemMap();
                Set<Map.Entry<String, List<Checkitem>>> entries = checkitemMap.entrySet();
                for (Map.Entry<String, List<Checkitem>> entry : entries) {
                    log.info("      患者:{},检查项:{}",entry.getKey(),entry.getValue().stream().map(Checkitem::getName).collect(Collectors.toSet()));
                }
                log.info("*************************************************");
            }

        }
        log.info("等待池:{}",waitPatients.stream().map(Patient::getName).collect(Collectors.toList()));

    }

    /**
     * 将患者从病区接到现场
     * @param patient
     * @return
     */
    private boolean takeoverFromWard(Patient patient) {

        List<Worksite> wss = new ArrayList<>();
        // 找出有必要检查项的现场
        for (Worksite worksite : worksites) {
            // 患者进入现场
            boolean flag = worksite.takeover(patient);
            if (flag && patient.getChecktodoitems().size()==0) {
                break;
            }
        }

        // 如果所有现场的检查室都没有检查完患者的检查项，
        // 则将患者已分配的现场和检查室以及患者自身的检查室安排清零
        if (patient.getChecktodoitems().size() > 0) {
            Set<String> croom = patient.resetCheck();
            Set<Checkroom> collect = worksites.stream()
                    .map(Worksite::getCheckrooms)
                    .flatMap(Collection::stream)
                    .filter(e ->croom.contains(e.getName()))
                    .collect(Collectors.toSet());

            int size = collect.size();
            // 如果已分配现场
            if (size > 0) {
                // 患者离开现场
                for (Worksite ws : worksites) {
                    List<Checkroom> crList = ws.getCheckrooms();
                    if(!Sets.intersection(Sets.newHashSet(crList), collect).isEmpty()) {
                        ws.outWorksite(patient);
                        size--;
                        if (size == 0) {
                            break;
                        }
                    }
                }
                // 患者离开检查室
                for (Checkroom cr : collect) {
                    cr.outCheckMap(patient.getName());
                }

            }

            return false;
        }

        return true;


    }

    /**
     *  给患者选择合适的现场
     * @param wp
     * @return
     */
    private List<Worksite> findWorksite(Patient wp) {

        List<Worksite> wss = new ArrayList<>();

        // 找出有必要检查项的现场
        for (Worksite worksite : worksitesOpened) {
            // 如果现场已达压床值，则跳过
            if (worksite.overStock()) {
                continue;
            }

            // 现场检查项
            Set<Checkitem> wcitems = Sets.newHashSet(worksite.getCheckitems());

            // 患者检查项
            Set<Checkitem> pitems = Sets.newHashSet(wp.getCheckitems());

            Sets.SetView<Checkitem> intersection = Sets.intersection(wcitems, pitems);
            // 交集为空,患者不在这个现场做
            if (intersection.isEmpty()) {
                continue;
            }
            // 交集检查项
            HashSet<Checkitem> xjitems = intersection.copyInto(Sets.newHashSet());

            // 交集与患者检查项相等，则患者完全在这个现场做
            if (xjitems.equals(pitems)) {
                // 将检查项完全赋值给待办
//                wp.setChecktodoitems(wp.getCheckitems());
                return Collections.singletonList(worksite);
            }


            wss.add(worksite);
        }

        wss = wss.stream().sorted((a,b)->{


            return 1;
        }).collect(Collectors.toList());

        // 根据等待时间排序

        return wss;

    }

    /**
     *  把等待池配置给现场
     *  获取所有开启现场中的检查项去重后配置到当前字段
     *  创建线程，根据现场名添加到map中
     */
    private void reloadData() {

        // 获取所有开启现场
        worksitesOpened = worksites.stream()
            .filter(e-> {
                boolean b = e.getState() == 1;
                /*if (b) {
                    // 给开启的现场连接等待池
                    Thread thread = worksiteThMap.get(e.getName());
                    if (null == thread) {
                        thread = new Thread(e);
                    }
                    worksiteThMap.put(e.getName(),thread);
                }*/
                return b;
            }).collect(Collectors.toList());

        // 将现场开启
        for (Worksite ws : worksitesOpened) {
            ws.openWorkSite();
        }

        this.quenePatients = new ArrayList<>();

        // 将开启现场中的检查项去重后配置到当前字段
        checkitems = worksitesOpened.stream()
            .map(Worksite::getCheckitems)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());

        log.info("当前所有现场的可检查项为{}", checkitems);
    }



}
