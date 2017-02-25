package cn.tjuscs.oj.patch;

/**
 * Created by yunhao on 17-2-25.
 */
public class Patch {
    public static void fix(int totCases,int passedCases){
        System.out.println("tot="+totCases+" passed="+passedCases);
        if(passedCases == 0){
            System.out.println("You didn't pass any case, we can't fix");
        }
        else if(totCases == passedCases){
            System.out.println("You already pass all cases! Don't need to fix");
        }
        else{
            //fix
        }
    }
}
