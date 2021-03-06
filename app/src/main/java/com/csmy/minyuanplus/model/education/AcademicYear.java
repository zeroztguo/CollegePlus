package com.csmy.minyuanplus.model.education;

import org.litepal.crud.DataSupport;

/**
 * 学年实体，数据库模型
 * Created by Zero on 16/6/27.
 */
public class AcademicYear extends DataSupport{
    private String academicYear;

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    @Override
    public String toString() {
        return "AcademicYear{" +
                "academicYear='" + academicYear + '\'' +
                '}';
    }
}
