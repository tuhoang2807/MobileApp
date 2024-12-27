package com.example.moblie_app.Utils;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            Log.e("DateUtils", "Ngày tháng không hợp lệ (null hoặc trống)");
            return "";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            if (date == null) {
                Log.e("DateUtils", "Không thể phân tích ngày tháng: " + dateString);
                return "";
            }
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            Log.e("DateUtils", "Lỗi khi định dạng ngày tháng: " + e.getMessage());
            return "";
        }
    }
}
