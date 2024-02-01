package com.example.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

	/**
	 * エラー情報を抽出する。
	 *
	 * @param req リクエスト情報
	 * @return エラー情報
	 */
	private static Map<String, Object> getErrorAttributes(HttpServletRequest req) {
		// DefaultErrorAttributes クラスで詳細なエラー情報を取得する
		ServletWebRequest swr = new ServletWebRequest(req);
		DefaultErrorAttributes dea = new DefaultErrorAttributes();
		return dea.getErrorAttributes(swr, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE));
	}

	/**
	 * レスポンス用の HTTP ステータスを決める。
	 *
	 * @param req リクエスト情報
	 * @return レスポンス用 HTTP ステータス
	 */
	private static HttpStatus getHttpStatus(HttpServletRequest req) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		return status;

	}

	/**
	 * HTML レスポンス用の ModelAndView オブジェクトを返す。
	 *
	 * @param req リクエスト情報
	 * @param mav レスポンス情報
	 * @return HTML レスポンス用の ModelAndView オブジェクト
	 */
	@ExceptionHandler({ Error.class, Exception.class })
	@RequestMapping(value = "/error", produces = "text/html")
	public ModelAndView myErrorHtml(HttpServletRequest req, ModelAndView mav) {

		// エラー情報を取得
		Map<String, Object> attr = getErrorAttributes(req);

		// HTTP ステータスを決める
		HttpStatus status = getHttpStatus(req);

		// HTTP ステータスをセットする
		mav.setStatus(status);

		// ビュー名を指定する
		// Thymeleaf テンプレートの場合は src/main/resources/templates/error.html
		mav.setViewName("error/500");

		// 出力したい情報をセットする
		mav.addObject("status", status.value());
		mav.addObject("error", attr.get("error"));
		mav.addObject("trace", attr.get("trace"));
		return mav;
	}

}
