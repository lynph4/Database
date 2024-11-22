package com.lab2;

import com.lab2.controller.Controller;

public class Main {
    public static void main(String[] args) {
        try {
            Controller controller = new Controller();
            controller.start();
        } catch (IllegalStateException e) {
            System.err.println("Fatal Error: " + e.getMessage());
        }
    }
}