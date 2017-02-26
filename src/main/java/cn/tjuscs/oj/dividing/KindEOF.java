package cn.tjuscs.oj.dividing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

//import cn.tjuscs.oj.cmdHelper.ExecuteLinuxCommand;
import cn.tjuscs.oj.cmdHelper.ExecuteLinuxCommand;
import cn.tjuscs.oj.config.Config;

public class KindEOF extends FileKind{
	public final int MAX_LINE = 1000000;
	public String[] ipt = new String[MAX_LINE];
	public String[] opt = new String[MAX_LINE];
	public String[] tmp = new String[MAX_LINE];
	public String tmpFileName;
	public String Split = "split";
	public int IFLen,OFLen,curInLen,curOutLen,prvInLen,prvOutLen,FileIndex;
	public List<String> DonePaths;
	
	public KindEOF(String sourcePath, String outputFilePath, String targetPath, String rightProPath) {
		super(sourcePath, outputFilePath, targetPath, rightProPath);
		// TODO Auto-generated constructor stub
	}

	public KindEOF(String pid, String sid) throws IOException, SQLException {
		// TODO Auto-generated constructor stub
		super(pid, sid);
	}

	@Override
	int judgeKind() throws IOException {
		// TODO Auto-generated method stub
		return EOF_Kind;
	}

	@Override
	boolean splitFile() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		String  ExName = compile(this.rightProPath);
		IFLen = OFLen = curInLen = curOutLen = prvInLen = prvOutLen = FileIndex = 0;
		FileReader ifst = new FileReader(sourceFilePath);
		FileReader ofst = new FileReader(outputFilePath);
		BufferedReader IFin = new BufferedReader(ifst);
		BufferedReader OFin = new BufferedReader(ofst);
		while((ipt[IFLen]=IFin.readLine()) != null){
			IFLen++;
		}
		while((opt[OFLen]=OFin.readLine()) != null){
			OFLen++;
		}
		String arg = new String("");
		
		arg = ipt[0];
		curInLen = 1;
		for(int i=1;i<=IFLen;i++){
			//System.out.println(i);
			curOutLen = 0;
			for(;curInLen<i;curInLen++){
				arg = arg + "\n" + ipt[curInLen];
			}
			tmpFileName = Config.getTempPath() + "/tempFile.in";
			BufferedWriter fout = new BufferedWriter(new FileWriter(tmpFileName));
			fout.write(arg);
			fout.flush();
			fout.close();
			fout = new BufferedWriter(new FileWriter(Config.getTempPath() + "/tempFile.out"));
			fout.close();
			ExecuteLinuxCommand.execute(ExName+" < " + tmpFileName + " > " + Config.getTempPath() + "/tempFile.out" + "\n");
//			Thread.sleep(1000);
	/*
			Character terminate;
			terminate = 3;
			ExecuteLinuxCommand.execute(terminate.toString());
	*/		
			BufferedReader getOut = new BufferedReader(new FileReader(Config.getTempPath() + "/tempFile.out"));
			while( (tmp[curOutLen]=getOut.readLine()) != null ){
				//System.out.println(tmp[curOutLen]);
				curOutLen++;
			}
//			System.out.println(curOutLen);
			if(curOutLen > prvOutLen && isprefix()){
				//System.out.println(i);
				String pathin = this.targetFilePath + "/" + this.pid + "_" + FileIndex + ".in";
				String pathout = this.targetFilePath + "/" + this.pid + "_" + FileIndex + ".out";
				fout = new BufferedWriter(new FileWriter(pathin));
				System.out.println(pathin);
				//DonePaths.add(new String(Split+FileIndex+".out"));
				for(;prvInLen < curInLen;prvInLen++){
					//System.out.println(prvInLen);
					fout.write(ipt[prvInLen]);
					fout.write("\n");
					fout.flush();
				}
				FileIndex++;
				prvOutLen = curOutLen;
				System.out.println("A file is created");
				ExecuteLinuxCommand.execute(ExName+" < " + pathin + " > " + pathout + "\n");
			}
			getOut.close();
		}
		IFin.close();
		OFin.close();
		BufferedWriter num = new BufferedWriter(new FileWriter(this.targetFilePath + "/" + this.pid + "_total.txt"));
		num.write(String.valueOf(FileIndex));
		num.flush();
		num.close();
		//return DonePaths;
		return false;
	}
	public List<String> getPathes(){
		return this.DonePaths;
	}
	
	public boolean isprefix(){
		for(int i=0;i<curOutLen;i++){
			if(!opt[i].equals(tmp[i])) return false;
		}
		return true;
	}
}
