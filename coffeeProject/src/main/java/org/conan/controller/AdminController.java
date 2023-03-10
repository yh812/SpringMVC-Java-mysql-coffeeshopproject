package org.conan.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.conan.domain.AttachImageVO;
import org.conan.domain.Criteria;
import org.conan.domain.GoodsVO;
import org.conan.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



@Controller
@RequestMapping("/admin")
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	
	
	@Autowired
	private AdminService adminService;
	
	
	
	/* 包府磊 皋牢 其捞瘤 捞悼 */
	@RequestMapping(value="main", method = RequestMethod.GET)
	public void adminMainGET() throws Exception{
		
		logger.info("包府磊 其捞瘤 捞悼");
		
	}

	/* 惑前 包府(惑前格废) 其捞瘤 立加 */
	@RequestMapping(value = "goodsManage", method = RequestMethod.GET)
	public void goodsManageGET(Criteria cri, Model model) throws Exception{
		
		logger.info("惑前 包府(惑前格废) 其捞瘤 立加");
		
		/* 惑前 府胶飘 单捞磐 */
		List list = adminService.goodsGetList(cri);
		
		if(!list.isEmpty()) {
			model.addAttribute("list", list);
		} else {
			model.addAttribute("listCheck", "empty");
			return;
		}
		
	}	
	
	/* 惑前 殿废 其捞瘤 立加 */
	@RequestMapping(value = "goodsEnroll", method = RequestMethod.GET)
	public void goodsRegister(Model model) throws Exception{
		
		logger.info("惑前 殿废 其捞瘤 立加");
		
		ObjectMapper objm = new ObjectMapper();
		
		List list = adminService.cateList();
		
		String cateList = objm.writeValueAsString(list);
		
		model.addAttribute("cateList", cateList);
		
		//logger.info("函版 傈.........." + list);
		//logger.info("函版 gn.........." + cateList);
		
	}
	
	/* 惑前 炼雀 其捞瘤, 惑前 荐沥 其捞瘤 */
	@GetMapping({"/goodsDetail", "/goodsModify"})
	public void goodsGetInfoGET(int gdsNum, Criteria cri, Model model) throws JsonProcessingException {
		
		logger.info("goodsGetInfo()........." + gdsNum);
		
		ObjectMapper mapper = new ObjectMapper();
		
		/* 墨抛绊府 府胶飘 单捞磐 */
		model.addAttribute("cateList", mapper.writeValueAsString(adminService.cateList()));		
		
		/* 格废 其捞瘤 炼扒 沥焊 */
		model.addAttribute("cri", cri);
		
		/* 炼雀 其捞瘤 沥焊 */
		model.addAttribute("goodsInfo", adminService.goodsGetDetail(gdsNum));
		
	}
	
	/* 惑前 沥焊 荐沥 */
	@PostMapping("/goodsModify")
	public String goodsModifyPOST(GoodsVO vo, RedirectAttributes rttr) {
		
		logger.info("goodsModifyPOST.........." + vo);
		
		int result = adminService.goodsModify(vo);
		
		rttr.addFlashAttribute("modify_result", result);
		
		return "redirect:/admin/goodsManage";		
		
	}	
	
	/* 惑前 沥焊 昏力 */
	@PostMapping("/goodsDelete")
	public String goodsDeletePOST(int gdsNum, RedirectAttributes rttr) {
		
		logger.info("goodsDeletePOST..........");
		
		List<AttachImageVO> fileList = adminService.getAttachInfo(gdsNum);
		
		if(fileList != null) {
			
			List<Path> pathList = new ArrayList();
			
			fileList.forEach(vo ->{
				
				// 盔夯 捞固瘤
				Path path = Paths.get("E:\\upload", vo.getUploadPath(), vo.getUuid() + "_" + vo.getFileName());
				pathList.add(path);
				
				// 级匙老 捞固瘤
				path = Paths.get("E:\\upload", vo.getUploadPath(), "s_" + vo.getUuid()+"_" + vo.getFileName());
				pathList.add(path);
				
			});
			
			pathList.forEach(path ->{
				path.toFile().delete();
			});
			
		}		
		
		int result = adminService.goodsDelete(gdsNum);
		
		rttr.addFlashAttribute("delete_result", result);
		
		return "redirect:/admin/goodsManage";
		
	}	
	

	/* 惑前 殿废 */
	@PostMapping("/goodsEnroll")
	public String goodsEnrollPOST(GoodsVO goods, RedirectAttributes rttr) throws Exception {
		
		logger.info("goodsEnrollPOST......" + goods);
		
		adminService.Enroll(goods);
		
		rttr.addFlashAttribute("enroll_result", goods.getGdsName());
		
		return "redirect:/admin/goodsManage";
	}	
	
	/* 累啊 八祸 扑诀芒 */
	@GetMapping("/authorPop")
	public void authorPopGET(Criteria cri, Model model) throws Exception{
		
		logger.info("authorPopGET.......");
		
		cri.setAmount(5);
		

	}

	
	/* 梅何 颇老 诀肺靛 */
	@PostMapping(value="/uploadAjaxAction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<AttachImageVO>> uploadAjaxActionPOST(MultipartFile[] uploadFile) {
		
		logger.info("uploadAjaxActionPOST..........");
		
		/* 捞固瘤 颇老 眉农 */
		for(MultipartFile multipartFile: uploadFile) {
			
			File checkfile = new File(multipartFile.getOriginalFilename());
			String type = null;
			
			try {
				type = Files.probeContentType(checkfile.toPath());
				logger.info("MIME TYPE : " + type);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(!type.startsWith("image")) {
				
				List<AttachImageVO> list = null;
				return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
				
			}
			
		}// for		
		
		String uploadFolder = "C:\\upload";
		
		/* 朝楼 弃歹 版肺 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date date = new Date();
		
		String str = sdf.format(date);
		
		String datePath = str.replace("-", File.separator);
		
		/* 弃歹 积己 */
		File uploadPath = new File(uploadFolder, datePath);
		
		if(uploadPath.exists() == false) {
			uploadPath.mkdirs();
		}
		
		/* 捞固历 沥焊 淬绰 按眉 */
		List<AttachImageVO> list = new ArrayList();
		
		// 氢惑等 for
		for(MultipartFile multipartFile : uploadFile) {
			
			/* 捞固瘤 沥焊 按眉 */
			AttachImageVO vo = new AttachImageVO();
			
			/* 颇老 捞抚 */
			String uploadFileName = multipartFile.getOriginalFilename();
			vo.setFileName(uploadFileName);
			vo.setUploadPath(datePath);
			
			/* uuid 利侩 颇老 捞抚 */
			String uuid = UUID.randomUUID().toString();
			vo.setUuid(uuid);
			
			uploadFileName = uuid + "_" + uploadFileName;
			
			/* 颇老 困摹, 颇老 捞抚阑 钦模 File 按眉 */
			File saveFile = new File(uploadPath, uploadFileName);
			
			/* 颇老 历厘 */
			try {
				
				multipartFile.transferTo(saveFile);
				
				/* 芥匙老 积己(ImageIO) */
				/*
				File thumbnailFile = new File(uploadPath, "s_" + uploadFileName); 
				
				BufferedImage bo_image = ImageIO.read(saveFile);
					//厚啦 
					double ratio = 3;
					//承捞 臭捞
					int width = (int) (bo_image.getWidth() / ratio);
					int height = (int) (bo_image.getHeight() / ratio);				
				
				BufferedImage bt_image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
								
				Graphics2D graphic = bt_image.createGraphics();
				
				graphic.drawImage(bo_image, 0, 0,width,height, null);
					
				ImageIO.write(bt_image, "jpg", thumbnailFile);				
				*/
				
				/* 规过 2 */
				File thumbnailFile = new File(uploadPath, "s_" + uploadFileName);	
				
					BufferedImage bo_image = ImageIO.read(saveFile);

					//厚啦 
					double ratio = 3;
					//承捞 臭捞
					int width = (int) (bo_image.getWidth() / ratio);
					int height = (int) (bo_image.getHeight() / ratio);					
				
				
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
			} 
			
			list.add(vo);
			
		} //for

		ResponseEntity<List<AttachImageVO>> result = new ResponseEntity<List<AttachImageVO>>(list, HttpStatus.OK);
		
		return result;
	}
	
	/* 捞固瘤 颇老 昏力 */
	@PostMapping("/deleteFile")
	public ResponseEntity<String> deleteFile(String fileName){
		
		logger.info("deleteFile........" + fileName);
		
		File file = null;
		
		try {
			/* 芥匙老 颇老 昏力 */
			file = new File("c:\\upload\\" + URLDecoder.decode(fileName, "UTF-8"));
			
			file.delete();
			
			/* 盔夯 颇老 昏力 */
			String originFileName = file.getAbsolutePath().replace("s_", "");
			
			logger.info("originFileName : " + originFileName);
			
			file = new File(originFileName);
			
			file.delete();
			
			
		} catch(Exception e) {
			
			e.printStackTrace();
			
			return new ResponseEntity<String>("fail", HttpStatus.NOT_IMPLEMENTED);
			
		} // catch
		
		return new ResponseEntity<String>("success", HttpStatus.OK);
		
	}
	
	
	
}