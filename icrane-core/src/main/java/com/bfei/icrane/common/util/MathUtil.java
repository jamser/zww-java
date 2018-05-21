package com.bfei.icrane.common.util;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.cert.X509Certificate;

import static com.bfei.icrane.common.util.InitializeHeadsUtil.getRandom;

/**
 * Created by SUN on 2018/2/7.
 */
public class MathUtil {


    private static final Double MONEY_RANGE = 0.1;

    /**
     * 比较两个金额是否相等
     *
     * @param d1
     * @param d2
     * @return
     */

    public static Boolean equals(Double d1, Double d2) {
        double result = Math.abs(d1 - d2);
        if (result < MONEY_RANGE) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 得币
     *
     * @param min         最少
     * @param max         最多
     * @param expectation 平均数
     * @return
     */
    public int getShare(Double min, Double max, Double expectation) {
        if (min >= expectation || max <= expectation) {
            return (int) expectation.doubleValue();
        }
        max = max - min;
        expectation = expectation - min + 1;
        double e = expectation;
        double i = getI(max, expectation);
        double m = max;
        double r = getRandom().nextInt(10000001);
        if (r >= 0 && r <= i) {
            Double i2 = getI2(e, i, r);
            if (i2 < 0) {
                i2 = 0.0;
            }
            return (int) (i2 + min);
        } else {
            return (int) (getI3(e, i, r, m) + min);
        }
    }


    public Double getI(Double max, Double ex) {
        Double I = -(10000000 * ex - 10000000 * max) / max;
        return I;
    }

    public Double getI2(Double e, Double i, Double r) {
        Double I = e / i * r;
        return I;
    }

    public Double getI3(Double e, Double i, Double r, Double m) {
        Double I = (r - i) * (m - e) / (10000000 - i) + e;
        return I;
    }


}