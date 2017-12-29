package com.muvi.www.xmpe.categorylist;

import java.io.Serializable;

/**
 * Created by MUVI on 5/19/2017.
 */

public class Model implements Serializable {

   public String category_id;
    String category_name;
    String category_img_url;

    public String getCategoryPermalink() {
        return categoryPermalink;
    }

    public Model(String category_id, String category_name, String category_img_url, String categoryPermalink) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.category_img_url = category_img_url;
        this.categoryPermalink = categoryPermalink;
    }

    public void setCategoryPermalink(String categoryPermalink) {
        this.categoryPermalink = categoryPermalink;
    }

    String categoryPermalink;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_img_url() {
        return category_img_url;
    }

    public void setCategory_img_url(String category_img_url) {
        this.category_img_url = category_img_url;
    }
}
