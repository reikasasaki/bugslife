package com.example.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public class CustomErrorController implements ErrorController {

	private static Map<String, Object> getErrorAttributes(HttpServletRequest req) {
		ServletWebRequest swr = new ServletWebRequest(req);
		DefaultErrorAttributes dea = new DefaultErrorAttributes();
		return dea.getErrorAttributes(swr, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE));
	}

	private static HttpStatus getHttpStatus(HttpServletRequest req) {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	@ExceptionHandler({ Error.class, Exception.class })
	public ModelAndView myErrorHtml(HttpServletRequest req, ModelAndView mav) {
		Map<String, Object> attr = getErrorAttributes(req);
		HttpStatus status = getHttpStatus(req);
		mav.setStatus(status);
		mav.setViewName("error/500");
		mav.addObject("status", status.value());
		mav.addObject("error", attr.get("error"));
		mav.addObject("trace", attr.get("trace"));
		return mav;
	}

}
