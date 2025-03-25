package org.maping.maping.common.utills.ULID;


import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.stereotype.Component;

@Component
public class ULIDUtill {
    public static String generatorULID(String type) {
        return type + "-" + UlidCreator.getMonotonicUlid().toString();
    }
}