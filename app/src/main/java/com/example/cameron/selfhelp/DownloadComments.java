package com.example.cameron.selfhelp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cameron on 1/6/16.
 */
public class DownloadComments extends AsyncTask<String, Void, List>{

    private Context context;

    JSONObject post;
    JSONArray comments;
    String url;
    List commentsList;
    List threadedComments;

    public ArrayAdapter<String> adapter;

    public DownloadComments(Context context) {
        this.context = context;
    }

    public void buildUrl(String threadId, String serverUrl) {
        url = "http://" + serverUrl + "/posts/" + threadId;
    }
    @Override
    protected void onPostExecute(List result) {
        System.out.println("RESULT");
        System.out.println(result);
        threadedComments = result;

        ListView listView = (ListView) ((Activity) context)
                .findViewById(R.id.comments);
        adapter = new ArrayAdapter<>(context,
                R.layout.commenttext, threadedComments);
        listView.setAdapter(adapter);

    }


    @Override
    protected List doInBackground(String... params) {
        ServerReq request = new ServerReq();
        try {
            post = request.readJsonFromUrl(url);
            comments = post.getJSONArray("comments");

            ArrayList<Object> commentsList = new ArrayList<>();
            if (comments != null) {
                for (int i = 0; i < comments.length(); i++) {
                    commentsList.add(comments.get(i));
                    System.out.println(comments.get(i));
                }
            }
            System.out.println("COMMENTS LIST");
            System.out.println(commentsList);
            return commentsList;
//
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toThreadedComments(commentsList);
    }

        public static List<Comment> toThreadedComments(List<JSONObject> comments){
        // TODO: sort comments by date
            System.out.println("COMMENTS");
            System.out.println(comments);

        //resulting array of threaded comments
        List<Comment> threaded = new ArrayList<>();

        //An array used to hold processed comments which should be removed at the end of the cycle
        List<Comment> removeComments = new ArrayList<>();

            Gson gson = new Gson();


        // First get the root comments
        for(int i = 0; i < comments.size(); i++) {
            Comment c = gson.fromJson(comments.get(i).toString(), Comment.class);
            if (c.getComment_parent().equals("0")) {
                c.setCommentDepth(0);
                c.setChildCount(0);
                threaded.add(c);
                removeComments.add(c);
            }
        }

        if (removeComments.size() > 0) {
            // clear processed comments
            comments.removeAll(removeComments);
            removeComments.clear();
        }

        int depth = 0;
        // get the child comments up to a max depth of 10
        while(comments.size() > 0 && depth <= 10) {
            depth++;
            for (int i = 0; i < comments.size(); i++) {
                Comment child = gson.fromJson(comments.get(i)
                                        .toString(), Comment.class);
                for (int j = 0; j < threaded.size(); j++) {
                    Comment parent = threaded.get(j);
                    if(parent.getComment_id().equals(child.getComment_parent())) {
                        parent.setChildCount(parent.getChildCount()+1);
                        child.setCommentDepth(depth+parent.getCommentDepth());
                        threaded.add(i+parent.getChildCount(), child);
                        removeComments.add(child);
                        continue;
                    }
                }
            }
            if (removeComments.size() > 0) {
                comments.removeAll(removeComments);
                removeComments.clear();
            }
        }
        return threaded;
    }


}
