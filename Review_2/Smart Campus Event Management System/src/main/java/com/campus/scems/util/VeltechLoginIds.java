package com.campus.scems.util;

import java.util.regex.Pattern;

public final class VeltechLoginIds {

    /** Veltech student: VTU + exactly 5 digits (e.g. VTU12345). */
    public static final Pattern STUDENT_PATTERN = Pattern.compile("^VTU\\d{5}$", Pattern.CASE_INSENSITIVE);

    /** Faculty portal: only these four login IDs are accepted (case-insensitive). */
    public static final String[] FACULTY_SEED_IDS = {"VTUF001", "VTUF002", "VTUF003", "VTUF004"};

    private VeltechLoginIds() {
    }

    public static String normalize(String raw) {
        return raw == null ? "" : raw.trim().toUpperCase();
    }

    public static boolean isValidStudentId(String id) {
        return id != null && STUDENT_PATTERN.matcher(id.trim()).matches();
    }
}
