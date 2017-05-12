package com.dolphinblue.models;

import java.util.List;

/**
 * Created by Devon on 5/12/17.
 */
public class SearchObject {
    String searchTerm; // keywords
    String filter; // all, main, shared, your lessons
    String sortBy; // title or author or none
    Boolean asc; // ascending/ descending
    List<String> searchBy; // title, description, author, etc

    public SearchObject() {

    }

    public SearchObject(String searchTerm, String filter, String sortBy, Boolean asc, List<String> searchBy) {
        this.searchTerm = searchTerm;
        this.filter = filter;
        this.sortBy = sortBy;
        this.asc = asc;
        this.searchBy = searchBy;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    public List<String> getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(List<String> searchBy) {
        this.searchBy = searchBy;
    }
}
