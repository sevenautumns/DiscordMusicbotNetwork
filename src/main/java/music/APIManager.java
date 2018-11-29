package music;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import misc.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class APIManager {
    private static APIManager singleton;
    private final Properties APIKEYS;

    private APIManager(){
        APIKEYS = new Properties();
        Utility.loadProperties("apikeys.properties", APIKEYS);
    }

    public List<SearchResult> youtubeSearch(String query, int resultsNumber){
        List<SearchResult> results;
        YouTube youTube;

        try{
            youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest httpRequest) {
                }
            }).setApplicationName("Music Bot Search" + query).build();

            YouTube.Search.List search = youTube.search().list("id,snippet");
            search.setKey(APIKEYS.getProperty("youtube-key"));
            search.setQ(query);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults((long) resultsNumber);

            SearchListResponse searchListResponse = search.execute();
            results = searchListResponse.getItems();

            if(results == null){
                results = new ArrayList<>();
            }

            return results;
        }catch (Exception e){
            e.printStackTrace();

            results = new ArrayList<>();
            return results;
        }
    }

    public static APIManager getInstance(){
        if(singleton != null) return singleton;

        return createSingleton();
    }

    private static synchronized APIManager createSingleton(){
        if(singleton == null) singleton = new APIManager();
        return singleton;
    }
}
