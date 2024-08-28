package com.architecture.archi.content.content.controller;

import com.architecture.archi.content.content.controller.docs.ContentControllerDocs;
import com.architecture.archi.content.content.service.ContentReadService;
import com.architecture.archi.content.content.service.ContentWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/content")
public class ContentController implements ContentControllerDocs {
//    private final ContentReadService contentReadService;
//    private final ContentWriteService contentWriteService;


}
