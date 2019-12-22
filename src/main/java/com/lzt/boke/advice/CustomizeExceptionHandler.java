package com.lzt.boke.advice;

import com.alibaba.fastjson.JSON;
import com.lzt.boke.dto.ResultDTO;
import com.lzt.boke.exception.CustomizeErrorCode;
import com.lzt.boke.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * 自定义异常处理
 * 获取异常信息 在错误页面显示
 */
@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model, HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO;
            //返回json
            if (e instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) e);
            } else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
            try {//使用response暴力传送数据
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }else {
            //错误页面跳转
            if (e instanceof CustomizeException) {//自定义异常处理
                model.addAttribute("message", e.getMessage());
            } else {
                e.printStackTrace();
                model.addAttribute("message", "服务冒烟了，要不然您稍后再试试");
            }
            return new ModelAndView("error");
        }
    }
}
