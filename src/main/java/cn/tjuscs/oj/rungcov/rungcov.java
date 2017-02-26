package cn.tjuscs.oj.rungcov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cn.tjuscs.oj.cmdHelper.Compile;
import com.ncfxy.FaultLocalization.FaultLocalization;
import cn.tjuscs.oj.config.Config;
//import cn.tjuscs.oj.cmdHelper.ExecuteLinuxCommand;
import cn.tjuscs.oj.cmdHelper.ExecuteLinuxCommand;
import cn.tjuscs.oj.cmdHelper.JavaOperateFiles;

public class rungcov {

	public static List<Integer> mainProcess(String pid, String sid) throws NumberFormatException, IOException, SQLException {
		rungcov r = new rungcov();
		return r.runAndGetMat(sid, pid);
	}

	/**
	 * 运行并生成该程序的覆盖矩阵
	 *
	 * @param sid
	 *            代码编号
	 * @param pid
	 *            问题编号
	 * @throws NumberFormatException
	 * @throws IOException
	 * @since TOJ_Plus_Plus　Ver 1.0-SNAPSHOT
	 */
	public List<Integer> runAndGetMat(String sid, String pid)
			throws NumberFormatException, IOException, SQLException {
		List<Integer> retVal = new ArrayList<Integer>();
//		String workpath = new File("./").getCanonicalPath();
		String workpath = Config.getProjectPath();
		System.out.println(workpath);
//		String dataPath = "./data/toj_problem_" + pid;
//		dataPath = new File(dataPath).getCanonicalPath();
		String dataPath = Config.getProblemBasePath(pid);
		String casenumFileName = dataPath + "/splitedTestCases/" + pid
				+ "_total.txt";
		String inputFileName = dataPath + "/splitedTestCases/" + pid + "_";
		String outputFileName = dataPath + "/splitedTestCases/output";
//		String srcFileDir = dataPath + "/programs/commit_id_" + sid + "/";
		String srcFileDir = Config.getTestProblemPath(pid, sid);
		String srcFileName = dataPath + "/programs/commit_id_" + sid + "/"
				+ sid;
		String compileHelperPath = "sh " + workpath + "/" + "compile_helper.sh ";

		FileReader cnst = new FileReader(casenumFileName);
		BufferedReader fin = new BufferedReader(cnst);
		int casenum = Integer.valueOf(fin.readLine()).intValue();
		retVal.add(casenum);
		fin.close();

		// 编译文件
		Compile.compile(dataPath + "/programs/commit_id_" + sid + "/" + sid + ".src", dataPath + "/programs/commit_id_" + sid);
		ExecuteLinuxCommand.execute("mv " + dataPath + "/programs/commit_id_" + sid + "/" + sid + "_src.cpp " + dataPath + "/programs/commit_id_" + sid + "/" + sid + ".cpp");
		ExecuteLinuxCommand.execute(compileHelperPath
				+ (dataPath + "/programs/commit_id_" + sid) + " " + sid
				+ " g++");

		for (int i = 0; i < casenum; i++) {
			JavaOperateFiles.deleteFile(srcFileDir + sid + ".gcda");
			ExecuteLinuxCommand.execute(srcFileName + ".exe < "
					+ inputFileName + i + ".in > " + outputFileName + i
					+ ".out");
			// ExecuteLinuxCommand.execute("gcov "+srcFileName+".cpp");
			ExecuteLinuxCommand.execute(compileHelperPath
					+ (dataPath + "/programs/commit_id_" + sid) + " " + sid
					+ " gcov");
			JavaOperateFiles.deleteFile(srcFileDir+sid+"_"+i+".cpp.gcov");
			JavaOperateFiles.renameFile(srcFileDir, sid + ".cpp.gcov", sid
					+ "_" + i + ".cpp.gcov");
		}
		List<Integer> testResult = compareAndCombine((dataPath
				+ "/splitedTestCases/" + pid), (outputFileName), casenum);
		retVal.add(getMatrixFromGcov(srcFileDir, sid, casenum, testResult));
		return retVal;
	}

	/**
	 * 遍历gcov文件生成覆盖矩阵coverage_matrix.txt
	 *
	 * @param sourceDir
	 * @param programName
	 * @param caseNum
	 * @param testResult
	 * @throws IOException
	 * @since TOJ_Plus_Plus　Ver 1.0-SNAPSHOT
	 */
	private Integer getMatrixFromGcov(String sourceDir, String programName,
			int caseNum, List<Integer> testResult) throws IOException {
		// programName 使用sid
		Integer passedCasesNum = 0;
		String outputFileName = sourceDir + "coverage_matrix.txt";
		StringBuffer outputBuffer = new StringBuffer();
		outputBuffer.append("#Ver_# " + programName + '\n');
		outputBuffer.append("#NOTS# " + caseNum + '\n');
		List<String> executableLines = getExecutableLines(sourceDir
				+ programName + "_0.cpp.gcov");
		outputBuffer.append("#LOES# ");
		for (String s : executableLines) {
			outputBuffer.append(s + " ");
		}
		outputBuffer.append("\n");
		outputBuffer.append("#NOES# " + executableLines.size() + "\n");
		outputBuffer.append("#NOF_# 0\n");
		outputBuffer.append("#LOFS# 0\n");
		for (int i = 0; i < caseNum; i++) {
			outputBuffer.append("#CASE#" + getString(i) + "#R"
					+ testResult.get(i) + "# ");
			passedCasesNum += 1- testResult.get(i);
			String fileName = sourceDir + programName + "_" + i + ".cpp.gcov";
			try (Scanner cin = new Scanner(new File(fileName));) {
				while (cin.hasNext()) {
					String line = cin.nextLine();
					if (line.length() < 16)
						continue;
					if (!(line.charAt(13) == ' ' && line.charAt(14) == '0')) {
						if (line.charAt(9) == ':' && line.charAt(15) == ':'
								&& line.charAt(8) != '-') {
							if (line.charAt(8) != '#') {
								outputBuffer.append("1 ");
							} else {
								outputBuffer.append("0 ");
							}
						}
					}
				}
				outputBuffer.append("\n");
			}
		}
		try (FileOutputStream outputStream = new FileOutputStream(new File(
				outputFileName));) {
			outputStream.write(outputBuffer.toString().getBytes());
			;
		}
		FaultLocalization localization = new FaultLocalization(outputFileName);
		List<Integer> sus = localization.getSuspiciousList();
		System.out.println("代码行的可疑值排序为:");
		System.out.println(sus.toString());
		return passedCasesNum;
	}

	/**
	 * 将一个整数补全到5位,不足的前边补零
	 */
	private String getString(int x) {
		if (x < 10)
			return "0000" + x;
		else if (x < 100)
			return "000" + x;
		else if (x < 1000)
			return "00" + x;
		else if (x < 10000)
			return "0" + x;
		else
			return "" + x;
	}

	/**
	 * 遍历gcov生成文件，得到代码的可执行代码行列表
	 *
	 * @param gcovFile
	 * @return
	 * @throws FileNotFoundException
	 * @since TOJ_Plus_Plus　Ver 1.0-SNAPSHOT
	 */
	private List<String> getExecutableLines(String gcovFile)
			throws FileNotFoundException {
		Scanner cin = new Scanner(new File(gcovFile));
		List<String> list = new ArrayList<String>();
		while (cin.hasNext()) {
			String line = cin.nextLine();
			if (!(line.charAt(13) == ' ' && line.charAt(14) == '0')) {
				if (line.charAt(9) == ':' && line.charAt(15) == ':'
						&& line.charAt(8) != '-') {
					String s = line.substring(10, 15);
					s = s.replace(' ', '0');
					list.add(s);
				}
			}
		}
		cin.close();
		return list;
	}

	/**
	 * 对比所有测试用例的结果，0表示测试成功，1表示测试失败
	 * 
	 * @return List<Integer> 返回测试结果的数组
	 * @param expectResultBasePath
	 * @param actualResultBasePath
	 * @param caseNum
	 * @since TOJ_Plus_Plus　Ver 1.0-SNAPSHOT
	 */
	private List<Integer> compareAndCombine(String expectResultBasePath,
			String actualResultBasePath, int caseNum)
			throws FileNotFoundException, IOException {
		StringBuffer buffer = new StringBuffer();
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < caseNum; i++) {
			// 0表示成功，1表示失败
			if (JavaOperateFiles.compareTwoFile(expectResultBasePath + "_" + i
					+ ".out", actualResultBasePath + i + ".out")) {
				list.add(0);
			} else {
				list.add(1);
			}
		}
		return list;
	}

}
