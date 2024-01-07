package com.dororo.future.dororocrypto.controller;

import com.dororo.future.dororocrypto.service.FolderTreeService;
import com.dororo.future.dororocrypto.vo.BaseMvcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件夹层级树控制器
 *
 * @author Dororo
 * @date 2024-01-07 18:58
 */
@Slf4j
@RestController
@RequestMapping("/folder")
public class FolderTreeController {

    @Autowired
    private FolderTreeService folderTreeService;

    @GetMapping("/getTreeData")
    public BaseMvcResponse getTreeData(@RequestParam("path") String path) {
        return BaseMvcResponse.successData(folderTreeService.getTreeData(path));
    }
}
