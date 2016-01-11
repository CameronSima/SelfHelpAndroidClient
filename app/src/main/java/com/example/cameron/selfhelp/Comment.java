package com.example.cameron.selfhelp;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

/**
 * Created by cameron on 1/6/16.
 */
public abstract class Comment implements org.w3c.dom.Comment{

    public static JSONObject comment;
    public String[] path;
    public int depth;
    public int childCount;

    public void parseJSON(JSONObject comment) {
        try {
            String jpath = comment.getString("path");
            this.path = jpath.split("#");
            this.depth = path.length;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getComment_parent() {
        int len = getCommentDepth();
        if (len > 0) {
            return path[1];
        } else {
            return "0";
        }
    }

    public int getCommentDepth() {
        return depth;
    }

    public void setCommentDepth(int depth) {
        this.depth = depth;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }
    public String getComment_id() {
        return path[0];
    }

    public int getChildCount() {
        return childCount;
    }
}
