package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.constants.Message;
import com.example.model.Category;
import com.example.model.TaxType;
import com.example.service.TaxTypeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/tax")
public class TaxTypeController {

	@Autowired
	private TaxTypeService taxTypeService;

	@GetMapping
	public String index(Model model) {
		List<List<TaxType>> groupedTaxTypes = taxTypeService.getGroupedTaxTypes();
		model.addAttribute("groupedTaxTypes", groupedTaxTypes);
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
			taxTypes = taxTypeService.save(entity.getRate());
			redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_INSERT);
			redirectAttributes.addAttribute("q", "create");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			e.printStackTrace();

		}
		return "redirect:/tax";
	}

	@GetMapping("/{rate}")
	public String show(Model model, @PathVariable Integer rate) {
		if (rate != null) {
			List<TaxType> taxtypes = taxTypeService.findByRate(rate);
			model.addAttribute("rateList", taxtypes);
		}
		return "tax_type/show";
	}

	@DeleteMapping("/{rate}")
	public String delete(@PathVariable Integer rate, RedirectAttributes redirectAttributes) {
		try {
			if (rate != null) {
				List<TaxType> taxtypes = taxTypeService.findByRate(rate);
				boolean existFlg = false;
				for (TaxType taxtype : taxtypes) {
					if (taxtype.getProducts().size() == 0) {
						continue;
					}
					existFlg = true;
					break;
				}
				if (existFlg) {
					redirectAttributes.addFlashAttribute("error", "商品と紐づいているため削除できません。");

				} else {
					taxTypeService.delete(rate);
					redirectAttributes.addFlashAttribute("success", Message.MSG_SUCESS_DELETE);

				}
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", Message.MSG_ERROR);
			throw new ServiceException(e.getMessage());
		}
		return "redirect:/tax";
	}

}
