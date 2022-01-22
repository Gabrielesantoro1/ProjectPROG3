package com.unito.prog3.fmail.support;

import com.unito.prog3.fmail.model.Mailbox;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Support {
    public final static String PATH_NAME_DIR = "C:\\Users\\santo\\Documenti\\DirProjProg3";
    public final static Integer port = 8189;

    public final static Mailbox daniele = new Mailbox("daniele@gmail.com");
    public final static Mailbox gabriele = new Mailbox("gabriele@gmail.com");
    public final static Mailbox danieleSer = new Mailbox("danieleSer@gmail.com");
    //public final static Mailbox wrong = new Mailbox("wrong@css");

    //TODO Nella funzione non è possibile inserire punti o numeri.
    public static boolean match_account(String account_name){
        Pattern p = Pattern.compile("[a-zwA-Z]++@[a-zA-Z]++.com");
        Matcher m = p.matcher(account_name);
        return m.matches();
    }


}
