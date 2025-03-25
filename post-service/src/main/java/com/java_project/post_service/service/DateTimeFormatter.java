package com.java_project.post_service.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class DateTimeFormatter {
    //Tạo 1 map tên strategiMap
    //Key là kiểu Long, value và 1 func có biến là Instant và trả về String
    //LinkHashMap giữ nguyên thứ tự khi các các phần tử được thêm vào map
    Map<Long, Function<Instant, String>> strategiMap = new LinkedHashMap<>();

    public DateTimeFormatter() {
        strategiMap.put(60L, this::formatInSeconds);
        strategiMap.put(3600L, this::formatInMinutes);
        strategiMap.put(86400L, this::formatInHours);
        strategiMap.put(Long.MAX_VALUE, this::formatInDate);
    }

    public String format(Instant instant) {
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());

        var strategy = strategiMap.entrySet()
                .stream()
                .filter(longFunctionEntry -> elapseSeconds < longFunctionEntry.getKey())
                .findFirst().get();

        return strategy.getValue().apply(instant);
    }

    private String formatInSeconds(Instant instant) {
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());

        return String.format("%s seconds", elapseSeconds);
    }

    private String formatInMinutes(Instant instant){
        long elapseMinutes = ChronoUnit.MINUTES.between(instant, Instant.now());
        return String.format("%s minutes", elapseMinutes);
    }

    private String formatInHours(Instant instant){
        long elapseHours = ChronoUnit.HOURS.between(instant, Instant.now());
        return String.format("%s hours", elapseHours);
    }

    private String formatInDate(Instant instant) {
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ISO_DATE;

        return localDateTime.format(dateTimeFormatter);
    }
}
