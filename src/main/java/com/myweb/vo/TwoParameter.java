package com.myweb.vo;

import java.math.BigDecimal;

public class TwoParameter
{

    private Integer id;

    private Integer userid;

    private Integer ownerid;

    private Integer bookstoreid;

    private Integer bookid;

    private Integer status;

    private String letter;

    private BigDecimal fee;

    private Integer page;

    public BigDecimal getFee()
    {
        return fee;
    }

    public void setFee(BigDecimal fee)
    {
        this.fee = fee;
    }

    public Integer getBookstoreid()
    {
        return bookstoreid;
    }

    public void setBookstoreid(Integer bookstoreid)
    {
        this.bookstoreid = bookstoreid;
    }

    public String getLetter()
    {
        return letter;
    }

    public void setLetter(String letter)
    {
        this.letter = letter;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getUserid()
    {
        return userid;
    }

    public void setUserid(Integer userid)
    {
        this.userid = userid;
    }

    public Integer getOwnerid()
    {
        return ownerid;
    }

    public void setOwnerid(Integer ownerid)
    {
        this.ownerid = ownerid;
    }

    public Integer getBookid()
    {
        return bookid;
    }

    public void setBookid(Integer bookid)
    {
        this.bookid = bookid;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getPage()
    {
        return page;
    }

    public void setPage(Integer page)
    {
        this.page = page;
    }
}
