package com.datn.todo.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ToDoExtractor {
    private const val DATE_PATTERN = "dd-MM-yyyy"

    fun extractVietnameseTimeAndDate(string: String): String? {
        if (string.contains("giờ") && string.contains("phút") && string.contains("ngày") && string.contains(
                "tháng"
            ) && string.contains("năm")
        ) {
            val hour = string.substringAfterLast("giờ").substringBefore("phút").trim().toIntOrNull()
                ?: return null
            val minute =
                string.substringAfterLast("phút").substringBefore("ngày").trim().toIntOrNull()
                    ?: return null
            val day =
                string.substringAfterLast("ngày").substringBefore("tháng").trim().toIntOrNull()
                    ?: return null
            val month =
                string.substringAfterLast("tháng").substringBefore("năm").trim().toIntOrNull()
                    ?: return null
            val year = string.substringAfterLast("năm").trim().toIntOrNull() ?: return null

            if (hour < 0 || hour > 23) return null
            if (minute < 0 || minute > 59) return null
            if (day < 0 || day > 31) return null
            if (month < 0 || month > 12) return null
            if (year < 0) return null

            return if (minute < 10) {
                "$hour:0${minute}/$day-$month-$year"
            } else {
                "$hour:${minute}/$day-$month-$year"
            }
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun extractVietnameseTimeAndDateSecondFilter(string: String): String? {
        if (string.contains("giờ") && string.contains("phút")) {
            val hour = string.substringAfterLast("giờ").substringBefore("phút").trim().toIntOrNull()
                ?: return null
            val minute =
                string.substringAfterLast("phút").substringBefore("ngày").trim().toIntOrNull()
                    ?: return null
            if (hour < 0 || hour > 23) return null
            if (minute < 0 || minute > 59) return null

            if (string.lowercase().contains("ngày mai")) {
                val tomorrow = LocalDate.now().plusDays(1)
                return if (minute < 10) {
                    "$hour:0${minute}/${tomorrow.format(DateTimeFormatter.ofPattern(DATE_PATTERN))}"
                } else {
                    "$hour:${minute}/${tomorrow.format(DateTimeFormatter.ofPattern(DATE_PATTERN))}"
                }
            } else if (string.lowercase().contains("ngày kia")) {
                val tomorrow = LocalDate.now().plusDays(2)
                return if (minute < 10) {
                    "$hour:0${minute}/${tomorrow.format(DateTimeFormatter.ofPattern(DATE_PATTERN))}"
                } else {
                    "$hour:${minute}/${tomorrow.format(DateTimeFormatter.ofPattern(DATE_PATTERN))}"
                }
            }
        }
        return null
    }

    fun extractTimeAndDate(string: String): String? {
        if (string.contains("hour") && string.contains("minute") && string.contains("day") && string.contains(
                "month"
            ) && string.contains("year")
        ) {
            val hour =
                string.substringAfterLast("hour").substringBefore("minute").trim().toIntOrNull()
                    ?: return null
            val minute =
                string.substringAfterLast("minute").substringBefore("set time").trim().toIntOrNull()
                    ?: return null
            val day = string.substringAfterLast("day").substringBefore("month").trim().toIntOrNull()
                ?: return null
            val month =
                string.substringAfterLast("month").substringBefore("year").trim().toIntOrNull()
                    ?: return null
            val year =
                string.substringAfterLast("year").substringBefore("set date").trim().toIntOrNull()
                    ?: return null

            if (hour < 0 || hour > 23) return null
            if (minute < 0 || minute > 59) return null
            if (day < 0 || day > 31) return null
            if (month < 0 || month > 12) return null
            if (year < 0) return null

            return if (minute < 10) {
                "$hour:0${minute}/$day-$month-$year"
            } else {
                "$hour:${minute}/$day-$month-$year"
            }
        }
        return null
    }

    fun extractDesc(string: String): String {
        return string.substringBefore("hour")
    }

    fun extractVietnameseDesc(string: String): String {
        return string.substringBefore("giờ")
    }
}