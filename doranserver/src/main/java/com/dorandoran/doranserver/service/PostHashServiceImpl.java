package com.dorandoran.doranserver.service;

import com.dorandoran.doranserver.repository.PostHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostHashServiceImpl implements PostHashService{
    private final PostHashRepository postHashRepository;


}
