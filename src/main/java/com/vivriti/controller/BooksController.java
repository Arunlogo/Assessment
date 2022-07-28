package com.vivriti.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vivriti.model.Books;
import com.vivriti.model.BooksBorrower;
import com.vivriti.repository.BooksBorrowerRepository;
import com.vivriti.repository.BooksRepository;

@Controller
@RequestMapping("/book")
public class BooksController {

	@Autowired
	BooksRepository booksRepository;

	@Autowired
	BooksBorrowerRepository booksBorrowerRepository;

	@GetMapping("/createBook/{userID}")
	public String createBook(Model model, @PathVariable(value = "userID") String userID) {
		model.addAttribute("userId", userID);
		return "createBook";
	}

	@PostMapping("/addBook/{userID}")
	public String userAddBook(Books book, @PathVariable(value = "userID") String userID, Model model) {
		book.setTotalBookCount(book.getBookCount());
		booksRepository.save(book);
		model.addAttribute("userId", userID);
		return findPaginated(1, model);

	}

	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model) {

		int pageSize = 5;

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		Page<Books> page = booksRepository.findAll(pageable);
		List<Books> listBooks = page.getContent();

		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("books", listBooks);

		return "index";
	}

	@GetMapping("/borrowBook/{userID}/{bookId}")
	public String borrowBook(@PathVariable(value = "userID") String userID,
			@PathVariable(value = "bookId") String bookId, Model model) {

		Optional<Books> book = booksRepository.findById(Long.parseLong(bookId));
		booksRepository.updateBookCount(book.get().getBookCount() - 1, Long.parseLong(bookId));

		BooksBorrower borrower = BooksBorrower.builder().bookId(book.get().getId()).userId(userID).build();
		booksBorrowerRepository.save(borrower);
		model.addAttribute("userId", userID);
		return findPaginated(1, model);
	}

	@GetMapping("/returnBook/{userID}/{bookId}")
	public String returnBook(@PathVariable(value = "userID") String userID,
			@PathVariable(value = "bookId") String bookId, Model model) {

		Optional<Books> book = booksRepository.findById(Long.parseLong(bookId));

		if (book.get().getBookCount() < book.get().getTotalBookCount())
			booksRepository.updateBookCount(book.get().getBookCount() + 1, Long.parseLong(bookId));

		booksBorrowerRepository.deleteBookBorrower(userID, book.get().getId());
		model.addAttribute("userId", userID);
		return findPaginated(1, model);
	}

	@GetMapping("/findBooks/{userID}")
	public String viewBooks(@PathVariable(value = "userID") String userID, Model model) {
		List<BooksBorrower> booksBorrowerList = booksBorrowerRepository.findByUserId(userID);
		// Function;
		List<Long> idList = booksBorrowerList.stream().map(BooksBorrower::getBookId).collect(Collectors.toList());

		List<Books> booksList = booksRepository.findByIdIn(idList);
		model.addAttribute("booksList", booksList);
		model.addAttribute("userId", userID);
		return "viewBooks";

	}

	@GetMapping("/searchBooks/{searchValue}")
	public String searchBooks(@PathVariable(value = "searchValue") String searchValue, Model model) {

		List<Books> booksList = booksRepository.findByNameOrAuthor(searchValue, searchValue);
		model.addAttribute("booksList", booksList);
		return "viewBooks";

	}

}
