package com.sky.controller.admin;

import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssProperties aliOssProperties;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        String endpoint = aliOssProperties.getEndpoint();
        String accessKeyId = aliOssProperties.getAccessKeyId();
        String accessKeySercert = aliOssProperties.getAccessKeySecret();
        String bucketName = aliOssProperties.getBucketName();
        AliOssUtil aliOssUtil = new AliOssUtil(endpoint,accessKeyId,accessKeySercert,bucketName);

        String orignName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString() + orignName.substring(orignName.lastIndexOf("."));
        //生成36位的uuid字符串
        String filePath = aliOssUtil.upload(file.getBytes(), uuid);
        return Result.success(filePath);
    }
}
