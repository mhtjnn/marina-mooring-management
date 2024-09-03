package com.marinamooringmanagement.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ThymeleafService {

    String createContent(String template, Map<String, Object> variables);

}
