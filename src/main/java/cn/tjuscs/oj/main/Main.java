package cn.tjuscs.oj.main;

/**
 * Created by yunhao on 17-2-25.
 */

import cn.tjuscs.oj.config.Config;
import cn.tjuscs.oj.dividing.index;
import cn.tjuscs.oj.patch.Patch;
import cn.tjuscs.oj.rungcov.rungcov;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException, IOException{
        //dividing
        String pid = args[0];
        String sid = args[1];
//        String pid = new String("2800");
//        String sid = new String("155281");
        index.mainProcess(pid, "rightCode");

        //rungcov
//        ResultSet res = Config.getAllSubmitIdFromProblemId(pid);
//        res.next();
//        String sid = res.getString("sid");
//        List<Integer> ret = rungcov.mainProcess(pid, "154961"); //100,100
//        List<Integer> ret = rungcov.mainProcess(pid, "155219"); //100,0
//        List<Integer> ret = rungcov.mainProcess(pid, "155220"); //100,0
//        List<Integer> ret = rungcov.mainProcess(pid, "155222"); //100,3
//        List<Integer> ret = rungcov.mainProcess(pid, "155281"); //100,20
//        List<Integer> ret = rungcov.mainProcess(pid, "155283"); //100,20
//        List<Integer> ret = rungcov.mainProcess(pid, "155289"); //100,0

        List<Integer> ret = rungcov.mainProcess(pid, sid); //100,20
//        String suspiciousLines = Config.getTestProblemPath(pid, sid)+"suspiciousLines";
//        BufferedWriter out = new BufferedWriter(new FileWriter(suspiciousLines));
//        out.write(ret.toString().replace(" ", "").replace("[", "").replace("]", ""));
//        out.newLine();
//        out.flush();
//        out.close();

        String filePath = new File(Config.getProjectPath()).getParent() + "/php/" + sid + ".json";
        System.out.print(filePath);
        BufferedReader in = new BufferedReader(new FileReader(Config.getTestProblemPath(pid, sid)+sid+".cpp"));
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
        String s = "{\"dataset\":[";
        out.write(s);
        out.newLine();
        int cnt = 0;
        while((s = in.readLine()) != null){
            s = s.replaceAll("&", "&amp;");
            s = s.replaceAll("<", "&lt;");
            s = s.replaceAll(">", "&gt;");
            s = s.replaceAll("\"", "&quot;");
            s = s.replaceAll("'", "&apos;");
            s = s.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
            if(cnt != 0) {
                out.write(",");
                out.newLine();
            }
            cnt++;
            int flag = -1;
            Iterator<Integer> it = ret.iterator();
            int idx = 1;
            while(it.hasNext()){
                int cur = ((Integer)it.next()).intValue();
                if(idx > 9) break;
                if(cur == cnt){
                    flag = idx;
                    break;
                }
                idx++;
            }
            String stmp = "{\"line_number\":\""+cnt+"\",\"flag\":\""+flag+"\",\"code\":\""+s+"\"}";
            out.write(stmp);
            out.newLine();
        }
        s = "]}";
        out.write(s);
        out.newLine();

        out.flush();
        in.close();
        out.close();

        //patch
//        Integer totCases = ret.get(0);
//        Integer passedCases = ret.get(1);
//        Patch.fix(totCases,passedCases);
    }

    public static List<Integer> suspiciousCoverage(String sid, String pid) throws SQLException, IOException{
        List<Integer> ret = rungcov.mainProcess(pid, sid);
        return ret;
    }

}
