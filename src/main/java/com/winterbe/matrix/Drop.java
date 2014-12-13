package com.winterbe.matrix;

import java.util.Date;

/**
 * @author Benjamin Winterberg
 */
public class Drop implements Comparable<Drop> {
    private long id;
    private String user;
    private String repository;
    private String url;
    private String code;
    private Date timestamp;
    private String fileSha;

    public String getKey() {
        return id + "_" + fileSha;
    }

    public String getFileSha() {
        return fileSha;
    }

    public void setFileSha(String fileSha) {
        this.fileSha = fileSha;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int compareTo(Drop other) {
        if (other == null) {
            return -1;
        }
        if (timestamp == null) {
            return 1;
        }
        return timestamp.compareTo(other.timestamp);
    }
}
