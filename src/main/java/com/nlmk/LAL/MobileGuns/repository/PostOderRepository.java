package com.nlmk.LAL.MobileGuns.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.PostOder;
import com.nlmk.LAL.MobileGuns.entity.PostOderId;

@Repository
public interface PostOderRepository
        extends JpaRepository<PostOder, PostOderId> {

    // Chercher par commandeSq + posteCde
    Optional<PostOder> findByIdCommandeSqAndIdPosteCde(
        String commandeSq, String posteCde);
}
