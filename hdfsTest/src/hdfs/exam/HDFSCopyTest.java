package hdfs.exam;
/*
 * -hdfs의 파일을 읽어서 새로운 파일을 생성

 * -input 파일 경로, output 파일 경로를 명령행 매개변수로 클래스파일
 *  실행 결과 캡쳐해서 메일 전송
 *  
 */

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSCopyTest {
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		
		FileSystem hdfs = null;
		
		FSDataOutputStream hdfsout = null;
		FSDataInputStream hdfsinput = null;
		
		try {
			hdfs=FileSystem.get(conf);
			
			Path pathin = new Path(args[0]);
			Path pathout = new Path(args[1]);
			
			hdfsinput = hdfs.open(pathin);
			hdfsout = hdfs.create(pathout);
			
			//hdfsinput.readUTF();
			while(true) {
				int data = hdfsinput.read();
				System.out.println((char)data);
				if(data==-1) {
					break;
				}
				hdfsout.write(data);
			}
			/*String str = hdfsinput.readUTF();
			hdfsout.writeUTF(str);
		
			System.out.println(str);*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
