package com.voice.voicespace.mp4tomp3.service;

import com.microsoft.playwright.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BilibiliLoginService {

    @Autowired
    private BilibiliCookieManager cookieManager;

    private static final String LOGIN_URL = "https://passport.bilibili.com/login";
    private static final long TIMEOUT_MS = 300000; // 5分钟等待扫码

    public boolean loginWithQrCode() {
        Playwright playwright = null;
        Browser browser = null;
        try {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate(LOGIN_URL);

            System.out.println("页面加载完成，等待二维码出现...");

            // 尝试多个可能的选择器
            try {
                page.waitForSelector(".qrcode-img", new Page.WaitForSelectorOptions().setTimeout(5000));
            } catch (Exception e) {
                System.out.println("尝试选择器 .qrcode-img 失败");
                try {
                    page.waitForSelector(".qrcode", new Page.WaitForSelectorOptions().setTimeout(5000));
                } catch (Exception e2) {
                    System.out.println("尝试选择器 .qrcode 失败");
                    page.waitForSelector("img", new Page.WaitForSelectorOptions().setTimeout(5000));
                }
            }

            System.out.println("请使用B站App扫码登录...");
            System.out.println("登录地址: " + page.url());

            // 等待登录成功 - 通过检查URL变化或特定元素
            page.waitForFunction(
                "() => window.location.href.includes('bilibili.com') && !window.location.href.includes('passport')",
                null,
                new Page.WaitForFunctionOptions().setTimeout(TIMEOUT_MS)
            );

            // 登录成功，提取cookies
            List<com.microsoft.playwright.options.Cookie> playwrightCookies = context.cookies();
            Map<String, String> cookies = new HashMap<>();
            for (com.microsoft.playwright.options.Cookie cookie : playwrightCookies) {
                cookies.put(cookie.name, cookie.value);
            }

            cookieManager.saveCookies(cookies);
            System.out.println("登录成功，Cookie已保存！");
            return true;

        } catch (Exception e) {
            System.err.println("登录失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        }
    }
}