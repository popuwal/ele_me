package com.example.ele_me.entity;

import java.io.Serializable;
import java.util.List;

public class TestVolleyJson implements Serializable {
    public List<Data> data;//需要使用data

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public TestVolleyJson.links getLinks() {
        return links;
    }

    public void setLinks(TestVolleyJson.links links) {
        this.links = links;
    }

    public TestVolleyJson.meta getMeta() {
        return meta;
    }

    public void setMeta(TestVolleyJson.meta meta) {
        this.meta = meta;
    }

    public links links;
    public meta meta;

    public  class Data implements Serializable{

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIs_read() {
            return is_read;
        }

        public void setIs_read(int is_read) {
            this.is_read = is_read;
        }

        public String getImage_link() {
            return image_link;
        }

        public void setImage_link(String image_link) {
            this.image_link = image_link;
        }

        int id;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        String from;
        String to;

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        String user_name;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        String message;
        int is_read;
        String image_link;

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        String created_at;

        @Override
        public String toString() {
            return "pic: "+getImage_link()+" from: "+getFrom()+" name: "+getUser_name()+" msg: "+getMessage()+" date: "+getCreated_at()+"\n";
        }
    }

    public  class links {
        String first;

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getPrev() {
            return prev;
        }

        public void setPrev(String prev) {
            this.prev = prev;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        String last;
        String prev;
        String next;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        @Override
        public String toString() {
            return "first: "+getFirst()+" next: "+getNext();
        }
    }

    public  class meta {
        int current_page;

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getLast_page() {
            return last_page;
        }

        public void setLast_page(int last_page) {
            this.last_page = last_page;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        int from;
        int last_page;
        String path;
        int per_page;
        int to;
        int total;

    }
}
