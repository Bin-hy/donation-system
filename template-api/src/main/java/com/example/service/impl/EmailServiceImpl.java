package com.example.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.example.service.EmailService;
import com.example.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 功能：
 *
 * @author 熊浩毅
 * @time 2025-03-22 23:01
 * @Description
 */
@Service
public class EmailServiceImpl implements EmailService {
    private static final int CODE_EXPIRE_MINUTES = 5;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public void sendRegistEmail(String emailTo) {
        // hutool 生成6w位随机验证码
        String code = RandomUtil.randomNumbers(6);
        // 添加到Redis缓存中
        ValueOperations<String, String> ops  = redisTemplate.opsForValue();
        redisTemplate.delete(emailTo);
        ops.set(emailTo,code , CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        // 调用服务层方法发送邮件
        emailUtil.sendHtmlMail(emailTo,"【邮箱验证码】注册用户", buildOperaEmailHtml("注册用户",code));
    }

    @Override
    public void sendPasswordFindEmail(String emailTo) {
        // hutool 生成6w位随机验证码
        String code = RandomUtil.randomNumbers(6);
        // 添加到Redis缓存中
        ValueOperations<String, String> ops  = redisTemplate.opsForValue();
        redisTemplate.delete(emailTo);
        ops.set(emailTo,code , CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        // 调用服务层方法发送邮件
        emailUtil.sendHtmlMail(emailTo,"【邮箱验证码】找回密码", buildOperaEmailHtml("找回密码",code));
    }

    @Override
    public boolean checkEmailCode(String email, String code) {
        ValueOperations<String, String> ops  = redisTemplate.opsForValue();
        // 验证码正确
        if (code.equals(ops.get(email))) {
            // 删除验证码
            redisTemplate.delete(email);
            return true;
        }
        return false;
    }

    // 通用 操作构建邮箱 ： 用户注册 | 找回密码
    private String buildOperaEmailHtml(String operator, String code) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    /* 保持原有样式，此处省略部分CSS以保持简洁 */
                    .verification-code {
                        text-align: center;
                        padding: 20px;
                        margin: 25px 0;
                        background: #ffffff;
                        border: 3px solid #3498db;
                        border-radius: 8px;
                        font-size: 32px;
                        letter-spacing: 8px;
                        color: #2c3e50;
                        font-weight: 700;
                        box-shadow: 0 2px 8px rgba(52,152,219,0.2);
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>公益捐献网</h1>
                        <p>%s验证码</p>
                    </div>
                    <div class="notice">
                        您正在进行%s操作，请使用以下验证码完成身份验证：
                    </div>
                    <div class="verification-code">
                        %s
                    </div>
                    <div class="footer">
                        <p>此验证码将在%d分钟内失效</p>
                        <p>为保障账户安全，请勿将验证码告知他人</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(operator,operator,code, CODE_EXPIRE_MINUTES);

    }
}
