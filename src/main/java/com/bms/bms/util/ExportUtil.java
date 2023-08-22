package com.bms.bms.util;

import com.poiji.bind.Poiji;
import com.xpresspayments.phedMiddlewareAdminPortal.email.service.MailService;
import com.xpresspayments.phedMiddlewareAdminPortal.exception.GeneralException;
import com.xpresspayments.phedMiddlewareAdminPortal.exception.ReportGenerationException;
import com.xpresspayments.phedMiddlewareAdminPortal.general.enums.ResponseCodeAndMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Slf4j
@Service
public class ExportUtil {

    public static final String EXPORTS = "exports";
    public static final String UPLOADS = "uploads";

    private final MailService mailService;

    @Value("${phedMiddlewarePortal.superAdminEmails}")
    private String adminMails;

    public ExportUtil(MailService mailService) {
        this.mailService = mailService;
    }

    private FileManagementUtil createDirectory(String name) {
        FileManagementUtil fileManagementUtil = new FileManagementUtil(name);
        fileManagementUtil.createDirectory();
        return fileManagementUtil;
    }

    public <K> List<K> convertExcelToDTO(MultipartFile multipartFile, Class<K> type) {
        log.info("converting excel to DTO for {}...", type.getName());

        String fileName = multipartFile.getOriginalFilename();

        FileManagementUtil fileManagementUtil = createDirectory(UPLOADS);

        File file = fileManagementUtil.transferUploaded(multipartFile);
        if (Objects.isNull(file)) {
            throw new GeneralException(ResponseCodeAndMessage.AN_ERROR_OCCURRED_96.responseCode,
                    "Error occurred while parsing document");
        }
        log.info("successfully copied file to disk...");

        List<K> excelDTO;
        try {
            excelDTO = Poiji.fromExcel(file, type);
            log.info("Retrieved excel => {} and docs => {}", fileName, excelDTO);
        } catch (Exception e) {
            throw new GeneralException(ResponseCodeAndMessage.AN_ERROR_OCCURRED_96.responseCode,
                    "Error occurred while parsing excel");
        }

        boolean result = file.delete();
        String message = result ? "successfully deleted file" : "file deletion failed";
        log.info(message);

        return excelDTO;
    }

    public void sendReportToEmail(Class<?> reportClass, List<?> reportList, String reportName, String email) {
        log.info("Initiating {} download for  user => {}", reportName, email);

        if (reportList.isEmpty()) {
            throw new ReportGenerationException(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode,
                    "Nothing to export for selection");
        }

        boolean result = forSendingReportMail(reportClass, reportList, reportName, email);

        if (result) {
            throw new ReportGenerationException(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode,
                    "Report sent to email " + email + " successfully");
        } else {
            throw new ReportGenerationException(ResponseCodeAndMessage.AN_ERROR_OCCURRED_96.responseCode,
                    "Report was not successfully sent");
        }
    }

    private boolean forSendingReportMail(Class<?> reportClass, List<?> reportList, String reportName, String email) {
        String sheetName = getSheetName(reportName);
        String mailSubject = getMailSubject(reportName);

        String reportPath = generateReportPath(reportClass, reportList, sheetName, email);

        if (Objects.nonNull(reportPath)) {
            String[] copy = adminMails.split(",");

            Map<String, Object> params = new HashMap<>();

            params.put("reportName", reportName);

            try {
                mailService.sendMailWithAttachment(mailSubject, email, copy, params, "report_template", reportPath);
            } catch (Exception e) {
                log.error("Error sending mail with attachment", e);
                throw new GeneralException(ResponseCodeAndMessage.AN_ERROR_OCCURRED_96);
            }

            return true;
        }

        return false;
    }

    private String generateReportPath(Class<?> aClass, List<?> reportList, String sheetName, String email) {
        ExcelUtil excelUtil = new ExcelUtil(aClass);

        String filePath = excelUtil.getFileName(email);

        try {
            //create file directory
            createDirectory(EXPORTS);

            String excelPath = excelUtil.writeExcel(reportList, sheetName, EXPORTS + File.separator + filePath);

            //send mail
            if (!excelPath.equals("failed")) {
                log.info("Generated excel sheet at {}", excelPath);
                return excelPath;
            } else {
                log.info("excel creation failed");
            }
        } catch (Exception e) {
            log.info("Error while creating Excel {}", e.getMessage());
        }
        return null;
    }

//    private void sendEODMail(String email, String institutionName, String fileName, String mailSubject, String reportDetails) {
//        Map<String, Object> params = new HashMap<>();
//
//        params.put("reportName", reportDetails);
//        params.put("institutionName", institutionName);
//        mailService.sendMailWithAttachment(mailSubject, email, null, params, "wallet_report_eod_template", null, fileName);
//    }

    private void deleteAllFilesInFolders(String... folderNames) {
        for (String folderName : folderNames) {
            FileManagementUtil fileManagementUtil = new FileManagementUtil(folderName);
            fileManagementUtil.deleteAllFileInFolder();
        }
    }

    private String getMailSubject(String reportName) {
        String trimmedName = reportName.trim();
        trimmedName = trimmedName.endsWith("Report") ? trimmedName : trimmedName + " Report";
        return "XBS " + trimmedName;
    }

    private String getSheetName(String reportName) {
        return reportName.trim().replace(" ", "_").toLowerCase(Locale.ROOT);
    }
}
