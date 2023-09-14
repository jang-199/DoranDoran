package com.dorandoran.doranserver.domain.report.domain.ReportType;

import lombok.Getter;

@Getter
public enum ReportType {
    MENU_1("선정성"),
    MENU_2("폭력성"),
    MENU_3("욕설 및 비방"),
    MENU_4("광고"),
    MENU_5("불건전한 만남 유도"),
    MENU_6("불건전한 닉네임"),
    MENU_7("기타");

    private final String menu;

    ReportType(String menu) {
        this.menu = menu;
    }

    public static ReportType getReportType(String reportContent) {
        ReportType reportType = null;
        for (ReportType reportTypeValue : ReportType.values()) {
            if (reportTypeValue.getMenu().equals(reportContent)){
                reportType = reportTypeValue;
            }
        }

        if (reportType == null){
            reportType = ReportType.MENU_7;
        }
        return reportType;
    }
}
