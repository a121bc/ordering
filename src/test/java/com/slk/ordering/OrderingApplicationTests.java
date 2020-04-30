package com.slk.ordering;

import com.slk.ordering.po.Checkitem;
import com.slk.ordering.po.Checkroom;
import com.slk.ordering.po.Patient;
import com.slk.ordering.po.Worksite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//@SpringBootTest
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
        Checkroom checkroom_de = Checkroom.builder().name("检查室DE").checkitems(Arrays.asList(c_d,c_e)).build();
        Checkroom checkroom_d = Checkroom.builder().name("检查室D").checkitems(Collections.singletonList(c_d)).build();
        Checkroom checkroom_abc = Checkroom.builder().name("检查室ABC").checkitems(Arrays.asList(c_a,c_b,c_c)).build();
        Checkroom checkroom_bc = Checkroom.builder().name("检查室BC").checkitems(Arrays.asList(c_b,c_c)).build();
        Checkroom checkroom_f = Checkroom.builder().name("检查室F").checkitems(Collections.singletonList(c_f)).build();
        Checkroom checkroom_a = Checkroom.builder().name("检查室A").checkitems(Collections.singletonList(c_a)).build();

        /* 现场 */
        Worksite worksite1 = Worksite.builder().name("现场1").checkrooms(Arrays.asList(checkroom_de, checkroom_d)).stockLimit(12).build();
        Worksite worksite2 = Worksite.builder().name("现场2").checkrooms(Arrays.asList(checkroom_abc, checkroom_bc,checkroom_f,checkroom_a)).stockLimit(14).build();


        /* 患者 */

        Patient abcdef_1 = Patient.builder().name("abcdef_1").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e, c_f)).build();
        Patient abcdef_2 = Patient.builder().name("abcdef_2").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e, c_f)).build();
        Patient abcdef_3 = Patient.builder().name("abcdef_3").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e, c_f)).build();
        Patient abcdef_4 = Patient.builder().name("abcdef_4").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e, c_f)).build();
        Patient abcdef_5 = Patient.builder().name("abcdef_5").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e, c_f)).build();

        Patient abcde_1 = Patient.builder().name("abcde_1").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e)).build();
        Patient abcde_2 = Patient.builder().name("abcde_2").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e)).build();
        Patient abcde_3 = Patient.builder().name("abcde_3").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e)).build();
        Patient abcde_4 = Patient.builder().name("abcde_4").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e)).build();
        Patient abcde_5 = Patient.builder().name("abcde_5").checkitems(Arrays.asList(c_a, c_b, c_c, c_d, c_e)).build();

        /* 病区池 */
        List<Patient> patients = Arrays.asList(abcdef_1, abcdef_2, abcdef_3, abcdef_4, abcdef_5,
                abcde_1, abcde_2, abcde_3, abcde_4, abcde_5);

    }

}
