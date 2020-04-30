package com.slk.ordering;

import com.slk.ordering.po.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//@SpringBootTest
@Slf4j
class OrderingApplicationTests {

    @Test
    void contextLoads() {

        /* 检查项 */
        Checkitem c_a = Checkitem.builder().name("A").power(10).costTime(6).build();
        Checkitem c_b = Checkitem.builder().name("B").power(9).costTime(6).build();
        Checkitem c_c = Checkitem.builder().name("C").power(8).costTime(6).build();
        Checkitem c_d = Checkitem.builder().name("D").power(7).costTime(3).build();
        Checkitem c_e = Checkitem.builder().name("E").power(6).costTime(3).build();
        Checkitem c_f = Checkitem.builder().name("F").power(5).costTime(15).build();

        /* 检查室 */
        Checkroom checkroom_de = Checkroom.builder().name("检查室DE").checkitems(Arrays.asList(c_d,c_e)).overtime(50).build();
        Checkroom checkroom_d = Checkroom.builder().name("检查室D").checkitems(Collections.singletonList(c_d)).overtime(50).build();
        Checkroom checkroom_abc = Checkroom.builder().name("检查室ABC").checkitems(Arrays.asList(c_a,c_b,c_c)).overtime(50).build();
        Checkroom checkroom_bc = Checkroom.builder().name("检查室BC").checkitems(Arrays.asList(c_b,c_c)).overtime(50).build();
        Checkroom checkroom_f = Checkroom.builder().name("检查室F").checkitems(Collections.singletonList(c_f)).overtime(50).build();
        Checkroom checkroom_a = Checkroom.builder().name("检查室A").checkitems(Collections.singletonList(c_a)).overtime(50).build();

        /* 现场 */
        Worksite worksite1 = Worksite.builder().name("现场1").state(1).checkrooms(Arrays.asList(checkroom_de, checkroom_d)).stockLimit(12).build();
        Worksite worksite2 = Worksite.builder().name("现场2").state(1).checkrooms(Arrays.asList(checkroom_abc, checkroom_bc,checkroom_f,checkroom_a)).stockLimit(14).build();


        /* 患者 */

        Patient abcdef_1 = Patient.builder().name("abcdef_1").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e, c_f)).build();
        Patient abcdef_2 = Patient.builder().name("abcdef_2").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e, c_f)).build();
        Patient abcdef_3 = Patient.builder().name("abcdef_3").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e, c_f)).build();
        Patient abcdef_4 = Patient.builder().name("abcdef_4").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e, c_f)).build();
        Patient abcdef_5 = Patient.builder().name("abcdef_5").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e, c_f)).build();

        Patient bcde_1 = Patient.builder().name("bcde_1").checkitems(Arrays.asList(c_b, c_c, c_d, c_e)).build();
        Patient bcde_2 = Patient.builder().name("bcde_2").checkitems(Arrays.asList(c_b, c_c, c_d, c_e)).build();
        Patient bcde_3 = Patient.builder().name("bcde_3").checkitems(Arrays.asList(c_b, c_c, c_d, c_e)).build();
        Patient bcde_4 = Patient.builder().name("bcde_4").checkitems(Arrays.asList(c_b, c_c, c_d, c_e)).build();
        Patient bcde_5 = Patient.builder().name("bcde_5").checkitems(Arrays.asList(c_b, c_c, c_d, c_e)).build();

        Patient abc_1 = Patient.builder().name("abc_1").checkitems(Arrays.asList(c_a, c_b, c_c)).build();
        Patient abc_2 = Patient.builder().name("abc_2").checkitems(Arrays.asList(c_a, c_b, c_c)).build();

        Patient ad_1 = Patient.builder().name("ad_1").checkitems(Arrays.asList(c_a,c_d)).build();
        Patient ad_2 = Patient.builder().name("ad_2").checkitems(Arrays.asList(c_a,c_d)).build();

        Patient de_1 = Patient.builder().name("de_1").checkitems(Arrays.asList(c_d,c_e)).build();
        Patient de_2 = Patient.builder().name("de_2").checkitems(Arrays.asList(c_d,c_e)).build();

        Patient d_1 = Patient.builder().name("d_1").checkitems(Arrays.asList(c_d)).build();
        Patient d_2 = Patient.builder().name("d_2").checkitems(Arrays.asList(c_d)).build();

        /* 病区池 */
        List<Patient> patients = Arrays.asList(
                abcdef_1, abcdef_2, abcdef_3, abcdef_4, abcdef_5,
                bcde_1, bcde_2, bcde_3, bcde_4, bcde_5,
                abc_1,abc_2,
                ad_1,ad_2,
                de_1,de_2,
                d_1,d_2
        );

        long start = System.currentTimeMillis();

        Hospital.builder()
                .worksites(Arrays.asList(worksite1,worksite2))
                .waitPatients(new ArrayList<>(patients))
                .build()
                .openHospital();
        long end = System.currentTimeMillis();
        log.info("排序耗时：{}毫秒",end-start);


    }

}
