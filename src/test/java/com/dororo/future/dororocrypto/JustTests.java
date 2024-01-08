package com.dororo.future.dororocrypto;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.dororo.future.dororocrypto.util.PathUtils;
import com.dororo.future.dororocrypto.vo.req.CryptoReqVo;
import org.junit.jupiter.api.Test;

public class JustTests {


    @Test
    public void test01() {
        String jsonStr = JSONUtil.toJsonStr(CryptoReqVo.builder().build(), JSONConfig.create().setIgnoreNullValue(false));
        System.out.println(jsonStr);
    }
}
