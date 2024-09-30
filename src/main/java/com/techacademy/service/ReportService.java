package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findByCode(Integer employeeId, LocalDate reportDate) {
        return reportRepository.findByEmployeeIdAndReportDate(employeeId, reportDate).orElse(null);
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {
        // 日付重複チェック
        Report existingReport = findByCode(report.getEmployee().getId(), report.getReportDate());
        if (existingReport != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        // 日報のフラグと時間を設定
        report.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        // 日報を保存
        reportRepository.save(report);
        return ErrorKinds.SUCCESS; // 成功を返す
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Integer employeeId, Report report) {
        // 日付重複チェック
        Report existingReport = findByCode(employeeId, report.getReportDate());
        if (existingReport != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        // 更新日時を設定
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);

        // 日報を保存
        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }
}
