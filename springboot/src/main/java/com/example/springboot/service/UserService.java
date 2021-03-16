package com.example.springboot.service;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springboot.entity.Role;
import com.example.springboot.entity.User;
import com.example.springboot.repository.RoleRepository;
import com.example.springboot.repository.UserRepository;

import net.bytebuddy.utility.RandomString;

@Service
public class UserService {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JavaMailSender mailSender;
	
	public List<User> listAll() {
		return repo.findAll();
	}
	
	@Value("${spring.mail.username}")
	private String email;
	
	public void register(User user, String siteURL) 
			throws UnsupportedEncodingException, MessagingException {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		String randomCode = RandomString.make(64);
		user.setVerificationCode(randomCode);
		user.setEnabled(false);
		
		Role role=roleRepository.findByRole(2L);
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		user.setRoles(roles);
		
		repo.save(user);
		
		sendVerificationEmail(user, siteURL);
	}
	
	private void sendVerificationEmail(User user, String siteURL) 
			throws MessagingException, UnsupportedEncodingException {
		String toAddress = user.getEmail();
		String fromAddress = email;
		String senderName = "Spring Boot Project";
		String subject = "Please verify your registration";
		String content = "Dear [[name]],<br>"
				+ "Please click the link below to verify your registration:<br>"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
				+ "Thank you,<br>"
				+ "Spring Boot Project.";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		content = content.replace("[[name]]", user.getFullName());
		String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
		
		content = content.replace("[[URL]]", verifyURL);
		
		helper.setText(content, true);
		
		mailSender.send(message);
		
		
	}
	
	public boolean verify(String verificationCode) {
		User user = repo.findByVerificationCode(verificationCode);
		
		if (user == null || user.isEnabled()) {
			return false;
		} else {
			user.setVerificationCode(null);
			user.setEnabled(true);
			repo.save(user);
			
			return true;
		}
		
	}
	
	public User getById(Long id) {
		
		return repo.getOne(id);
	}
	
	public void save(User user) {
		
		repo.save(user);
	}
	
	public String passwordEncode(String password) {
	
		return passwordEncoder.encode(password);
	}
	
	public boolean passwordMatchesControl(String password,String encoded) {
	
		return passwordEncoder.matches(password, encoded);
	}
	
	public User findByEmail(String email) {
		
		return repo.findByEmail(email);
	}
}
