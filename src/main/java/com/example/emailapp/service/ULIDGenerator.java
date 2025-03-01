package com.example.emailapp.service;

import de.huxhorn.sulky.ulid.ULID;

public class ULIDGenerator {
    private static final ULID ulid = new ULID();

    public static String generateULID() {
        return ulid.nextULID();
    }
}
