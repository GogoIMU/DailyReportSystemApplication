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

        // 日付重複チェック

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
    public ErrorKinds update(String code, Report report) {

        // 更新日時を設定
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);

        // 従業員情報を保存
        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

}