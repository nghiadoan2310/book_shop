package com.java_project.product_service.utils;

import java.text.Normalizer;

public class SlugUtils {

    public static String toSlug(String input) {
        //
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        return normalized.replaceAll("\\p{M}", "") // Bỏ dấu
                .toLowerCase()
                .replaceAll("đ", "d")            // Bỏ đ
                .replaceAll("Đ", "D")            // Bỏ Đ
                .replaceAll("[^a-z0-9\\s-]", "") // Xóa ký tự đặc biệt
                .replaceAll("\\s+", "-")         // Đổi khoảng trắng thành "-"
                .replaceAll("-{2,}", "-")        // Gộp nhiều dấu "-" liên tiếp
                .replaceAll("^-|-$", "");        // Xóa "-" ở đầu/cuối
    }
}
