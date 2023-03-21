package com.dorandoran.doranserver.controller;


import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "SwaggerTestController")
@RequestMapping("/v1/test")
public class HelloController {
    @ApiOperation(value = "test", notes = "테스트입니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK~!"),
            @ApiResponse(code = 404, message = "page not found!!!")
    })
    @GetMapping(value = "/board")
    public Map<String, String> selectBoard(@ApiParam(value = "게시판번호", required = true, example = "1") @RequestParam String no) {
        Map<String, String> result = new HashMap<>();
        result.put("test title", "테스트");
        result.put("test content", "테스트 내용");
        return result;
    }
}
