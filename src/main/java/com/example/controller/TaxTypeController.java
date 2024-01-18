package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.constants.Message;
import com.example.model.TaxType;
import com.example.service.TaxTypeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/tax")
public class TaxTypeController {

	@Autowired
	private TaxTypeService taxTypeService;

	@GetMapping
	public String index(Model model) {
		List<TaxType> all = taxTypeService.findAll();
		model.addAttribute("taxList", all);
		return "tax_type/index";
	}

	@GetMapping(value = "/new")
	public String create(Model model, @ModelAttribute TaxType entity) {
		model.addAttribute("taxType", entity);
		return "tax_type/form";
	}

	@PostMapping
	public String create(RedirectAttributes redirectAttributes, @ModelAttribute TaxType entity) {
		List<TaxType> taxTypes = new ArrayList<>();
		try {
			taxTypes = taxTypeService.save(entity.getId(), entity.getTaxRate());
			redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_INSERT);
			redirectAttributes.addAttribute("q", "create");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			e.printStackTrace();

		}
		return "redirect:/tax";
	}

}
