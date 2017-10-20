package com.company.sts_ar.vo;

import java.io.Serializable;

/**
 * Created by thong.le on 10/17/2017.
 */

public class Project implements Serializable {
    public String name;
    public String cover;
    public String description;
    public String folder;
    public int size;

    public static class Component {
        public String image;
        public String obj;
        public String mtl;

        public Component() {
        }

        public Component(String image, String obj, String mtl) {
            this.image = image;
            this.obj = obj;
            this.mtl = mtl;
        }
    }
}
