package com.example.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.springboot.detail.CustomUserDetails;
import com.example.springboot.entity.Book;
import com.example.springboot.entity.User;
import com.example.springboot.service.BookService;
import com.example.springboot.service.UserService;

@Controller
public class BookController {

	@Autowired
	private BookService bookService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/addbook")
	public String addbook(Model model) {
		Book book = new Book();
		model.addAttribute("book", book);
		return "addbook";
	}

	@PostMapping("/savebook")
	public String savebook(@ModelAttribute("book") Book book, BindingResult result, RedirectAttributes redirectAttributes) {
		
		book.setReserve(false);
		book.setTaken(false);
		book.setDeleted(false);

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "error");
			return "redirect:/addbook";
		}

		bookService.save(book);
		redirectAttributes.addFlashAttribute("success", "success");
		return "redirect:/addbook";
	}

	@GetMapping("/editbooks")
	public String editbooks(Model model) {
		model.addAttribute("bookList", bookService.getAllBooks());
		return "editbooks";
	}

	@RequestMapping("/editBook/{id}")
	public String editBook(Model model, @PathVariable(name = "id") Long id) {

		Book book = bookService.getById(id);
		model.addAttribute("book", book);

		return "editBook";
	}

	@RequestMapping("/deleteBook/{id}")
	public String deleteBook(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
		Book book = bookService.getById(id);
		book.setDeleted(true);
		bookService.save(book);
		redirectAttributes.addFlashAttribute("success", "success");

		return "redirect:/editbooks";
	}
	
	@PostMapping("/saveEditedBook")
	public String saveEditedBook(@ModelAttribute("book") Book book, BindingResult result,RedirectAttributes redirectAttributes) {
		
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "error");
			return "redirect:/editBook";
		}

		bookService.save(book);
		redirectAttributes.addFlashAttribute("success", "success");
		return "redirect:/editBook";
	}
	
	@RequestMapping("/reserve/{id}")
	public String reserve(@PathVariable(name = "id") Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		User user=userService.getById(userDetails.getId());
		
		Book book = bookService.getById(id);
		book.setUser(user);
		book.setReserve(true);
		bookService.save(book);

		return "redirect:/reserve";
	}

	@RequestMapping("/cancelRezervation/{id}")
	public String cancelRezervation(@PathVariable(name = "id") Long id) {
		Book book = bookService.getById(id);
		book.setUser(null);
		book.setReserve(false);
		bookService.save(book);

		return "redirect:/reserve";
	}

	@RequestMapping("/reserve")
	public String reserve(Model model) {

		List<Book> listBook = bookService.getAllBooks();
		model.addAttribute("listBook", listBook);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		model.addAttribute("userId", userDetails.getId().toString());
		return "reserve";
	}
	
	@RequestMapping("/lend")
	public String lendAndReceive(Model model) {

		List<Book> listBook = bookService.getReservedBooks();
		model.addAttribute("listBook", listBook);

		return "lend";
	}

	@RequestMapping("/receive/{id}")
	public String receive(@PathVariable(name = "id") Long id) {
		
		Book book = bookService.getById(id);
		book.setUser(null);
		book.setTaken(false);
		book.setReserve(false);
		bookService.save(book);

		return "redirect:/receive";
	}

	@RequestMapping("/lend/{id}")
	public String lend(@PathVariable(name = "id") Long id) {
		
		
		Book book = bookService.getById(id);
		book.setTaken(true);
		book.setReserve(false);
		
		book.getUsers().add(book.getUser());
		bookService.save(book);

		return "redirect:/lend";
	}
	
	@PostMapping("/updatebook")
	public String updatebook(@ModelAttribute("book") Book book) {

		bookService.save(book);
		
		return "redirect:/editbooks";
	}
	
	@RequestMapping("/receive")
	public String receive(Model model) {

		List<Book> listBook = bookService.getTakenBooks();
		model.addAttribute("listBook", listBook);

		return "receive";
	}
	
	@RequestMapping("/borrowed")
	public String borrowed(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		User user=userService.getById(userDetails.getId());
		List<Book> listBook = bookService.getBooksTakenByUser(user);
		model.addAttribute("listBook", listBook);

		return "borrowed";
	}
	
	@RequestMapping("/cancelRezervationAdmin/{id}")
	public String cancelRezervationAdmin(@PathVariable(name = "id") Long id) {
		
		Book book = bookService.getById(id);
		book.setUser(null);
		book.setReserve(false);
		bookService.save(book);

		return "redirect:/lend";
	}
}
