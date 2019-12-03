/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.bmp.app;

import java.util.Scanner;

/**
 *
 * @author permadi
 */
public class test {

    public static void main(String[] args) {

        //System Objects
        Scanner keyboard = new Scanner(System.in);

        //Variables
        String Married;
        System.out.print("My name :\t");
        String name = keyboard.nextLine();

        System.out.print("Will you merry me, " + name + "? (yes or no) : ");
        Married = keyboard.next();
        if (Married.equalsIgnoreCase("yes")) {
            System.out.print("Then I shall call you my wife.");
        }
        else
        {
            System.out.print("Fuck you laa..");
        }

    }

}
