package com.idursun.camel.routes.utils;

import java.util.Random;

public class RandomGroupGenerator {

    static final Random rng = new Random();

    public int next() {
        return rng.nextInt(10);
    }
}
