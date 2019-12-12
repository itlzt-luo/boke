package com.lzt.boke.controller;



import com.lzt.boke.mapper.QuestionMapper;
import com.lzt.boke.mapper.UserMapper;
import com.lzt.boke.model.Question;
import com.lzt.boke.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
@Controller
public class PublishController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionMapper questionMapper;

    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }
    @PostMapping("/publish")
    public String publish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            HttpServletRequest request,
            Model model
    ) {

        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        User user = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies
             ) {
            if ("token".equals(cookie.getName())) {
                String token = cookie.getValue();
                user = userMapper.getUserByToken(token);
                request.getSession().setAttribute("user", user);
                break;
            }
        }
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "publish";
        }
        if (title == null || title.equals("")) {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if (description == null || description.equals("")) {
            model.addAttribute("error", "内容不能为空");
            return "publish";
        }
        if (tag == null || tag.equals("")) {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }

        Question question = new Question();
        question.setCreator(user.getId());
        question.setDescription(description);
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(System.currentTimeMillis());
        question.setTitle(title);
        question.setTag(tag);

        questionMapper.insert(question);
        return "index";
    }
}
