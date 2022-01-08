package com.mkweb.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.mkweb.config.MkSqlConfig;
import com.mkweb.config.MkViewConfig;

import com.mkweb.config.MkConfigReader;
import com.mkweb.config.MkFTPConfigs;
import com.mkweb.data.MkFileServerAttributes;
import com.mkweb.data.MkFtpData;
import com.mkweb.data.MkPageJsonData;
import com.mkweb.data.MkSqlJsonData;
import com.mkweb.database.MkDbAccessor;
import com.mkweb.logger.MkLogger;
import com.mkweb.utils.ConnectionChecker;
import com.mkweb.utils.crypto.MkCrypto;

@WebServlet(name = "MkFTPServlet", loadOnStartup = 1)
@MultipartConfig
public class MkFileReceiver extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String TAG = "[MkFileReceiver]";
    private static final MkLogger mklogger = new MkLogger(TAG);

    private MkPageJsonData pjData = null;
    private ArrayList<MkPageJsonData> pi = null;
    private boolean isPiSet = false;
    MkPageJsonData pageStaticData = null;
    private static final String HASH_PREFIX = MkConfigReader.Me().get("mkweb.ftp.hash.prefix");
    private static MkFileServerAttributes ftpAttributes = MkFTPConfigs.Me().getAttributes();

    private String requestParameterName = null;
    private ArrayList<String> requestValues = null;
    private String method = null;

    private List<Part> fileParts = null;

    private ArrayList<MkPageJsonData> getPageControl(String url) {
        String mkPage = null;
        String hostcheck = url.split("://")[1];
        String host = MkConfigReader.Me().get("mkweb.web.hostname") + "/";
        if (hostcheck.contentEquals(host)) {
            mkPage = "";
        } else {
            try {
                mkPage = "/" + hostcheck.split(host)[1];
            } catch (IndexOutOfBoundsException e) {
                mklogger.error("Unknown server is trying to send data: " + hostcheck);
                return null;
            }

        }
        mklogger.debug("mkpage :" + mkPage);
        return MkViewConfig.Me().getNormalControl(mkPage);
    }

    private boolean checkMethod(HttpServletRequest request, String rqMethod, String rqPageURL) {
        String hostCheck = rqPageURL.split("://")[1];
        String host = MkConfigReader.Me().get("mkweb.web.hostname");

        mklogger.debug("method : " + rqMethod);
        if (host == null) {
            mklogger.error(" Hostname is not set. You must set hostname on configs/MkWeb.conf");
            return false;
        }
        host = host + "/";
        String requestURI = rqPageURL.split(MkConfigReader.Me().get("mkweb.web.hostname"))[1];
        String mkPage = !hostCheck.contentEquals(host) ? requestURI : "";
        if (!ConnectionChecker.isValidPageConnection(mkPage)) {
            mklogger.error(" checkMethod: Invalid Page Connection.");
            return false;
        }
        if (this.pi == null || !this.isPiSet) {
            mklogger.error(" PageInfo is not set!");
            return false;
        }

        ArrayList<MkPageJsonData> pal = MkViewConfig.Me().getNormalControl(mkPage);
        for (MkPageJsonData pj : pal) {
            if (pj.getParameter().equals(this.requestParameterName)) {
                mklogger.debug("requestParameterName : " + requestParameterName);
                this.pjData = pj;
                break;
            }
        }

        try {
            if (!this.pjData.getMethod().toLowerCase().contentEquals(rqMethod)) {
                mklogger.error("data service : " + pjData.getServiceName());
                mklogger.error("no method matched | page : " + pjData.getMethod() + ", requested : " + rqMethod);
                return false;
            }

        } catch (NullPointerException e) {
            mklogger.error("There is no service for request parameter. You can ignore 'Request method is not authorized.' error.");
            mklogger.debug("Page Json Data is Null");
            return false;
        }

        return (this.pjData != null);
    }

    private void doTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String control = this.pjData.getControlName();
        String service = this.pjData.getServiceName();

        boolean isDone = false;
        mklogger.debug("controller: " + control + ", service : " + service);
        ArrayList<MkFtpData> ftpControl = MkFTPConfigs.Me().getControlByServiceName(service);

        MkFtpData ftpService = null;
        for (MkFtpData mfd : ftpControl) {
            if (mfd.getServiceName().contentEquals(service)) {
                ftpService = mfd;
                break;
            }
        }

        String result = "";
        StringBuilder responseMsg = new StringBuilder();
        StringBuilder uploaded = new StringBuilder();
        int responseCode = -1;

        if (ftpService == null) {
            responseMsg = new StringBuilder("No further information.");
            responseCode = 400;
            mklogger.error("There is no FTP service named : " + service);
        }

        if(!ConnectionChecker.isFtpAuthorized(request, ftpService)){
            if(MkConfigReader.Me().get("mkweb.auth.redirect.use").contentEquals("yes")){
                mklogger.debug("Fail to authorize redirect to login page");
                response.sendRedirect(MkConfigReader.Me().get("mkweb.auth.redirect.uri"));
            } else {
                response.sendError(401);
            }
            return;
        }

        if (!ConnectionChecker.compareFtpPageValueWithRequestValue(pjData.getPageValue(), requestValues, pageStaticData, ftpService, false)) {
            mklogger.error(" Request Value is not authorized. Please check page config.");
            response.sendError(400);
            return;
        }

        //해당 설정하면 됨
        ArrayList<Long> executedSequences = new ArrayList<>();
        ArrayList<String> executedDirectories = new ArrayList<>();
        ArrayList<MkSqlJsonData> sqlControl = MkSqlConfig.Me().getControl(ftpAttributes.getSqlControlName(), false);
/*
        String befQuery = ConnectionChecker.regularQuery(sqlService.getControlName(), service, false);
        String query = ConnectionChecker.setQuery(befQuery);
        mklogger.debug("set query: " + query);
        if(isRequestValid(requestValues, pjData.getPageValue())) {//requestValues != null) {
            mklogger.debug("passed");
            requestValues = (ArrayList<String>) ConnectionChecker.setRequestValueSequences(requestValues, pjData.getPageValue());
            String[] reqs = new String[requestValues.size()];
            String tempValue = "";

            MkDbAccessor DA = new MkDbAccessor();
            DA.setPreparedStatement(query);
        }
*/
        while (true) {
            String[] allowFormats = ftpService.getData();
            /* 파일이 여러개 올라가는 문제 */
            /* duplicate code */
            String filePath = ftpService.getPath();
            String ftpDirPrefix = ftpService.getDirPrefix();
            String[] dirs = null;
            boolean ftpDirHash = ftpService.getHashDirPrefix();
            if (ftpDirPrefix != null) {
                ftpDirPrefix = request.getParameter((pjData.getParameter() + "." + ftpDirPrefix));
                if (ftpDirPrefix == null) {
                    mklogger.error("User didn't send dir prefix.");
                    responseCode = 400;
                    responseMsg = new StringBuilder("Need to send path.");
                    break;
                }

                if (ftpDirPrefix.contains("^")) {
                    dirs = ftpDirPrefix.split("\\^");
                    StringBuilder tempDir = new StringBuilder();
                    for (String dir : dirs) {
                        if (ftpDirHash)
                            tempDir.append("/").append(MkCrypto.MD5(dir + HASH_PREFIX));
                        else
                            tempDir.append("/").append(dir);
                    }

                    ftpDirPrefix = tempDir.toString();
                } else {
                    if (ftpDirHash)
                        ftpDirPrefix = "/" + MkCrypto.MD5(ftpDirPrefix + HASH_PREFIX);
                    else
                        ftpDirPrefix = "/" + ftpDirPrefix;
                }
            } else {
                ftpDirPrefix = "";
            }
            filePath = MkFTPConfigs.Me().getPrefix() +
                                (ftpDirPrefix.isEmpty()
                                        ? filePath + "/" + System.currentTimeMillis()
                                        : filePath + ftpDirPrefix);
            /* Receive Mode */
            if (responseCode == -1)
                responseCode = 201;
            int size = fileParts.size();
            ArrayList<InputStream> fileContents = new ArrayList<>();
            String[] fileNames = new String[size];
            int currentIndex = 0;
            for (Part filePart : fileParts) {
                String partName = filePart.getName();
                if (partName != null && !partName.contentEquals("")) {
                    mklogger.debug("partName : " + partName + " | ftpDirPrefix: " + ftpDirPrefix + "!" + " is empty : " + ftpDirPrefix.isEmpty());
                    if (partName.contains(".") && !ftpDirPrefix.isEmpty()) {
                        if (partName.split("\\.")[1].contains(ftpDirPrefix)) {
                            continue;
                        }
                    }
                }

                fileNames[currentIndex] = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
                fileContents.add(filePart.getInputStream());

                if (fileNames[currentIndex].contains(".")) {
                    String[] periods = fileNames[currentIndex].split("\\.");
                    String extension = periods[periods.length - 1];

                    boolean passed = false;
                    for (String format : allowFormats) {
                        if (extension.contentEquals(format)) {
                            passed = true;
                        }
                    }
                    if (!passed) {
                        mklogger.error("The received file have format which is not supported.");
                        return;
                    }
                }
                currentIndex++;
            }

            String folderName = null;
            if (!ftpDirPrefix.contentEquals("") || ftpDirPrefix != null) {
                File folder = new File(filePath);
                folderName = folder.getPath();
                boolean isDirExists = folder.exists();
                if (!isDirExists) {
                    mklogger.info("The directory is not exists. Creating new one... " + folder.getPath());
                    try {
                        isDirExists = folder.mkdirs();
                        folder.setReadable(true, false);
                        folder.setExecutable(true, false);
                        if (!isDirExists) {
                            responseCode = 500;
                            responseMsg = new StringBuilder("Server have some problems! Please contact Admin.");
                            mklogger.error("Failed to create path. [" + filePath + "]");
                            break;
                        }
                    } catch (Exception e) {
                        mklogger.error("Failed to create path. [" + filePath + "] " + e.getMessage());
                        responseCode = 500;
                        responseMsg = new StringBuilder("Server have some problems! Please contact Admin.");
                        break;
                    }
                }
            }
            currentIndex = 0;
            for (InputStream fileContent : fileContents) {
                MkDbAccessor DA = new MkDbAccessor();
                String[] periods = fileNames[currentIndex].split("\\.");
                String extension = periods[periods.length - 1];

                String befQuery = ConnectionChecker.regularQuery(ftpAttributes.getSqlControlName(), sqlControl.get(1).getServiceName(), false);
                String query = ConnectionChecker.setQuery(befQuery);
                mklogger.debug("set query: " + query);
                DA.setPreparedStatement(query);
                String[] reqValues = {ftpService.getControlName(), ftpService.getServiceName(), filePath, extension};
                DA.setRequestValue(reqValues);
                long currentSequence = -1;
                try{
                    currentSequence = DA.executeDML();
                    executedSequences.add(currentSequence);
                } catch (SQLException e){
                    mklogger.error("Failed to upload file. Maybe something wrong in database" + e.getMessage());
                    responseCode = 500;
                    responseMsg = new StringBuilder("Fail to fetch Database.");

                    continue;
                }

                File currentFile = new File(filePath + "/" + MkCrypto.MD5(fileNames[currentIndex++]) + "." + extension);
                try {
                    currentFile.createNewFile();
                } catch (IOException e) {
                    mklogger.error("Failed to upload file. Maybe there is no target directory." + e.getMessage());
                    responseCode = 500;
                    responseMsg = new StringBuilder("Server have some problems! Please contact Admin.");

                    continue;
                }

                currentFile.setReadable(true, false);
                currentFile.setExecutable(true, false);
                try (FileOutputStream outputStream = new FileOutputStream(currentFile)) {
                    int read;
                    byte[] bytes = new byte[1024];

                    while ((read = fileContent.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }

                    String responsePrefix = ftpService.getPath() + "/" + currentFile.getPath().split(ftpService.getPath())[1];
                    responsePrefix = responsePrefix.replaceAll("\\/\\/", "/").split("\\.")[0];
                    uploaded.append("{\"prefix\":\"");
                    uploaded.append(responsePrefix).append("\",");
                    uploaded.append("\"").append(ftpAttributes.get("seq")).append("\":").append(currentSequence).append("}");
                    responseMsg.append("[Success to upload : ").append(responsePrefix).append("]");
                    executedDirectories.add(responsePrefix);
                    /*
                    SQL에 정보 등록하기
                     */
                    if (currentIndex < fileNames.length) {
                        uploaded.append(",");
                        responseMsg.append(",");
                    }
                } catch (Exception e) {
                    if (!currentFile.getAbsolutePath().contentEquals(folderName)) {
                        mklogger.temp("There was something wrong to create file : " + currentFile.getAbsolutePath() + "/ file name : " + currentFile.getName(), false);
                        mklogger.temp(e.getMessage(), false);
                        mklogger.flush("error");

                        responseCode = 401;
                        responseMsg.append("[Fail to upload : ").append(currentFile.getAbsolutePath()).append("],");
                    }
                }
            }

            break;
        }

        boolean success = updateQuery(executedDirectories, executedSequences, sqlControl, responseCode == 201);

        mklogger.debug("So did I success? : " + success);

        response.setStatus(responseCode);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        result = "{\"code\":\"201\",\"response\":\"" + responseMsg + "\",\"executed\":[" + uploaded + "]}";
        out.print(result);
        out.flush();
        out.close();
    }

    private boolean updateQuery(ArrayList<String> executedDirectories, ArrayList<Long> executedSequences, ArrayList<MkSqlJsonData> sqlControl, boolean success){
        String auQuery = (success) ? ConnectionChecker.regularQuery(ftpAttributes.getSqlControlName(), sqlControl.get(2).getServiceName(), false)
                : ConnectionChecker.regularQuery(ftpAttributes.getSqlControlName(), sqlControl.get(3).getServiceName(), false);
        if(auQuery == null) {
            mklogger.error("(func updateQuery) Fail to set regular Query: auQuery is null. There is no sql control matches control name or service name.");
            mklogger.error(String.format("(func updateQuery) Control: %s, Service: %s", ftpAttributes.getSqlControlName(), (success ? sqlControl.get(2).getServiceName() : sqlControl.get(3).getServiceName())));

            return false;
        }
        String query = ConnectionChecker.setQuery(auQuery);
        MkDbAccessor DA = new MkDbAccessor();
        DA.setPreparedStatement(query);
        mklogger.debug("set query: " + query);
        if(success){
            for(int i = 0; i < executedDirectories.size(); i++){
                long seq = executedSequences.get(i);
                String dir = executedDirectories.get(i);
                DA.setRequestValue(new String[]{ dir, "0", String.valueOf(seq)});
                try{
                    DA.executeDML();
                } catch (SQLException e){
                    mklogger.error("(func updateQuery) Fail to update database: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }
        }
        else {
            for (Long executedSequence : executedSequences) {
                DA.setRequestValue(new String[]{ String.valueOf(executedSequence) });
                try{
                    DA.executeDML();
                } catch (SQLException e){}
            }
        }
        return true;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String refURL = request.getHeader("Referer");
        pi = getPageControl(refURL);
        isPiSet = (this.pi != null);
        pageStaticData = null;
        if (this.isPiSet) {
            int i;
            for (i = 0; i < this.pi.size(); i++) {
                if (((MkPageJsonData) this.pi.get(i)).getPageStatic()) {
                    pageStaticData = this.pi.get(i);
                    break;
                }
            }
        }

        if (!prepareToReceiveFiles(request)) {
            mklogger.error("Failed to prepare Receive files");
            response.sendError(400);
            return;
        }

        if (!checkMethod(request, "get", refURL)) {
            mklogger.error(" Request method is not authorized. [Tried: GET]");
            response.sendError(405);
            return;
        }

        doTask(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String refURL = request.getHeader("Referer");

        pi = getPageControl(refURL);
        isPiSet = (pi != null);
        pageStaticData = null;
        if (isPiSet) {
            int i;
            for (i = 0; i < this.pi.size(); i++) {
                if (pi.get(i).getPageStatic()) {
                    pageStaticData = pi.get(i);
                    break;
                }
            }
        }

        if (!prepareToReceiveFiles(request)) {
            mklogger.error("Failed to prepare Receive files");
            return;
        }

        if (!checkMethod(request, "post", refURL)) {
            mklogger.error(" Request method is not authorized. [Tried: POST]");
            response.sendError(405);
            return;
        }

        method = "post";
        doTask(request, response);
    }

    private String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE fix.
            }
        }
        return null;
    }


    private String getSubmittedParameterName(String datas) {
        mklogger.debug("datas : " + datas);
        try{
            return datas.substring(0, datas.indexOf('.'));
        } catch (java.lang.StringIndexOutOfBoundsException e){
            mklogger.error("(func getSubmittedParameterName) MkWeb cannot translate your request: " + datas);
            return null;
        }

    }

    private String[] getSubmittedParameters(String formData) {
        String[] splitDatas = formData.split("Content-Disposition");
        String[] result = new String[splitDatas.length - 1];
        if (splitDatas.length < 1) {
            return null;
        }

        for (int i = 1; i < splitDatas.length; i++) {
            String fd = splitDatas[i].split("Content-Type")[0];
            result[i - 1] = getSubmittedParameterName(fd);
            mklogger.debug("result i : " + result[i - 1]);
        }
        return result;
    }

    private static String getSubmittedParameters(Part part) {
        for (String pd : part.getHeader("content-disposition").split(";")) {
            if (pd.trim().startsWith("name")) {
                String parameterName = pd.substring(pd.indexOf('=') + 1).trim().replace("\"", "");
                return parameterName.substring(parameterName.lastIndexOf('/') + 1).substring(parameterName.lastIndexOf('\\') + 1); // MSIE fix.
            }
        }
        return null;
    }

    private boolean prepareToReceiveFiles(HttpServletRequest request) throws IOException, ServletException {
        fileParts = (List<Part>) request.getParts();

        HashMap<String, ArrayList<String>> requestPartsParameters = new HashMap<>();
        ArrayList<Part> dumpList = new ArrayList<>();
        for (Part part : fileParts) {
            String partParameter = getSubmittedParameters(part);
            String partParameterName = getSubmittedParameterName(partParameter);
            if(partParameterName == null){
                mklogger.error("(func prepareToReceiveFiles) The request is not valid. You need to use defined parameter.");
                return false;
            }
            mklogger.debug("is part clear:" + part.getSize());

            if(part.getSize() == 0){
                dumpList.add(part);
                continue;
            }
            ArrayList<String> temp = null;

            temp = (requestPartsParameters.get(partParameterName) == null) ? new ArrayList<>() : requestPartsParameters.get(partParameterName);

            temp.add(partParameter.split("\\.")[1]);
            requestPartsParameters.put(partParameterName, temp);
        }

        fileParts.removeAll(dumpList);

        if (requestPartsParameters.size() != 1) {
            return false;
        }

        if (fileParts.size() < 1){
            mklogger.error("(func prepareToReceiveFiles) There is no file to upload.");
            return false;
        }

        requestParameterName = requestPartsParameters.keySet().iterator().next();
        requestValues = requestPartsParameters.get(requestParameterName);
        return true;
    }
}