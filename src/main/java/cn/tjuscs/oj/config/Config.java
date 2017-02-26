package cn.tjuscs.oj.config;

/**
 * Created by yunhao on 17-1-21.
 */

import cn.tjuscs.oj.cmdHelper.ExecuteLinuxCommand;
import cn.tjuscs.oj.mysqlHelper.DB;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Config {
    //所有的路径结尾都没有/,注意！

    //OJ系统的code路径
    private static String getCodeSourcePath() throws IOException{
        return new File(getProjectPath()).getParent() + "/code";
//        return "/media/moon/喷水小火龙/tojdata/code/code";
    }

    //OJ系统的data路径
    private static String getDataSourcePath() throws IOException{
        return new File(getProjectPath()).getParent() + "/data";
//        return "/media/moon/喷水小火龙/tojdata/code/code";
    }

    //项目的根目录
    public static String getProjectPath() throws IOException{
        return new File(System.getProperty("user.dir")).getCanonicalPath();
    }

    //tpp_data路径，原项目中的data目录路径
    public static String getDataTargetPath() throws IOException{
        return new File(getProjectPath()).getParent() + "/tpp_data";
    }

    public static String getTempPath() throws IOException{
        return new File(getProjectPath()).getParent() + "/tpp_temp";
    }

    //某一到题的.in文件路径即 tpp_data/toj_problem_1001/1001_0.in
    public static String getProblemInputFilePath(String pid) throws IOException{
        return checkProblemBasePath(pid) + "/" + pid + "_0.in";
    }

    //某一道题的.out文件路径 tpp_data/toj_problem_1001/1001.out
    public static String getProblemOutPutFilePath(String pid) throws IOException{
        return checkProblemBasePath(pid) + "/" + pid + "_0.out";
    }

    //某一道题的splitedTestCases文件夹路径 tpp_data/toj_problem_1001/splitedTestCases
    public static String getProblemSplitedTestCasesPath(String pid) throws IOException{
        return checkProblemBasePath(pid) + "/splitedTestCases";
    }

    //某一道题的正确代码的路径 tpp_data/toj_problem_1001/programs/commit_id_testid/testid.src
    public static String getRightProblemPath(String pid, String sid) throws IOException, SQLException{
        String srcRightProgram = checkProblemBasePath(pid) + "/programs/commit_id_" + sid;
        if(!isDirectoryExist(srcRightProgram)){
            ExecuteLinuxCommand.execute("mkdir -p " + srcRightProgram);
        }
        srcRightProgram += "/"+sid+".src";
        if(!isFileExist(srcRightProgram)){
            DB db = new DB("sonar", "sonar", "acmdata");
            ResultSet res = db.query("SELECT * FROM submit where pid=" + pid + " AND result=\'" + "Accepted\'");
            res.next();
            int srcSid = res.getInt("sid");
            ExecuteLinuxCommand.execute("cp " + getCodeSourcePath() + "/" + srcSid + ".src" + " " + srcRightProgram);
        }
        return srcRightProgram;
    }

    //某一道题的基目录 tpp_data/toj_problem_1001
    public static String getProblemBasePath(String pid) throws IOException{
        return checkProblemBasePath(pid);
    }

    //根据uid从数据库中查询所有的sid信息以及对应的提交结果(AC,WA,etc)
    public static ResultSet getAllSubmitIdFromProblemId(String pid) throws IOException, SQLException {
        DB db = new DB("sonar", "sonar", "acmdata");
        return db.query("SELECT sid, result FROM submit where pid=" + pid);
    }

    //根据sid从数据库中查询对应的pid以及对应的提交结果(AC,WA,etc)
    public static ResultSet getProblemIdFromSubmitId(String sid) throws IOException, SQLException {
        DB db = new DB("sonar", "sonar", "acmdata");
        return db.query("SELECT pid, result FROM submit where sid=" + sid);
    }

    //某个题目的提交的路径 tpp_data/toj_problem_1001/programs/commit_id_123456/123456.src
    public static String getTestProblemPath(String pid, String sid) throws IOException, SQLException{
        String srcTestProgram = checkProblemBasePath(pid) + "/programs/commit_id_" + sid;
        if(!isDirectoryExist(srcTestProgram)){
            ExecuteLinuxCommand.execute("mkdir -p " + srcTestProgram);
        }
        srcTestProgram += "/" + sid+".src";
        if(!isFileExist((srcTestProgram))){
            ExecuteLinuxCommand.execute("cp " + getCodeSourcePath() + "/" + sid + ".src " + srcTestProgram);
        }
        return checkProblemBasePath(pid) + "/programs/commit_id_" + sid + "/";
    }

    private static String checkProblemBasePath(String pid) throws IOException{
        String ret = getDataTargetPath() + "/toj_problem_" + pid;
        //检查toj_problem_pid目录是否存在,不存在则创建
        if(!isDirectoryExist(ret)){
            ExecuteLinuxCommand.execute("mkdir -p " + ret +"/programs");
        }
        ret += "/" + pid + "_0.in";
        //检查toj_problem_pid下的pid_0.in是否存在，不存在就从OJ原数据中拷贝过来
        if(!isFileExist(ret)){
            String srcInputFile = getDataSourcePath() + "/" + pid + ".in";
            //检查在OJ原数据是否存在pid.in这个文件，存在即为一组输入文件，不存在即为多组输入文件
            if(isFileExist(srcInputFile)){
                ExecuteLinuxCommand.execute("cp " + srcInputFile + " " + ret);
            }
            //存在pid文件夹，即有多组输入文件
            else{
                String srcInputDirectory = getDataSourcePath() + "/" + pid;
                File file = new File(srcInputDirectory);
                File flist[] = file.listFiles();
                int num = 0;
                String tarInputFile = getDataTargetPath() + "/toj_problem_" + pid + "/" + pid + "_";
                //依次读取pid文件夹下的所有文件，并拷贝到tpp_data目录下
                if(flist != null){
                    for (File item : flist){
                        ExecuteLinuxCommand.execute("cp " + item.getCanonicalPath() + " " + tarInputFile + num + ".in");
                        num++;
                    }
                }
            }
        }
        return getDataTargetPath() + "/toj_problem_" + pid;
    }

    private static boolean isFileExist(String str){
        File file = new File(str);
        return file.exists();
    }

    private static boolean isDirectoryExist(String str){
        File file = new File(str);
        return file.exists() || file.isDirectory();
    }

    private static boolean isFile(String str){
        File file = new File(str);
        return file.isFile();
    }

}
