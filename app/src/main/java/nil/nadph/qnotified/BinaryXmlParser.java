package nil.nadph.qnotified;
import java.io.*;

public class BinaryXmlParser{
	byte[] byteSrc;

	public BinaryXmlParser(String filePath){
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try{
			fis=new FileInputStream(filePath);
			bos=new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len=fis.read(buffer))!=-1){
				bos.write(buffer,0,len);
			}
			byteSrc=bos.toByteArray();
		}catch(Exception e){
			Utils.log("parse xml error:"+e.toString());
		}finally{
			try{
				fis.close();
				bos.close();
			}catch(Exception e){}
		}
	}
	
	
	
	
}
