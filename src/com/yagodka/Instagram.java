package com.yagodka;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class Instagram {

    private static final String HOST = "https://www.instagram.com";
    private static final String USER_AGENT = "Mozilla/5.0"; // ??? Может быть Убрать

    // Magic
    private static final String SESSION_ID = "IGSCa049f340599e0786ce9dc667e0ac297e78b583c4e152c074eebfb8bbbc7c6c76%3AnkzqGPR5d9J0S4cBJYK0fJwPiiPTnGtj%3A%7B%22_auth_user_id%22%3A5589309006%2C%22_auth_user_backend%22%3A%22accounts.backends.CaseInsensitiveModelBackend%22%2C%22_auth_user_hash%22%3A%22%22%2C%22_token_ver%22%3A2%2C%22_token%22%3A%225589309006%3AsquuQM4ZrwjpmaDov1VV8Mp7uhu1ZGqY%3Ac8e062c996ae2e58147928a9612b7fcd591985750117417bcceb654fb96d6701%22%2C%22_platform%22%3A4%2C%22asns%22%3A%7B%22time%22%3A1497368046%2C%2289.252.21.111%22%3A31148%7D%2C%22last_refreshed%22%3A1497367973.9828121662%7D;";

    private static final String REQUEST_USER_PROFILE = "/%s/?__a=1";
    // private static final String REQUEST_FOLLOWERS_20 = "/graphql/query/?query_id=17851374694183129&id=%s&first=20%s";
    private static final String REQUEST_FOLLOWERS_1000 = "/graphql/query/?query_id=17851374694183129&id=%s&first=1000%s";

    public static void main(String[] args) throws IOException {

        if (args.length < 1) return;

        String urlUserProfile = String.format(HOST + REQUEST_USER_PROFILE, args[0]);
        String response = executeRequest(urlUserProfile);
        String user_id = new JSONObject(response).getJSONObject("user").getString("id");

        boolean hasNextPage = true;
        String endCursor = "";

        while (hasNextPage) {
            String urlFollowers = String.format(HOST + REQUEST_FOLLOWERS_1000, user_id, "".equals(endCursor) ? "" : "&after=" + endCursor);
            String response2 = executeRequest(urlFollowers);

            JSONObject json = new JSONObject(response2)
                    .getJSONObject("data")
                    .getJSONObject("user")
                    .getJSONObject("edge_followed_by");

            JSONArray edges = json.getJSONArray("edges");

            for (int i = 0; i < edges.length(); i++) {
                JSONObject node = edges.getJSONObject(i).getJSONObject("node");
                System.out.println("id: " + node.getString("id") + "\t\tusername: " + node.getString("username"));
            }

            JSONObject pageInfo = json.getJSONObject("page_info");
            hasNextPage = pageInfo.getBoolean("has_next_page");
            endCursor = pageInfo.getString("end_cursor");
        }
    }

    private static String executeRequest(String url) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.addRequestProperty("Cookie", "sessionid=" + SESSION_ID);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
