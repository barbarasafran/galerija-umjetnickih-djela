package org.example.service;

public interface Validator<T> {

    boolean jeIspravno(T objekt);

    default void validirajIliBaci(T objekt, String porukaGreske) {
        if (!jeIspravno(objekt)) {
            throw new IllegalArgumentException(porukaGreske);
        }
    }
}