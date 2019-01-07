package anime.project.dilidili.bean;

public class HomeWekBean {
    private String title;
    private String img;
    private String url;
    private String drama;

    public HomeWekBean(String title, String img, String url, String drama){
        this.title = title;
        this.img = img;
        this.url = url;
        this.drama = drama;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDrama() {
        return drama;
    }

    public void setDrama(String drama) {
        this.drama = drama;
    }
}
