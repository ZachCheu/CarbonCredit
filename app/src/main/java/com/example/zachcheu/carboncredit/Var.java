package com.example.zachcheu.carboncredit;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by ZachCheu on 10/11/16.
 */
public class Var extends Activity {
    //test
    public static int RefreshRate = 0;
    public static ArrayList<PointsToCred> data = new ArrayList<PointsToCred>();
    public static boolean Sound = true;
    public static boolean Noti = true;
    public static int Trans = 0;
    public static int[] carsMph = new int[] {30, 40, 50, 60};
    public static int avgMph = 20;
    public static float idleBase = 4;
    public static int localDown = 20;
    public static int localUp  = 40;
    public static int highDown = 50;
    public static int highUp = 60;
    public static int[] xpLvl = new int[] {0, 2000, 10000, 25000, 50000, 100000, 250000, 500000, 1000000,2500000,5000000,10000000};
    public static String[] xpName = new String[]{"Scooter","Segway","Moped","Public Bus","Nissan Leaf", "Toyota Prius","Tesla Model S","SpaceX Hyperloop","SpaceX Dragon","Dragonite used Fly","BatMobile","Golden Nimbus"};
}

