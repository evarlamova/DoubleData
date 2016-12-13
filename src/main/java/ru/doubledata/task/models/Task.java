package ru.doubledata.task.models;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

public class Task implements Runnable {

    private volatile boolean isCancelled;
    private String taskUuid;
    private final String fileName;
    private String objectName;
    private String algoType;
    private volatile Status status = Status.NEW;
    private boolean isLocal;
    private String result;
    private String userUuid;
    private String exceptionMessage;

    private volatile LocalDateTime begin;


    public Task(String fileName, String objectName, String algoType, boolean isLocal, String userUuid) {
        this.taskUuid = UUID.randomUUID().toString();
        this.fileName = fileName;
        this.objectName = objectName;
        this.algoType = algoType;
        this.isLocal = isLocal;
        this.userUuid = userUuid;
        this.begin = LocalDateTime.now();
    }

    @Override
    public void run() {
        try {
            status = Status.RUNNING;
            AlgoType algoTypeEnum = Arrays.asList(AlgoType.values()).stream()
                    .filter(t -> t.getAlgoString().equals(algoType))
                    .findFirst().get();

            MessageDigest md = MessageDigest.getInstance(algoTypeEnum.getAlgoString());
            try (InputStream inputStream =
                         isLocal ? new BufferedInputStream(new DigestInputStream(
                                 new FileInputStream(new File(fileName)), md), 1024 * 1024)
                                 : new URL(objectName).openStream()) {
                int countBytes = 0;
                while (inputStream.read() != -1) {
                    if ((countBytes % 1000 == 0) && isCancelled) {
                        return;
                    }
                    countBytes++;
                }
                result = DatatypeConverter.printHexBinary(md.digest());
                status = Status.COMPLETED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = Status.EXCEPTIONALLY;
            setExceptionMessage(e.getMessage());
        } finally {
            new File(fileName).delete();
        }

    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum AlgoType {
        MD5("MD5"), SHA_1("SHA-1"), SHA_256("SHA-256"),;
        private String algoString;

        AlgoType(String algoString) {
            this.algoString = algoString;
        }

        public String getAlgoString() {
            return algoString;
        }
    }

    public enum Status {
        NEW("NEW"), RUNNING("RUNNING"), EXCEPTIONALLY("EXCEPTIONALLY"), COMPLETED("COMPLETED");

        private String statusMessage;

        Status(String statusMessage) {
            this.statusMessage = statusMessage;
        }

        public String getStatusMessage() {
            return statusMessage;
        }
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public Status getStatus() {
        return status;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getResult() {
        return result;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getAlgoType() {
        return algoType;
    }
}
