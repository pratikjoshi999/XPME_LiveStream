package com.muvi.www.xmpe.content;

/**
 * Created by MUVI on 5/19/2017.
 */

public class ContentListModel {

    String movie_stream_uniq_id;
    String movie_id;
    String feed_url;
    String permalink;
    String name;
    String poster_url;
    String is_online;
    String muvi_uniq_id;
    String content_types_id;

    public ContentListModel(String movie_stream_uniq_id, String movie_id, String feed_url, String permalink, String name, String poster_url, String is_online, String muvi_uniq_id, String content_types_id) {
        this.movie_stream_uniq_id = movie_stream_uniq_id;
        this.movie_id = movie_id;
        this.feed_url = feed_url;
        this.permalink = permalink;
        this.name = name;
        this.poster_url = poster_url;
        this.is_online = is_online;
        this.muvi_uniq_id=muvi_uniq_id;
        this.content_types_id=content_types_id;
    }

    public String getMovie_stream_uniq_id() {
        return movie_stream_uniq_id;
    }

    public void setMovie_stream_uniq_id(String movie_stream_uniq_id) {
        this.movie_stream_uniq_id = movie_stream_uniq_id;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getFeed_url() {
        return feed_url;
    }

    public void setFeed_url(String feed_url) {
        this.feed_url = feed_url;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String getIs_online() {
        return is_online;
    }

    public void setIs_online(String is_online) {
        this.is_online = is_online;
    }

    public String getMuvi_uniq_id() {
        return muvi_uniq_id;
    }

    public void setMuvi_uniq_id(String muvi_uniq_id) {
        this.muvi_uniq_id = muvi_uniq_id;
    }

    public String getContent_types_id() {
        return content_types_id;
    }

    public void setContent_types_id(String content_types_id) {
        this.content_types_id = content_types_id;
    }

}

