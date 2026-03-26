package com.ds.app.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.Certification;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long>{

	List<Certification> findByVerifiedByHrFalse();

//	     findByEmployee_UserId(Long userId, Pageable pageable);

}
