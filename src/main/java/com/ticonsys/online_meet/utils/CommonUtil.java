package com.ticonsys.online_meet.utils;

import com.ticonsys.online_meet.dto.EnumResponse;
import com.ticonsys.online_meet.enums.ApiMessage;
import com.ticonsys.online_meet.enums.Language;
import com.ticonsys.online_meet.enums.NamedConstant;
import com.ticonsys.online_meet.exception.CustomSecurityException;
import com.ticonsys.online_meet.security.CustomUserDetails;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CommonUtil {

    public static Long getUserIdFromSecurityContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }

        throw new CustomSecurityException(CommonUtil.getApiMessages(ApiMessage.ERROR_SECURITY_CONTEXT));
    }

    public static Language getLanguageFromSecurityContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Object object = authentication.getPrincipal();
        if (object instanceof CustomUserDetails && ((CustomUserDetails) object).getLanguage() != null)
            return ((CustomUserDetails) object).getLanguage();
        return Language.EN;
    }

    public static  <T extends NamedConstant> EnumResponse getEnumResponse(T enumType ) {
        if( enumType == null )
            return null;
        return new EnumResponse( enumType.getName(), enumType );
    }

    public static  <T extends NamedConstant>  String getEnumName(T enumType ) {
        if( enumType == null )
            return null;
        return enumType.getName();
    }

    public static String getApiMessages(ApiMessage apiMessage) {
        Language language = getLanguageFromSecurityContext();
        return language.equals( Language.EN ) ? apiMessage.getEn() : apiMessage.getBn();
    }

    public static String getApiMessages(ApiMessage apiMessage, String key, String value) {
        String message = getApiMessages(apiMessage);
        return message.replace( key, value );
    }

    public static <Req, Resp> Resp copyProperties(Req req, Resp resp) {
        BeanUtils.copyProperties( req, resp );
        return resp;
    }

    public static Date getCurrentDateTime() {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka")).getTime();
    }

    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Dhaka"));
    }

    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now(ZoneId.of("Asia/Dhaka"));
    }

}
