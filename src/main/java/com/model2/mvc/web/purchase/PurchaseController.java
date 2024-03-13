package com.model2.mvc.web.purchase;

import java.util.Map;

import com.model2.mvc.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class PurchaseController {
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	public PurchaseController() {
		System.out.println(this.getClass());
	}
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;

	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	@RequestMapping("/addPurchase.do")
	public ModelAndView addPurchase(@ModelAttribute("addVO") Purchase purchase,  String tranCode, 
																@ModelAttribute("user") User user, @ModelAttribute("prod") Product prod) throws Exception {

		System.out.println("addPurchaes.do");
		
		purchase.setBuyer(user);
		purchase.setPurchaseProd(prod);
		purchaseService.addPurchase(purchase);
		
		String viewName = "forward:/purchase/addPurchaseReadView.jsp";
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);
		modelAndView.addObject("purchase", purchase); // Add Purchase object to model
		
		return modelAndView;
		
	}
	
	@RequestMapping("/addPurchaseView.do")
	public ModelAndView addPurchaseView(@RequestParam("prod_no") int prodNo, Model model) throws Exception{
		
		System.out.println("/addPurchaseView.do");
		Product prod = productService.getProduct(prodNo);
		model.addAttribute("addview", prod);
		
		String viewName = "forward:/purchase/addPurchaseView.jsp";
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);
		
		return modelAndView;
	}
	
	@RequestMapping("/getPurchase.do")
	public ModelAndView getPurchase(@RequestParam int tranNo, Model model) throws Exception{
		
		System.out.println("/getPurchase.do");
	
		Purchase purchase = purchaseService.getPurchase(tranNo);
		model.addAttribute("purVo", purchase);
	
		String viewName = "forward:/purchase/purchaseReadView.jsp";
	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);
	
		return modelAndView;
	}
	
	@RequestMapping("/updatePurchase.do")
	public ModelAndView updatePurchase(@ModelAttribute("updateVo") Purchase purchase, @ModelAttribute("userVo") User user,
																	 Model model) throws Exception{
		
		System.out.println("updatePurchase.do");
		purchase.setBuyer(user);
		purchaseService.updatePurchase(purchase);
		
		Purchase purch = purchaseService.getPurchase(purchase.getTranNo());
		model.addAttribute("updateVO", purch);
		
		String viewName = "forward:/purchase/updatePurchaseReadView.jsp";
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);
		
		return modelAndView;
	}
	
	@RequestMapping("/updatePurchaseView.do")
	public ModelAndView updatePurchaseView(@RequestParam int tranNo, Model model) throws Exception{
		
		System.out.println("updatePurchaseView.do");
		Purchase purchase = purchaseService.getPurchase(tranNo);
		model.addAttribute("updateview", purchase);
		
		String viewName = "forward:/purchase/updatePurchaseView.jsp";
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);
		
		return modelAndView;
	}
	
	@RequestMapping("/updateTranCode.do")
	public ModelAndView updateTranCode(@ModelAttribute("tranCode") Purchase purchase, @ModelAttribute("prod") Product prod) throws Exception{
		
		System.out.println("/updateTranCode.do");
		purchase.setPurchaseProd(prod);
		System.out.println("TranCode 저장 값 : "+purchase.getTranCode());
		purchaseService.updateTranCode(purchase);
		
		String viewName1 = "forward:/listProduct.do?menu=manage";
		String viewName2 = "forward:/listProduct.do?menu=search";
		
		ModelAndView modelAndView = new ModelAndView();
		if( purchase.getTranCode().equals("2")) {
			modelAndView.setViewName(viewName1);
		}else if( purchase.getTranCode().equals("3")) {
			modelAndView.setViewName(viewName2);
		}
		
		return modelAndView;
	}


	@RequestMapping("/listPurchase.do")
	public ModelAndView listPurchase(@ModelAttribute("Search") Search search,
									 HttpSession session,
									 Model model) throws Exception{

		System.out.println("/listPurchase.do");

		if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}

		search.setPageSize(pageSize);
		User buyerId = (User) session.getAttribute("user");

		Map<String, Object> map = purchaseService.getPurchaseList(search, buyerId.getUserId());

		Page resultPage = new Page(search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize );
		System.out.println(resultPage);

		model.addAttribute("list", map.get("list"));//이거 수정했음
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);

//		System.out.println("List 저장된 값 : "+model.getAttribute("list").toString());

		String viewName = "forward:/purchase/purchaseList.jsp";
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);

		return modelAndView;

	}


}
