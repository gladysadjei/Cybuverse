package com.gladys.cybuverse.Utils.GeneralUtils.collections.exceptions;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.println;

public class EmptyObjectException extends Exception {
    public EmptyObjectException(String object) {
        println("\n" + object + " is Empty.");
    }
}
