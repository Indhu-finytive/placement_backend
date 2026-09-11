package com.uniq.placement.util;

import com.uniq.placement.entity.enums.DuePeriod;

import java.time.LocalDate;

public class DateUtils {

    public static LocalDate calculateDueDate(LocalDate joiningDate, DuePeriod duePeriod, Integer customDays) {
        if (joiningDate == null) return null;
        if (duePeriod == DuePeriod.CUSTOM) {
            return customDays != null ? joiningDate.plusDays(customDays) : joiningDate;
        }
        return joiningDate.plusDays(duePeriod.toDays());
    }
}
