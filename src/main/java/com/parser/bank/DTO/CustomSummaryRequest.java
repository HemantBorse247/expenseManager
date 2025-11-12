package com.parser.bank.DTO;

import java.sql.Date;

public class CustomSummaryRequest {
    private Date startdate;
    private Date enddate;

    // getters & setters
    public Date getStartdate() {
        return startdate;
    }
    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }
    public Date getEnddate() {
        return enddate;
    }
    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }
}
