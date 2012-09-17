package com.foxconn.gds.sce.fileman.hdfs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.http.client.WebHDFSConnectionFactory;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.foxconn.gds.sce.fileman.FileMeta;
import com.google.gson.Gson;

@Controller("fileControllerHDFS")
@RequestMapping(FilemanController.PATH)
public class FilemanController {

	final static String PATH = "fileman/hdfs";
	
    @Resource(name = "webHDFSConnectionFactory")
    private WebHDFSConnectionFactory webHDFSConnectionFactory;
    
	
	@RequestMapping(value = "/homedir", method = RequestMethod.GET)
	public @ResponseBody
	String homeDir() throws IOException, AuthenticationException {
		
		return  webHDFSConnectionFactory.getConnection().getHomeDirectory();
	}	
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public /*@ResponseBody*/
	String handleList(@RequestParam("homedir") String homedir, HttpServletResponse response) throws IOException, AuthenticationException {
		String json = webHDFSConnectionFactory.getConnection().listStatus(homedir);
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        System.out.println("******* LIST=\n" + json);
        out.write(json);
		return null;
	}

	/**
	 * 描述 : <事先就知道确切的上传文件数目>. <br>
	 * <p>
	 * @param file
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/upload.single", method = RequestMethod.POST)
	public @ResponseBody
	String handleImport(
			@RequestParam(value = "file", required = false) MultipartFile file,
			HttpServletResponse response) throws IOException {

//		String uploadHome = GlobalVariable.getUploadHome();
//		FileMeta fileMeta = new FileMeta();
//
//		if (file != null && StringUtils.hasText(file.getOriginalFilename())) {
//			// System.out.println(file.getOriginalFilename());
//
//			fileMeta.setName(file.getOriginalFilename());
//			fileMeta.setSize(file.getSize());
//			// fileItem.setType(file.getContentType());
//			String path = filemanService.saveFileToServer(file, uploadHome);
//			// fileItem.setPath(path);
//		}
//
//		filemanService.json_encode(response, fileMeta);
		return null;

	}

	/**
	 * 描述 : <事先就并不知道确切的上传文件数目，比如FancyUpload这样的多附件上传组件>. <br>
	 * <p>
	 * 
	 * @param model
	 * @param multipartRequest
	 * @return
	 * @throws IOException
	 * @throws AuthenticationException 
	 */
	@RequestMapping(value = "/upload.multi", method = { RequestMethod.POST })
	public @ResponseBody
	String handleImport(DefaultMultipartHttpServletRequest multipartRequest,
			@RequestParam("working-directory") String workingDirectory,
			HttpServletResponse response) throws IOException, AuthenticationException {

		List<FileMeta> list = new ArrayList<FileMeta>();
		if (multipartRequest != null) {
			Iterator<String> iterator = multipartRequest.getFileNames();

			while (iterator.hasNext()) {
				MultipartFile multifile = multipartRequest
						.getFile((String) iterator.next());

				if (StringUtils.hasText(multifile.getOriginalFilename())) {
					System.out.println(multifile.getOriginalFilename());
					FileMeta fileMeta = new FileMeta();
					fileMeta.setName(multifile.getOriginalFilename());
					fileMeta.setSize(multifile.getSize());
					String path = workingDirectory + "/" + multifile.getOriginalFilename();
					System.out.println(path);
					
					webHDFSConnectionFactory.getConnection().create(path, multifile.getInputStream());
					fileMeta.setUrl( FilemanController.PATH + "/download?file=" + path);
					fileMeta.setDelete_url(FilemanController.PATH + "/delete?file=" +path);
					
					list.add(fileMeta);
				}

			}
		}
		json_encode(response, list);
		return null;

	}
	
	

	@RequestMapping(value = "/delete", method = {RequestMethod.DELETE, RequestMethod.POST})
	public @ResponseBody
	String handleDelete(
			@RequestParam(value = "file", required = false) String file,
			HttpServletResponse response) throws IOException, AuthenticationException {
		String filepath = new String(file.getBytes("iso-8859-1"), "UTF-8");
		return  webHDFSConnectionFactory.getConnection().delete(filepath);
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public /*@ResponseBody*/
	String handleDownload(
			@RequestParam(value = "file", required = false) String file,
			HttpServletRequest req,
			HttpServletResponse response) throws IOException, AuthenticationException {
		String filepath = new String(file.getBytes("iso-8859-1"), "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=\""+ file +"\"");
        webHDFSConnectionFactory.getConnection().open(filepath, response.getOutputStream());
		return null;
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
