/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.bmp.app;

/**
 *
 * @author permadi
 */
public class test {

    public static void main(String[] args) {

        boolean isDivisible;
        int value = 0;
        for (int i = 2; i < value; i++) {
            if (value % i == 0) {
                isDivisible = true;
                System.out.println(value + " is divisible");
                break;//loop break when divisible
            }
        }

    }

}
