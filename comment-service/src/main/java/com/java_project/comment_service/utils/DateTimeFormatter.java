package com.java_project.comment_service.utils;

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
    //Tạo 1 map tên strategyMap
    //Key là kiểu Long, value và 1 func có biến là Instant và trả về String
    //LinkHashMap giữ nguyên thứ tự khi các các phần tử được thêm vào map
    Map<Long, Function<Instant, String>> strategyMap = new LinkedHashMap<>();

    public DateTimeFormatter() {
        //Chèn các phần tử vào map strategiMap
        //Key = 60L (dưới 60s), value = func formatInSeconds
        strategyMap.put(60L, this::formatInSeconds);
        //Key = 3600L (dưới 3600s), value = func formatInMinutes
        strategyMap.put(3600L, this::formatInMinutes);
        //Key = 86400L (dưới 86400s), value = func formatInMinutes
        strategyMap.put(86400L, this::formatInHours);
        //Key = MAX_VALUE (giá trị lớn nhất của long), value = func formatInMinutes
        strategyMap.put(Long.MAX_VALUE, this::formatInDate);
    }

    public String format(Instant instant) {
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());

        var strategy = strategyMap.entrySet()//lấy tất cả các entry trong map strategyMap
                .stream()
                //lọc các entry có key lớn hơn elapseSeconds
                .filter(longFunctionEntry -> elapseSeconds < longFunctionEntry.getKey())
                .findFirst() //Lấy phần tử đầu tiên sau khi lọc
                .orElseThrow(); //tránh lỗi khi không tìm thấy

        //Trả về giá trị của cặp key-value đầu tiên sao khi lọc(là 1 trong các hàm format ở dưới)
        return strategy.getValue().apply(instant);
    }

    private String formatInSeconds(Instant instant) {
        //Tính số giây giữa 2 khoảng thời gian
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());

        //Trả về số giây đã qua
        //Dùng format để định dạng chuỗi %s -> elapseSeconds
        return String.format("%s seconds ago", elapseSeconds);
    }

    private String formatInMinutes(Instant instant){
        long elapseMinutes = ChronoUnit.MINUTES.between(instant, Instant.now());
        return String.format("%s minutes ago", elapseMinutes);
    }

    private String formatInHours(Instant instant){
        long elapseHours = ChronoUnit.HOURS.between(instant, Instant.now());
        return String.format("%s hours ago", elapseHours);
    }

    //Định dạng ngày
    private String formatInDate(Instant instant) {
        //Chuyển 1 instant(ngày giờ tại 1 nơi cố đinh) thành 1 localDateTime
        // dựa theo múi giờ hệ thống(ZoneId.systemDefault())
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        //Định dạng ngày giờ theo chuẩn ISO (VD: 2025-03-25)
        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ISO_DATE;

        //Trả về localDateTime với định dạng ISO
        return localDateTime.format(dateTimeFormatter);
    }
}
