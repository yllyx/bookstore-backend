package com.myweb.vo;

import java.math.BigDecimal;

public class OneParameter {
    private Integer id;
    private Integer userid;
    private Integer ownerid;
    private Integer bookid;
    private String bookids;
    private Integer bookstoreid;
    private Integer status;
    private BigDecimal deposit;
    private BigDecimal fee;
    private Integer days;
    private Integer weigth;
    private String isbn;
    private String username;
    private String password;
    private String openid;


    public String getBookids() {
        return bookids;
    }

    public void setBookids(String bookids) {
        this.bookids = bookids;
    }

    public Integer getBookstoreid() {
        return bookstoreid;
    }

    public void setBookstoreid(Integer bookstoreid) {
        this.bookstoreid = bookstoreid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getWeigth() {
        return weigth;
    }

    public void setWeigth(Integer weigth) {
        this.weigth = weigth;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(Integer ownerid) {
        this.ownerid = ownerid;
    }

    public Integer getBookid() {
        return bookid;
    }

    public void setBookid(Integer bookid) {
        this.bookid = bookid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
