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
import com.example.springboot.service.BookService;

@Controller
public class BookController {

	@Autowired
	private BookService bookService;
	
	@GetMapping("/addbook")
	public String addbook(Model model) {
		Book book = new Book();
		model.addAttribute("book", book);
		return "addbook";
	}

	@PostMapping("/savebook")
	public String savebook(@ModelAttribute("book") Book book, BindingResult result,
			RedirectAttributes redirectAttributes) {
		book.setReserve(false);
		book.setTaken(false);
		book.setDeleted(false);
		book.setUserId(0L);
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
		
		Book book = bookService.getById(id);
		book.setUserId(userDetails.getId());
		book.setReserve(true);
		bookService.save(book);

		return "redirect:/reserve";
	}

	@RequestMapping("/cancelRezervation/{id}")
	public String cancelRezervation(@PathVariable(name = "id") Long id) {
		

		Book book = bookService.getById(id);
		book.setUserId(0L);
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
}
