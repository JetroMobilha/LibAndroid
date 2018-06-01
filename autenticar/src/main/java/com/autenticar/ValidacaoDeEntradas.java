package com.autenticar;


public class ValidacaoDeEntradas {


    public static boolean isEmail(String email){

        return email.matches("[0-9a-z]+@[a-z]+\\.\\w{2,3}") || email.matches("[0-9a-z]+@[a-z]+\\.\\w{2,3}\\.\\w{2,3}");
    }

    public static boolean isNumero(String numero){

        return numero.matches("[+]{0,1}\\d{9,12}");
    }
}
