package org.example;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("C:\\Program Files");
        String[] list = file.list();
        for (String s : list) {
            System.out.println(s);
        }
    }
}