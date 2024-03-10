package com.genealogy.vo;

import java.util.LinkedList;
import java.util.List;

//权限一张图的资源类
public class UserPermissionSummaryResource {

    private List<AlbumPermissionResource> aprs;

    private List<String> libraryPermissions;
    private String libraryPermissionScope;

    private Boolean isAdmin;


    public List<AlbumPermissionResource> getAprs() {
        return aprs;
    }

    public void setAprs(List<AlbumPermissionResource> aprs) {
        this.aprs = aprs;
    }

    public List<String> getLibraryPermissions() {
        return libraryPermissions;
    }

    public void setLibraryPermissions(List<String> libraryPermissions) {
        this.libraryPermissions = libraryPermissions;
    }

    public UserPermissionSummaryResource() {
        this.aprs = new LinkedList<AlbumPermissionResource>();
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getLibraryPermissionScope() {
        return libraryPermissionScope;
    }

    public void setLibraryPermissionScope(String libraryPermissionScope) {
        this.libraryPermissionScope = libraryPermissionScope;
    }
}
