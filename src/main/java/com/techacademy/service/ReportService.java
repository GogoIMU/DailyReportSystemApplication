package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
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

    // IDで1件を検索
    public Report findById(Integer id) {
        Optional<Report> option = reportRepository.findById(id);
        return option.orElse(null);
    }

    // 特定の従業員と日付に基づいて日報を検索
    public Report findByEmployeeAndReportDate(Employee employee, LocalDate reportDate) {
        Optional<Report> option = reportRepository.findByEmployeeAndReportDate(employee, reportDate);
        return option.orElse(null);
    }


    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {
        // 日付重複チェック
        Report existingReport = findByEmployeeAndReportDate(report.getEmployee(), report.getReportDate());
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
    public ErrorKinds update(Integer employeeId, Report existingReport) {
        // 日付重複チェック（一旦コメントアウト）
        // Report existingReportWithSameDate = findByEmployeeAndReportDate(existingReport.getEmployee(), existingReport.getReportDate());
        // if (existingReportWithSameDate != null && !existingReportWithSameDate.getId().equals(existingReport.getId())) {
        //     return ErrorKinds.DUPLICATE_ERROR;
        // }

        // 更新日時を設定
        LocalDateTime now = LocalDateTime.now();
        existingReport.setUpdatedAt(now);

        // 日報を保存（既存のレポートを更新）
        reportRepository.save(existingReport);
        return ErrorKinds.SUCCESS;
    }


}
