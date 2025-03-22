package com.example.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.lang.UUID;
import com.example.vo.VerifyCodeVO;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class VerifyCodeUtil {

    public VerifyCodeVO getVerifyCode() {
        // 创建一个图像验证码宽度为130，高度为48，包含4个字符，干扰线10个
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(130, 48, 4, 10);
        // 获取验证码的文本
        String code = circleCaptcha.getCode();
        // 获取验证码图片的Base64编码
        String ImageBase64 = circleCaptcha.getImageBase64Data();

        VerifyCodeVO verifyCodeVO = new VerifyCodeVO();
        verifyCodeVO.setCode(code);
        verifyCodeVO.setImageBase64(ImageBase64);
        return verifyCodeVO;
    }
}
