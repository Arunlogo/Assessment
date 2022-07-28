package com.vivriti.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.vivriti.model.Books;

@Transactional
public interface BooksRepository extends CrudRepository<Books, Long> {

	Page<Books> findAll(Pageable pageable);

	@Modifying
	@Query("update Books book set book.bookCount = ?1 where book.id = ?2")
	int updateBookCount(Integer count, Long id);

	List<Books> findByIdIn(List<Long> id);

	List<Books> findByNameOrAuthor(String name, String author);

}
