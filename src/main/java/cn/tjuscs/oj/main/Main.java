package cn.tjuscs.oj.main;

/**
 * Created by yunhao on 17-2-25.
 */

import cn.tjuscs.oj.config.Config;
import cn.tjuscs.oj.dividing.index;
import cn.tjuscs.oj.patch.Patch;
import cn.tjuscs.oj.rungcov.rungcov;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException, IOException{
        //dividing
        String pid = new String("2800");
        index.mainProcess(pid, "rightCode");

        //rungcov
        ResultSet res = Config.getAllSubmitIdFromProblemId(pid);
        res.next();
        String sid = res.getString("sid");
//        List<Integer> ret = rungcov.mainProcess(pid, "155222");
        List<Integer> ret = rungcov.mainProcess(pid, "155281");

        //patch
        Integer totCases = ret.get(0);
        Integer passedCases = ret.get(1);
        Patch.fix(totCases,passedCases);
    }
}
