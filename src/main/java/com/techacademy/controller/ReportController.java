package com.techacademy.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {
        model.addAttribute("reportList", reportService.findAll());
        model.addAttribute("listSize", reportService.findAll().size());
        return "reports/list";
    }

 // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        Report report = reportService.findById(id);
        model.addAttribute("report", report);
        Employee employee = report.getEmployee();
        model.addAttribute("employee", employee);
        return "reports/detail";
    }


    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        Employee employee = userDetail.getEmployee();
        model.addAttribute("employee", employee);
        model.addAttribute("report", report);

        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        if (res.hasErrors()) {
            return create(report, userDetail, model);
        }

        Employee employee = userDetail.getEmployee();
        report.setEmployee(employee);

        ErrorKinds result = reportService.save(report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, userDetail, model);
        }

        return "redirect:/reports";
    }

    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String update(@PathVariable Integer id, Model model) {
        Report existingReport = reportService.findById(id);

        if (existingReport == null || existingReport.getEmployee() == null) {
            return "redirect:/reports"; // エラーの場合はリダイレクトする
        }

        model.addAttribute("report", existingReport);
        model.addAttribute("employee", existingReport.getEmployee());

        return "reports/update"; // 正常に処理された場合はupdate画面を表示
    }

    // 日報更新処理
    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id, @Validated @ModelAttribute Report report, BindingResult error, Model model) {
        Report existingReport = reportService.findById(id); // IDで既存のレポートを取得

        if (existingReport == null) {
            model.addAttribute("error", "レポートが見つかりません。");
            return "redirect:/reports";
        }

        // 既存のレポートのフィールドを更新
        existingReport.setTitle(report.getTitle());
        existingReport.setContent(report.getContent());
        existingReport.setReportDate(report.getReportDate());

        // バリデーションエラーの確認
        if (error.hasErrors()) {
            model.addAttribute("report", existingReport);
            return "reports/update"; // エラーがある場合は再度update.htmlを表示
        }

        // 更新処理
        ErrorKinds result = reportService.update(existingReport.getEmployee().getId(), existingReport);
        if (result != ErrorKinds.SUCCESS) {
            model.addAttribute("error", ErrorMessage.getErrorValue(result));
            return "reports/update"; // エラーがある場合は再度update.htmlを表示
        }

        return "redirect:/reports"; // 更新成功時は一覧にリダイレクト
    }

}