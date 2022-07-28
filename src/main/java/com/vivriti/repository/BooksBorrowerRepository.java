package com.vivriti.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.vivriti.model.BooksBorrower;

@Transactional
public interface BooksBorrowerRepository extends CrudRepository<BooksBorrower, Long> {

	@Modifying
	@Query("delete BooksBorrower booksBorrower where booksBorrower.userId = ?1 and booksBorrower.bookId = ?2")
	int deleteBookBorrower(String userId, Long bookId);

	List<BooksBorrower> findByUserId(String userId);
}
