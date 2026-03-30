package com.nlmk.LAL.MobileGuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.Yard;

@Repository
	public interface YardRepository extends JpaRepository<Yard , String> {

	  

}
