package com.vivriti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vivriti.model.Users;

@Controller
@RequestMapping("/login")
public class LoginController {

	@Autowired
	BooksController booksController;

	@GetMapping
	public String login() {
		return "login";
	}

	@PostMapping
	public String userLogin(Users users, BindingResult result, Model model) {

		if (("Librarian".toLowerCase().equals(users.getUserName().toLowerCase())
				|| "Borrower".toLowerCase().equals(users.getUserName().toLowerCase()))
				&& "test".equals(users.getPassword())) {

			model.addAttribute("userId", users.getUserName());
			return booksController.findPaginated(1, model);
		} else {
			ObjectError error = new ObjectError("globalError", "Invalid");
			result.addError(error);
			return "login";
		}
	}

}
