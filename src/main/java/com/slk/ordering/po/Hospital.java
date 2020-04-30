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
            // 匹配所有现场开启的检查项
            if (checkitems.containsAll(wp.getCheckitems())) {
                // 选择先要去的现场
//                List<Worksite> wss = findWorksite(wp);


                // 尝试出病区
                boolean f = takeoverFromWard(wp);

                // 尝试进入现场


                quenePatients.add(wp);
                wPatient.remove();
            }
        }


        // 开启map中的线程
        Collection<Thread> threads = worksiteThMap.values();
        for (Thread th : threads) {
            th.start();
        }



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

        }


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
                if (b) {
                    // 给开启的现场连接等待池
                    e.setWaitPatients(waitPatients);
                    Thread thread = worksiteThMap.get(e.getName());
                    if (null == thread) {
                        thread = new Thread(e);
                    }
                    worksiteThMap.put(e.getName(),thread);
                }
                return b;
            }).collect(Collectors.toList());

        // 将开启现场中的检查项去重后配置到当前字段
        checkitems = worksitesOpened.stream()
            .map(Worksite::getCheckitems)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());

        log.info("当前所有现场的可检查项为{}", checkitems);
    }



}
