package com.foxconn.gds.sce.fileman.fs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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

@Controller
@RequestMapping(FilemanController.PATH)
public class FilemanController {

	final static String PATH = "fileman/fs";
	//final static String PATH = "/fileman/fs"; //注意这个与上面的区别
	
	@Resource(name = "filemanServiceFS")
	private FilemanService filemanService;

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

		String homeDir = FilemanService.getHomeDirectory();
		FileMeta fileMeta = new FileMeta();

		if (file != null && StringUtils.hasText(file.getOriginalFilename())) {
			// System.out.println(file.getOriginalFilename());

			fileMeta.setName(file.getOriginalFilename());
			fileMeta.setSize(file.getSize());
			// fileItem.setType(file.getContentType());
			String path = filemanService.saveFileToServer(file, homeDir);
			fileMeta.setUrl( FilemanController.PATH + "/download?file=" + FilemanService.relativePath(path));
			fileMeta.setDelete_url(FilemanController.PATH + "/delete?file=" + FilemanService.relativePath(path));
		}

		filemanService.json_encode(response, fileMeta);
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
	 */
	@RequestMapping(value = "/upload.multi", method = { RequestMethod.POST })
	public @ResponseBody
	String handleImport(DefaultMultipartHttpServletRequest multipartRequest,
			HttpServletResponse response) throws IOException {

		String homeDir = FilemanService.getHomeDirectory();
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
					String path = filemanService.saveFileToServer(multifile, homeDir);
					fileMeta.setUrl( FilemanController.PATH + "/download?file=" + FilemanService.relativePath(path));
					fileMeta.setDelete_url( FilemanController.PATH + "/delete?file=" + FilemanService.relativePath(path));
					
					list.add(fileMeta);
				}

			}
		}
		filemanService.json_encode(response, list);
		return null;

	}
	
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public 
	String handleDownload(
			@RequestParam(value = "file", required = false) String file,
			HttpServletResponse resp) throws IOException {
		String homeDir = FilemanService.getHomeDirectory();
		
		boolean success = filemanService.downloadFileFromServer(file, homeDir, resp);
		return null;
	}	

	@RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})
	public @ResponseBody
	String handleDelete(
			@RequestParam(value = "file", required = false) String file,
			HttpServletResponse response) throws IOException {
		String homeDir = FilemanService.getHomeDirectory();
		
		boolean success = filemanService.deleteFileFromServer(file, homeDir);
		filemanService.json_encode(response, success);
		return null;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody
	String handleList(HttpServletResponse response) throws IOException, AuthenticationException {
		String homeDir = FilemanService.getHomeDirectory();
		List<File> allFiles = filemanService.listUploaledFiles(homeDir);
		List<FileMeta> uploadedFiles = new ArrayList<FileMeta>();
		for(File f : allFiles) {
			if ( f.isFile() ) {
				FileMeta fm = new FileMeta();
				fm.setName( f.getName() );
				fm.setSize( f.length() );
				fm.setUrl( f.getPath() );
				fm.setDelete_url(f.getPath());
				fm.setUrl( FilemanController.PATH + "/download?file=" +  FilemanService.relativePath(f.getPath()));
				fm.setDelete_type("POST");
				fm.setDelete_url( FilemanController.PATH + "/delete?file=" + FilemanService.relativePath(f.getPath()));	
				uploadedFiles.add(fm);
			}
		}
		filemanService.json_encode(response, uploadedFiles);
		return null;
	}
	
}
