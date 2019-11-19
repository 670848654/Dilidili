package anime.project.dilidili.api;

import anime.project.dilidili.application.DiliDili;

public class Api {
    public final static String SOURCE_1_API = "http://jx.618g.com/?url=";
    public final static String SOURCE_2_API = "http://player.jfrft.net/index.php?url=";
    public final static String SOURCE_3_API = "http://jx.yylep.com/?url=";
    public final static String SOURCE_4_API = "https://sg.hackmg.com/index.php?url=";
    public final static String SOURCE_5_API = "http://jqaaa.com/jx.php?url=";
    public final static String SOURCE_6_API = "http://jx.skyfollowsnow.pro/?url=";
    //百度站内搜索
    public final static String BAIDU_SEARCH_API = "http://zhannei.baidu.com/cse/site?q=%s&p=%s&nsid=&cc=%s";
    //新站点搜索
    public static String NEW_SEARCH_API = DiliDili.DOMAIN + "/search.php?keyword=%s&PageNo=%s";
    //检测更新
    public final static String CHECK_UPDATE = "https://api.github.com/repos/670848654/Dilidili/releases/latest";
}
