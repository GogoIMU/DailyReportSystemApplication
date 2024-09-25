package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
public class Report {

    // 主キー。自動生成
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 日報日付。null不許可
    @NotNull
    @Column(name = "report_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;


    // タイトル。null不許可
    @Column(nullable = false, length = 100)
    private String title;

    // 内容。null不許可
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    // 社員番号。null不許可。Employeeとのリレーション
    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;

    // 削除フラグ。null不許可
    @Column(name = "delete_flg", nullable = false)
    private Boolean deleteFlg;

    // 登録日時。null不許可。updateも不許可。
    @Column(name = "created_at", nullable = false, updatable =false)
    private LocalDateTime createdAt;

    // 更新日時。null不許可
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}