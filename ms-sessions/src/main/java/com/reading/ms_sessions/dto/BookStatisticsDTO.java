package com.reading.ms_sessions.dto;


public record BookStatisticsDTO(
        int daysRead,
        double averagePagesPerDay,
        double averageSessionTime
) { }