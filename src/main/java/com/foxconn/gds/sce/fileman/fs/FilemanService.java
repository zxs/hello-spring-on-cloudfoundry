package com.foxconn.gds.sce.fileman.fs;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@Service("filemanServiceFS")
public class FilemanService {
	
	public static String getHomeDirectory() {
		return "/tmp/blueimpUpload";
	}

	public static String relativePath(String pathWithHomeDir) {
		if(pathWithHomeDir!=null) {
			int idx = pathWithHomeDir.indexOf(getHomeDirectory());
			return pathWithHomeDir.substring(idx==-1?0:getHomeDirectory().length());
		}
		return null; 
	}
	
    /**
     * 描述 : <将文件保存到指定路径>. <br>
     *<p>
     *
     * @param multifile
     * @param path
     * @return
     * @throws IOException
     */
    public String saveFileToServer(MultipartFile multifile, String path)
            throws IOException {
        // 创建目录
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 读取文件流并保持在指定路径
        InputStream inputStream = multifile.getInputStream();
        OutputStream outputStream = new FileOutputStream(path + File.separator 
                + multifile.getOriginalFilename());
//        byte[] buffer = new byte[4048];//multifile.getBytes();
        byte[] buffer = new byte[12288]; // 8K=8192 12K=12288 64K= 
        int bytesum = 0;
        int byteread = 0;
        while ((byteread = inputStream.read(buffer)) != -1) {
            bytesum += byteread;
            outputStream.write(buffer, 0, byteread);
            outputStream.flush();
        }
        outputStream.close();
        inputStream.close();

        return path + File.separator + multifile.getOriginalFilename();
    }
    
    public boolean deleteFileFromServer(String file, String path)
            throws IOException {
        boolean success = Boolean.FALSE;
        File f = new File(path+file);
        if (f.exists()) {
           f.delete();
           success = Boolean.TRUE;
        }


        return success;
    }
    
	public boolean downloadFileFromServer(String file, String path, HttpServletResponse resp) throws IOException {
		boolean success = Boolean.FALSE;
        File f = new File(path+file);
        if (f.exists()) {
          FileInputStream is = new FileInputStream(f);
          resp.setContentType("application/octet-stream");
          resp.setHeader("Content-Disposition", "attachment;filename=\""+f.getName()+"\"");
          FileCopyUtils.copy(is, resp.getOutputStream());
        }
		return success;
	}
	
    
	public List<File> listUploaledFiles(
			String uploadHome) throws MalformedURLException, IOException, AuthenticationException {
		
		 // Local File System 
		File dir = new File(uploadHome);
		
		File[] allFiles = dir.listFiles();
		
		return allFiles!=null ? Arrays.asList(allFiles) : new ArrayList<File>();
	}
	
  
    public void json_encode(final HttpServletResponse response, Object o) throws IOException{
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Gson gs = new Gson();
        out.write(gs.toJson(o));
    }


    
    
}
