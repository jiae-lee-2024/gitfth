package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j // 로깅 어노테이션
@Service // 서비스 객체 선언
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository; // 게시글 리파지터리 객체 주입

    public List<Article> index() {
        return articleRepository.findAll();
    }

    public Article show(Long id) {
        return articleRepository.findById(id).orElse(null);
    }


//    10/29일 코드 
//    public Article create(ArticleForm dto) {
//        return null;
//    }


    // 10/30일 코드 수정 완료

    public Article create(ArticleForm dto) {
        Article article = dto.toEntity();
        if (article.getId() != null) {
            return null;
        }
        return articleRepository.save(article);
    }


    public Article update(Long id, ArticleForm dto) {
        // 1. 수정용 엔티티를 생성하고 로그를 찍어본다.
        // DTO -> 엔티티 변환하기
        Article article = dto.toEntity();
        log.info("id: {}, article: {}", id, article.toString());
        // 2. 대상 엔티티를 찾는 코드도 그대로 사용한다.
        // 타깃 조회하기
        Article target = articleRepository.findById(id).orElse(null);
        // 3. 잘못된 요청을 처리하고 응답하는 코드 수정 필요하다.
        // 잘못된 요청 처리하기
        if (target == null || id != article.getId()) {
            log.info("잘못된 요청! id: {}, article: {}", id, article.toString());
            return null; // (응답은 컨트롤러가 하므로 null 반환)
        }
        // 4. 업데이트 및 정상응답. 서비스는 db에 수정 데이트를 업데이트 한 후 최종적으로 updated에 저장된 데이터를
        // 컨트롤러에 반환만 하면 된다. 응답은 컨트롤러가 한다.
        target.patch(article);
        Article updated = articleRepository.save(target);
        return updated; // 응답은 컨트롤러가 하므로 여기서는 수정 데이터만 반환
    }

    public Article delete(Long id) {
        // 1. 대상 찾기
        Article target = articleRepository.findById(id).orElse(null);
        // 2. 잘못된 요청 처리하기
        if (target == null) {
            return null; // 응답은 컨트롤러가 하므로 여기서는 null 반환
        }
        // 3. 대상 삭제하기
        articleRepository.delete(target);
        return target; // db에서 삭제한 대상을 컨트롤러에 반환

        
    }

    @Transactional
    public List<Article> createArticles(List<ArticleForm> dtos) {
        // 1. dto 묶음을(리스트)  엔티티 묶음으로(리스트) 변환하기
        // 2. 엔티티 묶음을(리스트)  db에 저장하기
        // 3. 강제 예외 발생 시키기
        // 4. 결과 값 반환하기
        List<Article> articleList = dtos.stream()
                .map(dto -> dto.toEntity())
                .collect(Collectors.toList());


        articleList.stream()
                .forEach(article -> articleRepository.save(article));


        articleRepository.findById(-1L)
                .orElseThrow(() -> new IllegalArgumentException("결제 실패!"));

        return  articleList;



    }
}


