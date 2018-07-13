package com.bfei.icrane.common.util;

import com.bfei.icrane.common.enums.RandomEnum;
import com.bfei.icrane.core.pojos.TurnAnglePojo;

import java.util.Random;

/**
 * Created by moying on 2018/7/13.
 */
public class RandomUtil {


    public static TurnAnglePojo getTurnRandom() {
        Random random = new Random();
        int i = random.nextInt(100);
        if (0 <= i && i < 80) {
            return getTurnAngle(RandomEnum.TEN_COIN);
        } else if (80 <= i && i < 95) {
            return getTurnAngle(RandomEnum.FIFTEEN_COIN);
        } else {
            return getTurnAngle(RandomEnum.FIFTY_COIN);
        }
    }

    private static TurnAnglePojo getTurnAngle(RandomEnum randomEnum) {
        Random random = new Random();
        TurnAnglePojo turnAnglePojo = new TurnAnglePojo();
        switch (randomEnum.getCode()) {
            case 0:
                turnAnglePojo.setAngel(random.nextInt(60) + 31);
                turnAnglePojo.setCoins(randomEnum.getCoin());
                return turnAnglePojo;
            case 1:
                turnAnglePojo.setAngel(random.nextInt(60) + 91);
                turnAnglePojo.setCoins(randomEnum.getCoin());
                return turnAnglePojo;
            case 2:
                turnAnglePojo.setAngel(random.nextInt(60) + 151);
                turnAnglePojo.setCoins(randomEnum.getCoin());
                return turnAnglePojo;
            default:
                break;
        }
        return null;
    }
}
