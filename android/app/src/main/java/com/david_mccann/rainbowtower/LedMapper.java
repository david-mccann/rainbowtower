package com.david_mccann.rainbowtower;

import android.util.SparseIntArray;

import java.util.HashMap;
import java.util.Map;

public class LedMapper {
    private static final LedMapper ourInstance = new LedMapper();

    private final SparseIntArray mapper = new SparseIntArray();

    public static LedMapper getInstance() {
        return ourInstance;
    }

    public int ledToSide(int led) {
        return mapper.get(led);
    }

    private LedMapper() {
        // row 1
        mapper.append(0, 0);
        mapper.append(1, 0);
        mapper.append(2, 0);

        mapper.append(3, 1);
        mapper.append(4, 1);
        mapper.append(5, 1);

        mapper.append(6, 2);
        mapper.append(7, 2);
        mapper.append(8, 2);

        mapper.append(9, 3);
        mapper.append(10, 3);
        mapper.append(11, 3);

        // row 2
        mapper.append(12, 3);
        mapper.append(13, 3);
        mapper.append(14, 3);

        mapper.append(15, 2);
        mapper.append(16, 2);
        mapper.append(17, 2);

        mapper.append(18, 1);
        mapper.append(19, 1);
        mapper.append(20, 1);

        mapper.append(21, 0);
        mapper.append(22, 0);
        mapper.append(23, 0);

        // row 3
        mapper.append(24, 0);
        mapper.append(25, 0);
        mapper.append(26, 0);

        mapper.append(27, 1);
        mapper.append(28, 1);
        mapper.append(29, 1);

        mapper.append(30, 2);
        mapper.append(31, 2);
        mapper.append(32, 2);

        mapper.append(33, 3);
        mapper.append(34, 3);
        mapper.append(35, 3);

        // row 4
        mapper.append(36, 3);
        mapper.append(37, 3);
        mapper.append(38, 3);

        mapper.append(39, 2);
        mapper.append(40, 2);
        mapper.append(41, 2);

        mapper.append(42, 1);
        mapper.append(43, 1);
        mapper.append(44, 1);

        mapper.append(45, 0);
        mapper.append(46, 0);
        mapper.append(47, 0);

        // row 5
        mapper.append(48, 0);
        mapper.append(49, 0);
        mapper.append(50, 0);

        mapper.append(51, 1);
        mapper.append(52, 1);
        mapper.append(53, 1);

        mapper.append(54, 2);
        mapper.append(55, 2);
        mapper.append(56, 2);

        mapper.append(57, 3);
        mapper.append(58, 3);
        mapper.append(59, 3);
    }
}
