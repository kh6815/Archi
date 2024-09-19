package com.architecture.archi.common.enumobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum SnsType implements Serializable {

    GOOGLE("GOOGLE");

    private final String snsType;

}
