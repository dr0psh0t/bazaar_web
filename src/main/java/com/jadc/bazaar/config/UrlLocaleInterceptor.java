package com.jadc.bazaar.config;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

@Component
public class UrlLocaleInterceptor implements HandlerInterceptor {
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String[] tokens = request.getRequestURI().trim().split("/");

		try {
			String newLocale = tokens[1];

			if (newLocale != null) {
				LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

				if (localeResolver == null) {
					throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
				}

				if (Languages.getNames().contains(newLocale)) {
					localeResolver.setLocale(request, response, parseLocaleValue(newLocale));
				} else {
					localeResolver.setLocale(request, response, parseLocaleValue("ja"));
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return true;
	}

	@Nullable
	protected Locale parseLocaleValue(String localeValue) {
		return StringUtils.parseLocale(localeValue);
	}
}
