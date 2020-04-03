package com.company.callchain;

import com.company.callchain.function.FunctionExecutor;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        final var scan = new Scanner(System.in);
        System.out.println("IN");
        final var executor = new FunctionExecutor(scan.nextLine());
        System.out.println("OUT");
        System.out.println(executor.execute());
    }
}
