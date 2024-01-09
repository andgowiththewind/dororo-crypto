package com.dororo.future.dororocrypto;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.dororo.future.dororocrypto.util.AesUtils;
import com.dororo.future.dororocrypto.util.PathUtils;
import com.dororo.future.dororocrypto.vo.req.CryptoReqVo;
import org.junit.jupiter.api.Test;

public class JustTests {


    @Test
    public void test01() {
        System.out.println(FileUtil.mainName(".asdsadasd"));
        System.out.println(FileUtil.extName(".asdsadasd"));
    }
}
