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
        Optional<Report> option = reportRepository.findByEmployeeAndReportDateAndDeleteFlgFalse(employee, reportDate);
        return option.orElse(null);
    }


    // 従業員に紐づく日報を取得
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {
        // 入力チェック
        ErrorKinds inputCheckResult = validateReport(report);
        if (inputCheckResult != ErrorKinds.SUCCESS) {
            return inputCheckResult;
        }

        // 日付重複チェック
        Report existingReport = findByEmployeeAndReportDate(report.getEmployee(), report.getReportDate());
        if (existingReport != null) {
            return ErrorKinds.DATECHECK_ERROR;
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

    // 日報削除
    @Transactional
    public ErrorKinds delete(Report report) {
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report existingReport) {
        // 更新日時を設定
        LocalDateTime now = LocalDateTime.now();
        existingReport.setUpdatedAt(now);

        // 日報を保存（既存のレポートを更新）
        reportRepository.save(existingReport);
        return ErrorKinds.SUCCESS;
    }

    // 入力チェック
    public ErrorKinds validateReport(Report report) {
        if (report.getReportDate() == null) {
            return ErrorKinds.BLANK_ERROR;
        }
        if (report.getTitle() == null || report.getTitle().isEmpty()) {
            return ErrorKinds.BLANK_ERROR;
        }
        if (report.getTitle().length() > 100) {
            return ErrorKinds.TITLE_LENGTH_ERROR;
        }
        if (report.getContent() == null || report.getContent().isEmpty()) {
            return ErrorKinds.BLANK_ERROR;
        }
        if (report.getContent().length() > 600) {
            return ErrorKinds.CONTENT_LENGTH_ERROR;
        }
        return ErrorKinds.SUCCESS;
    }

    // 日報新規登録時のエラーチェック
    public ErrorKinds checkReportExists(Employee employee, LocalDate reportDate) {
        Report existingReport = findByEmployeeAndReportDate(employee, reportDate);
        if (existingReport != null) {
            return ErrorKinds.DATECHECK_ERROR;
        }
        return ErrorKinds.SUCCESS;
    }

 // 日報更新時のエラーチェック
    public ErrorKinds checkReportExistsForUpdate(Report existingReport, LocalDate reportDate) {
        // 同じ従業員で、同じ日付の日報が存在するかチェック
        Report duplicateReport = findByEmployeeAndReportDate(existingReport.getEmployee(), reportDate);

        // duplicateReportが存在し、かつそのIDが既存の日報のIDと異なる場合、エラーを返す
        if (duplicateReport != null) {
            if (!duplicateReport.getId().equals(existingReport.getId())) {
                return ErrorKinds.DATECHECK_ERROR; // 既存の日報データが存在する場合エラー
            }
        }

        return ErrorKinds.SUCCESS; // エラーがなければ成功を返す
    }

}
