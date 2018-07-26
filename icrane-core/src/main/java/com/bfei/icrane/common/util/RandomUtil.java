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
        if (0 <= i && i < 45) {
            return getTurnAngle(RandomEnum.TEN_COIN);
        } else if (45 <= i && i < 49) {
            return getTurnAngle(RandomEnum.FIFTEEN_COIN);
        } else if (49 <= i && i < 50) {
            return getTurnAngle(RandomEnum.FIFTY_COIN);
        } else {
            return getTurnAngle(RandomEnum.Thanks);
        }
    }

    public static void main(String[] args) {
        for (int i=0;i<100;i++) {
            TurnAnglePojo turnRandom = RandomUtil.getTurnRandom();
            System.out.println(turnRandom.toString());
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
            case 3:
                turnAnglePojo.setAngel(random.nextInt(60) + 271);
                turnAnglePojo.setCoins(randomEnum.getCoin());
                return turnAnglePojo;
            default:
                break;
        }
        return null;
    }
}
