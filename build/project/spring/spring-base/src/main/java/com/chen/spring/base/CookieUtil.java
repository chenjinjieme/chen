package com.chen.spring.base;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    private final Cookie cookie;

    public CookieUtil(String name, String value) {
        cookie = new Cookie(name, value);
    }

    public CookieUtil(Cookie cookie) {
        this.cookie = cookie;
    }

    public static Cookie get(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) for (Cookie cookie : cookies) if (cookie.getName().equals(name)) return cookie;
        return null;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public CookieUtil setValue(String newValue) {
        cookie.setValue(newValue);
        return this;
    }

    public CookieUtil setPath(String uri) {
        cookie.setPath(uri);
        return this;
    }

    public CookieUtil setMaxAge(int expiry) {
        cookie.setMaxAge(expiry);
        return this;
    }
}
