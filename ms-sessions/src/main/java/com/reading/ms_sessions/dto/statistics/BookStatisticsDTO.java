package com.reading.ms_sessions.dto.statistics;


public record BookStatisticsDTO(
        int daysRead,
        double averagePagesPerDay,
        double averageSessionTime //recebe o valor do readingSpeed
) { }