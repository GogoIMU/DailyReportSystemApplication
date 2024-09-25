package com.techacademy.service;

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

    // 1件を検索
    public Report findByCode(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

        // 日報日付チェック
        if (report.getReportDate() == null) {
            return ErrorKinds.BLANK_ERROR;
        }

        // タイトルチェック
        if (report.getTitle() == null || report.getTitle().isEmpty()) {
            return ErrorKinds.BLANK_ERROR;
        }

        // タイトル桁数チェック
        if (report.getTitle().length() > 99) {
            return ErrorKinds.RANGECHECK_ERROR;
        }


        // 内容チェック
        if (report.getContent() == null || report.getContent().isEmpty()) {
            return ErrorKinds.BLANK_ERROR;
        }

        // 内容桁数チェック
        if (report.getContent().length() > 599) {
            return ErrorKinds.RANGECHECK_ERROR;
        }

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

}