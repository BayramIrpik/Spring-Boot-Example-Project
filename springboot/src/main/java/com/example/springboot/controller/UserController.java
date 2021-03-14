package com.example.springboot.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.springboot.detail.CustomUserDetails;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/process_register")
	public String processRegister(User user, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		userService.register(user, getSiteURL(request));
		return "register_success";
	}

	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	@GetMapping("/verify")
	public String verifyUser(@Param("code") String code) {
		if (userService.verify(code)) {
			return "verify_success";
		} else {
			return "verify_fail";
		}
	}
	
	@RequestMapping("/changepassword")
	public String changepassword(
			@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("newPasswordAgain") String newPasswordAgain) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		User user=userService.getById(userDetails.getId());
		
		if(userService.passwordMatchesControl(oldPassword, user.getPassword())) {
			
			if(newPassword.equals(newPasswordAgain)) {
				
				user.setPassword(userService.passwordEncode(newPassword));
				
				userService.save(user);
				return "redirect:/password/3";
			}
			return "redirect:/password/2";
		}
		
		return "redirect:/password/1";
	}
	
	@RequestMapping("/password/{id}")
	public String sifre(Model model,@PathVariable(name = "id") Long id) {
		String message="";
		if(id==1L)
			message="Your old password is incorrect, the transaction failed.";
		
		if(id==2L)
			message= "Your new password is not the same. Operation failed.";
		
		if(id==3L)
			message= "The password has been changed successfully.";
		
		model.addAttribute("message", message);
	
		return "changepassword";
	}
}
